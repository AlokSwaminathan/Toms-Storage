package com.tom.storagemod.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import com.tom.storagemod.Content;
import com.tom.storagemod.components.ConfiguratorComponent;
import com.tom.storagemod.inventory.BlockFilter;
import com.tom.storagemod.item.IItemFilter;
import com.tom.storagemod.menu.slot.ItemFilterSlot;
import com.tom.storagemod.util.DataSlots;
import com.tom.storagemod.util.Priority;

public class InventoryConfiguratorMenu extends AbstractContainerMenu {
	private BlockPos pos;
	private BlockFilter f;
	private InteractionHand hand;
	public Direction side = Direction.DOWN;
	public Priority priority = Priority.NORMAL;
	public boolean skip, keepLast;
	private boolean isClosed = false;

	public InventoryConfiguratorMenu(int wid, Inventory pinv) {
		this(wid, pinv, null, null, null);
	}

	public InventoryConfiguratorMenu(int wid, Inventory pinv, BlockPos te, BlockFilter f, InteractionHand hand) {
		super(Content.invConfigMenu.get(), wid);
		this.pos = te;
		this.f = f;
		this.hand = hand;

		Container inv = f == null ? new SimpleContainer(1) : f.filter;

		addSlot(new ItemFilterSlot(inv, 0, 80, 32));

		for(int k = 0; k < 3; ++k) {
			for(int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(pinv, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}

		for(int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(pinv, l, 8 + l * 18, 142));
		}

		addDataSlot(DataSlots.create(v -> priority = Priority.VALUES[v % Priority.VALUES.length], () -> f.getPriority().ordinal()));
		addDataSlot(DataSlots.create(v -> side = Direction.from3DDataValue(v), () -> f.getSide().ordinal()));
		addDataSlot(DataSlots.create(v -> skip = v != 0, () -> f.skip() ? 1 : 0));
		addDataSlot(DataSlots.create(v -> keepLast = v != 0, () -> f.isKeepLast() ? 1 : 0));
	}

	@Override
	public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return (pos == null || player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 256) && !isClosed;
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		int mode = id & 0b111;
		int arg = id >> 3;
		switch (mode) {
		case 0:
			f.setPriority(Priority.VALUES[arg % Priority.VALUES.length]);
			break;

		case 1:
		{
			ItemStack is = player.getItemInHand(hand);
			if (is.is(Content.invConfig.get())) {
				var c = new ConfiguratorComponent.Configurator(is);
				c.startSelection(pos, f.getConnectedBlocks());
				isClosed = true;
				player.displayClientMessage(Component.translatable("tooltip.toms_storage.inventory_configurator.selection"), true);
			}
		}
		break;

		case 2:
		{
			f.getConnectedBlocks().clear();
			f.getConnectedBlocks().add(f.getMainPos());
			ItemStack is = player.getItemInHand(hand);
			if (is.is(Content.invConfig.get())) {
				var c = new ConfiguratorComponent.Configurator(is);
				c.setSelection(f.getConnectedBlocks());
			}
		}
		break;

		case 3:
			f.setSide(Direction.from3DDataValue(arg));
			break;

		case 4:
			f.setSkip(arg != 0);
			break;

		case 5:
			f.setKeepLast(arg != 0);
			break;

		case 6:
		{
			if (arg >= 0 && arg < slots.size()) {
				Slot s = slots.get(arg);
				if (s instanceof ItemFilterSlot && s.getItem().getItem() instanceof IItemFilter f) {
					f.openGui(s.getItem(), player, () -> s.getItem().getItem() == f);
				}
			}
		}
		break;

		default:
			break;
		}
		return true;
	}
}
