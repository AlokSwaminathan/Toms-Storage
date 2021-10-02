package com.tom.storagemod.block;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraftforge.common.ToolType;

import com.tom.storagemod.proxy.ClientProxy;
import com.tom.storagemod.tile.TileEntityInventoryConnector;

public class InventoryConnector extends ContainerBlock implements IInventoryCable {

	public InventoryConnector() {
		super(Block.Properties.of(Material.WOOD).strength(3).harvestTool(ToolType.AXE));
		setRegistryName("ts.inventory_connector");
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityInventoryConnector();
	}

	@Override
	public BlockRenderType getRenderShape(BlockState p_149645_1_) {
		return BlockRenderType.MODEL;
	}

	@Override
	public void appendHoverText(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip,
			ITooltipFlag flagIn) {
		ClientProxy.tooltip("inventory_connector", tooltip);
	}

	@Override
	public List<BlockPos> next(World world, BlockState state, BlockPos pos) {
		return Collections.emptyList();
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if(!worldIn.isClientSide) {
			TileEntity tile = worldIn.getBlockEntity(pos);
			if(tile instanceof TileEntityInventoryConnector) {
				TileEntityInventoryConnector te = (TileEntityInventoryConnector) tile;
				player.displayClientMessage(new TranslationTextComponent("chat.toms_storage.inventory_connector.free_slots", te.getFreeSlotCount(), te.getInvSize()), true);
			}
		}
		return ActionResultType.SUCCESS;
	}
}
