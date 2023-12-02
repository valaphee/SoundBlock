package com.valaphee.sb;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SoundBlock extends Block implements ITileEntityProvider {
    public static final PropertyBool POWERED_PROPERTY = PropertyBool.create("powered");
    public static final Block BLOCK = new SoundBlock();
    public static final Item ITEM = new ItemBlock(BLOCK).setRegistryName(BLOCK.getRegistryName());

    public SoundBlock() {
        super(Material.WOOD, MapColor.DIRT);

        setHardness(2.0f);
        setResistance(10.0f);
        setSoundType(SoundType.STONE);
        setUnlocalizedName("sound_block");
        setRegistryName("sound_block");
        setCreativeTab(CreativeTabs.REDSTONE);
        setDefaultState(blockState.getBaseState().withProperty(POWERED_PROPERTY, false));
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        IBlockState blockState = worldIn.getBlockState(pos);
        boolean powered = worldIn.isBlockPowered(pos);
        boolean wasPowered = blockState.getValue(POWERED_PROPERTY);
        if (powered) {
            if (!wasPowered) {
                SoundBlockData soundBlockData = (SoundBlockData) worldIn.getTileEntity(pos);

                worldIn.setBlockState(pos, blockState.withProperty(POWERED_PROPERTY, true));
                worldIn.setTileEntity(pos, soundBlockData);

                List<SoundBlockData.Sound> introSounds = soundBlockData.getIntro();
                for (SoundBlockData.Sound sound : introSounds) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(new SoundBlockSound(worldIn, pos, soundBlockData.getOffsetX(), soundBlockData.getOffsetY(), soundBlockData.getOffsetZ(), sound, false));
                }
                List<SoundBlockData.Sound> loopSounds = soundBlockData.getLoop();
                for (SoundBlockData.Sound sound : loopSounds) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(new SoundBlockSound(worldIn, pos, soundBlockData.getOffsetX(), soundBlockData.getOffsetY(), soundBlockData.getOffsetZ(), sound, true));
                }
            }
        } else if (wasPowered) {
            SoundBlockData soundBlockData = (SoundBlockData) worldIn.getTileEntity(pos);

            worldIn.setBlockState(pos, blockState.withProperty(POWERED_PROPERTY, false));
            worldIn.setTileEntity(pos, soundBlockData);

            List<SoundBlockData.Sound> outroSounds = soundBlockData.getOutro();
            for (SoundBlockData.Sound sound : outroSounds) {
                Minecraft.getMinecraft().getSoundHandler().playSound(new SoundBlockSound(worldIn, pos, soundBlockData.getOffsetX(), soundBlockData.getOffsetY(), soundBlockData.getOffsetZ(), sound, false));
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, POWERED_PROPERTY);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(POWERED_PROPERTY, (meta & 1) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        if (state.getValue(POWERED_PROPERTY)) {
            meta |= 1;
        }
        return meta;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new SoundBlockData();
    }
}
