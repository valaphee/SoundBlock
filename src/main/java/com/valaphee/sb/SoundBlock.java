package com.valaphee.sb;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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
        setCreativeTab(CreativeTabs.REDSTONE);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        boolean powered = worldIn.isBlockPowered(pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        boolean powered = worldIn.isBlockPowered(pos);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new SoundBlockData();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        playerIn.openGui(Main.instance, GuiHandler.SOUND_BLOCK_EDIT, worldIn, pos.getX(), pos.getY(), pos.getZ());

        return false;
    }
}
