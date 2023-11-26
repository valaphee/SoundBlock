package com.valaphee.sb.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SoundBlock extends Block implements ITileEntityProvider {
    public static final Block BLOCK = new SoundBlock();
    public static final Item ITEM = new ItemBlock(BLOCK).setRegistryName(BLOCK.getRegistryName());

    public SoundBlock() {
        super(Material.WOOD, MapColor.DIRT);

        setHardness(2.0f);
        setResistance(10.0f);
        setSoundType(SoundType.STONE);
        setUnlocalizedName("sound_block");
        setRegistryName("sound_block");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new SoundBlockEntity();
    }

    public static class SoundBlockEntity extends TileEntity {
        @Override
        public void readFromNBT(NBTTagCompound compound) {
            super.readFromNBT(compound);
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            super.writeToNBT(compound);
            return compound;
        }
    }
}
