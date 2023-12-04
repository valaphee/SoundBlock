package com.valaphee.sb;

import lombok.AllArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
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

    public Main() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
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

    private Map<BlockPos, DelayedSounds> delayedSounds = new HashMap<>();

    public void playIntro(SoundBlockData soundBlockData) {
        List<SoundBlockData.Sound> introSounds = soundBlockData.getIntro();
        for (SoundBlockData.Sound sound : introSounds) {
            Minecraft.getMinecraft().getSoundHandler().playSound(new SoundBlockSound(soundBlockData, sound, false));
        }

        // Only allow delaying one sound group per block
        List<SoundBlockData.Sound> loopSounds = soundBlockData.getLoop();
        List<SoundBlockSound> loopSoundsFinalized = new ArrayList<>();
        for (SoundBlockData.Sound sound : loopSounds) {
            loopSoundsFinalized.add(new SoundBlockSound(soundBlockData, sound, true));
        }
        DelayedSounds previousDelayedSounds = this.delayedSounds.put(soundBlockData.getPos(), new DelayedSounds(loopSoundsFinalized, soundBlockData.getLoopDelay()));
        if (previousDelayedSounds != null) {
            for (SoundBlockSound sound : previousDelayedSounds.sounds) {
                Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
            }
        }
    }

    public void continueLoop(SoundBlockData soundBlockData) {
        if (this.delayedSounds.containsKey(soundBlockData.getPos())) {
            return;
        }

        // Only allow delaying one sound group per block
        List<SoundBlockData.Sound> loopSounds = soundBlockData.getLoop();
        List<SoundBlockSound> loopSoundsFinalized = new ArrayList<>();
        for (SoundBlockData.Sound sound : loopSounds) {
            loopSoundsFinalized.add(new SoundBlockSound(soundBlockData, sound, true));
        }
        this.delayedSounds.put(soundBlockData.getPos(), new DelayedSounds(loopSoundsFinalized, 40));
    }

    public void playOutro(SoundBlockData soundBlockData) {
        List<SoundBlockData.Sound> outroSounds = soundBlockData.getOutro();
        for (SoundBlockData.Sound sound : outroSounds) {
            Minecraft.getMinecraft().getSoundHandler().playSound(new SoundBlockSound(soundBlockData, sound, false));
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        // Check if delayed sound can be played
        Iterator<Map.Entry<BlockPos, DelayedSounds>> delayedSoundIterator = delayedSounds.entrySet().iterator();
        while (delayedSoundIterator.hasNext()) {
            Map.Entry<BlockPos, DelayedSounds> delayedSound = delayedSoundIterator.next();
            if (delayedSound.getValue().remainingTicks != 0) {
                delayedSound.getValue().remainingTicks--;
                continue;
            }
            delayedSoundIterator.remove();

            for (SoundBlockSound sound : delayedSound.getValue().sounds) {
                Minecraft.getMinecraft().getSoundHandler().playSound(sound);
            }
        }
    }

    @AllArgsConstructor
    private static class DelayedSounds {
        private List<SoundBlockSound> sounds;
        private int remainingTicks;
    }
}
