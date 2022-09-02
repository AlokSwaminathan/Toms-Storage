package com.tom.storagemod.gui;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import com.mojang.blaze3d.systems.RenderSystem;

public class StorageTerminalScreen extends AbstractStorageTerminalScreen<StorageTerminalMenu> {
	private static final Identifier gui = new Identifier("toms_storage", "textures/gui/storage_terminal.png");

	public StorageTerminalScreen(StorageTerminalMenu screenContainer, PlayerInventory inv, Text titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@Override
	protected void init() {
		backgroundWidth = 194;
		backgroundHeight = 202;
		rowCount = 5;
		super.init();
	}

	@Override
	protected void drawBackground(MatrixStack st, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, getGui());
		drawTexture(st, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
	}

	@Override
	public Identifier getGui() {
		return gui;
	}

	@Override
	public void render(MatrixStack st, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(st);
		super.render(st, mouseX, mouseY, partialTicks);
	}
}
