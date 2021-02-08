package com.tom.storagemod.tile;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import com.tom.storagemod.StorageMod;

public class TileEntityInventoryCableConnector extends TileEntityInventoryCableConnectorBase {
	public TileEntityInventoryCableConnector(BlockPos pos, BlockState state) {
		super(StorageMod.invCableConnectorTile, pos, state);
	}
}
