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
import net.minecraft.item.ItemStack;
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

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new SoundBlockData();
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        if (!worldIn.isRemote) {
            return true;
        }

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        return tileEntity != null && tileEntity.receiveClientEvent(id, param);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (!(tileEntity instanceof SoundBlockData)) {
            return;
        }

        // Ignore if alwaysPowered
        SoundBlockData data = (SoundBlockData) tileEntity;
        if (data.isAlwaysPowered()) {
            return;
        }

        // Broadcast an event when the powered state has changed
        boolean wasPowered = data.isPowered();
        boolean powered = worldIn.isBlockPowered(pos);
        if (powered != wasPowered) {
            if (powered) {
                data.setPowered(true);
                worldIn.addBlockEvent(pos, worldIn.getBlockState(pos).getBlock(), 0, 0);
            } else {
                data.setPowered(false);
                worldIn.addBlockEvent(pos, worldIn.getBlockState(pos).getBlock(), 0, 1);
            }
        }
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (!(tileEntity instanceof SoundBlockData)) {
            return;
        }

        SoundBlockData data = (SoundBlockData) tileEntity;
        data.setPowered(false); // Stop playing

        // Save data to item stack
        ItemStack itemStack = new ItemStack(SoundBlock.ITEM);
        itemStack.setTagInfo("BlockEntityTag", data.serializeNBT());
        spawnAsEntity(worldIn, pos, itemStack);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        // Load data from item stack
        ItemStack itemStack = super.getItem(worldIn, pos, state);
        itemStack.setTagInfo("BlockEntityTag", worldIn.getTileEntity(pos).serializeNBT());
        return itemStack;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        playerIn.openGui(Main.instance, GuiHandler.SOUND_BLOCK_EDIT, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
