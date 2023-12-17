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

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        SoundBlockData soundBlockData = (SoundBlockData) worldIn.getTileEntity(pos);

        boolean powered = worldIn.isBlockPowered(pos);
        if (powered) {
            if (!soundBlockData.isPowered()) {
                soundBlockData.setPowered(true);

                Block soundBlock = worldIn.getBlockState(pos).getBlock();
                worldIn.addBlockEvent(pos, soundBlock, 0, 0);
            }
        } else if (soundBlockData.isPowered()) {
            soundBlockData.setPowered(false);

            Block soundBlock = worldIn.getBlockState(pos).getBlock();
            worldIn.addBlockEvent(pos, soundBlock, 0, 1);
        }
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        if (!worldIn.isRemote) {
            return true;
        }

        if (id == 0) {
            if (param == 0) {
                SoundBlockData soundBlockData = (SoundBlockData) worldIn.getTileEntity(pos);
                soundBlockData.setPowered(true);
                Main.instance.playIntro(soundBlockData);
            } else if (param == 1) {
                SoundBlockData soundBlockData = (SoundBlockData) worldIn.getTileEntity(pos);
                soundBlockData.setPowered(false);
                Main.instance.playOutro(soundBlockData);
            }
        }

        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new SoundBlockData();
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        // Can't keep its powered state and stops the looping
        SoundBlockData soundBlockData = (SoundBlockData) worldIn.getTileEntity(pos);
        soundBlockData.setPowered(false);

        ItemStack itemStack = new ItemStack(SoundBlock.ITEM);
        itemStack.setTagInfo("BlockEntityTag", soundBlockData.serializeNBT());
        spawnAsEntity(worldIn, pos, itemStack);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        ItemStack itemStack = super.getItem(worldIn, pos, state);
        itemStack.setTagInfo("BlockEntityTag", worldIn.getTileEntity(pos).serializeNBT());
        return itemStack;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        playerIn.openGui(Main.instance, GuiHandler.SOUND_BLOCK_EDIT, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return false;
    }
}
