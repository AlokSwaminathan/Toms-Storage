package com.tom.storagemod.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class DataPacket {
	public CompoundNBT tag;

	public DataPacket(CompoundNBT tag) {
		this.tag = tag;
	}

	public DataPacket(PacketBuffer pb) {
		tag = pb.func_244273_m();
	}

	public void toBytes(PacketBuffer pb) {
		pb.writeCompoundTag(tag);
	}
}
