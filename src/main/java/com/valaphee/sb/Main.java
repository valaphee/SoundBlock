package com.valaphee.sb;

import lombok.AllArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
@Mod.EventBusSubscriber
public class Main {
    public static final String MODID = "vsb";
    public static final String NAME = "Valaphee's Sound Block";
    public static final String VERSION = "1.0";

    @Mod.Instance
    public static Main instance;
    public static Logger logger;

    public static SimpleNetworkWrapper network;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        network = NetworkRegistry.INSTANCE.newSimpleChannel("VSB");
        network.registerMessage(SoundBlockMessage.Handler.class, SoundBlockMessage.class, 0, Side.SERVER);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onRegisterItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(SoundBlock.ITEM);

        ModelLoader.setCustomModelResourceLocation(SoundBlock.ITEM, 0, new ModelResourceLocation(SoundBlock.ITEM.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    public static void onRegisterBlock(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(SoundBlock.BLOCK);

        GameRegistry.registerTileEntity(SoundBlockData.class, new ResourceLocation(MODID, "sound_block"));
    }

    private Map<BlockPos, LoopSounds> loopSoundsByBlock = new HashMap<>();

    public void playIntro(SoundBlockData soundBlockData) {
        SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();

        List<SoundBlockData.Sound> introSounds = soundBlockData.getIntro();
        for (SoundBlockData.Sound sound : introSounds) {
            soundHandler.playSound(new SoundBlockSound(soundBlockData, sound, false));
        }

        // Only allow delaying one sound group per block
        List<SoundBlockData.Sound> loopSounds = soundBlockData.getLoop();
        List<SoundBlockSound> loopSoundsFinalized = new ArrayList<>();
        for (SoundBlockData.Sound sound : loopSounds) {
            loopSoundsFinalized.add(new SoundBlockSound(soundBlockData, sound, true));
        }
        loopSoundsByBlock.put(soundBlockData.getPos(), new LoopSounds(loopSoundsFinalized, soundBlockData.getLoopDelay()));
    }

    public void continueLoop(SoundBlockData soundBlockData) {
        loopSoundsByBlock.remove(soundBlockData.getPos());

        SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
        List<SoundBlockData.Sound> loopSounds = soundBlockData.getLoop();
        for (SoundBlockData.Sound sound : loopSounds) {
            soundHandler.playSound(new SoundBlockSound(soundBlockData, sound, true));
        }
    }

    public void playOutro(SoundBlockData soundBlockData) {
        loopSoundsByBlock.remove(soundBlockData.getPos());

        SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
        List<SoundBlockData.Sound> outroSounds = soundBlockData.getOutro();
        for (SoundBlockData.Sound sound : outroSounds) {
            soundHandler.playSound(new SoundBlockSound(soundBlockData, sound, false));
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();

        // Check if delayed sound can be played
        Iterator<Map.Entry<BlockPos, LoopSounds>> delayedSoundIterator = loopSoundsByBlock.entrySet().iterator();
        while (delayedSoundIterator.hasNext()) {
            Map.Entry<BlockPos, LoopSounds> delayedSound = delayedSoundIterator.next();
            if (delayedSound.getValue().remainingTicks != 0) {
                delayedSound.getValue().remainingTicks--;
                continue;
            }
            delayedSoundIterator.remove();

            for (SoundBlockSound sound : delayedSound.getValue().sounds) {
                soundHandler.playSound(sound);
            }
        }
    }

    @AllArgsConstructor
    private static class LoopSounds {
        private List<SoundBlockSound> sounds;
        private int remainingTicks;
    }
}
