package dev.cammiescorner.tsaso.client.gui.screens;

import dev.cammiescorner.tsaso.TSASO;
import dev.cammiescorner.tsaso.common.components.MakeADealComponent;
import dev.cammiescorner.tsaso.common.packets.ChosenDealPacket;
import dev.cammiescorner.tsaso.common.packets.CloseDealPacket;
import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import dev.cammiescorner.tsaso.common.screens.MakeADealScreenHandler;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.origins.Origins;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class MakeADealScreen extends HandledScreen<MakeADealScreenHandler> {
	private static final Identifier TEXTURE = new Identifier(Origins.MODID, "textures/gui/choose_origin.png");
	private final int texWidth = 176, texHeight = 182;
	private final Text trueTitle;
	private MutableText message = TSASO.translate("screen", "origins", "accept_deal");
	private ButtonWidget acceptDeal, cycleRight, cycleLeft;
	private int index = 0;

	public MakeADealScreen(MakeADealScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, Text.empty());
		this.trueTitle = title;
	}

	@Override
	protected void init() {
		super.init();
		x = (width - texWidth) / 2;
		y = (height - texHeight) / 2;
		playerInventoryTitleX = Integer.MAX_VALUE;
		playerInventoryTitleY = Integer.MAX_VALUE;

		// accept deal button
		int textWidth = textRenderer.getWidth(message) + 16;

		acceptDeal = addDrawableChild(ButtonWidget.builder(message, buttonWidget -> {
			ChosenDealPacket.send();
		}).position(width / 2 - textWidth / 2, y + 184).size(textWidth, 20).build());

		// cycle power options buttons
		cycleRight = addDrawableChild(ButtonWidget.builder(Text.of(">"), buttonWidget -> {
			if(index + 1 >= handler.viewPowerTypes().size())
				index = 0;
			else
				index++;
		}).position(x + texWidth + 8, y + texHeight / 2 - 10).size(20, 20).build());
		cycleLeft = addDrawableChild(ButtonWidget.builder(Text.of("<"), buttonWidget -> {
			if(index - 1 < 0)
				index = handler.viewPowerTypes().size() - 1;
			else
				index--;
		}).position(x - 28, y + texHeight / 2 - 10).size(20, 20).build());
	}

	@Override
	protected void drawBackground(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
		renderBackground(graphics);

		// draw fnuuy background because why wouldnt it just be part of the main texture
		renderWindowBackground(graphics);

		// draw main textures
		graphics.drawTexture(TEXTURE, x, y, 0, 0, texWidth, texHeight);
	}

	@Override
	protected void drawForeground(GuiGraphics graphics, int mouseX, int mouseY) {
		// draw title text
		graphics.drawCenteredShadowedText(textRenderer, trueTitle, x + texWidth / 2, y + 19, 0xffffff);

		if(index >= 0 && index < handler.viewPowerTypes().size()) {
			PowerType<?> powerType = handler.viewPowerTypes().get(index);
			int textHeight = textRenderer.getWrappedHeight(powerType.getName(), 144) + textRenderer.getWrappedHeight(powerType.getDescription(), 144);

			graphics.drawTextWrapped(textRenderer, powerType.getName().formatted(Formatting.UNDERLINE), x + 16, y + texHeight / 2 - textHeight / 2, 144, 0xffffff);
			graphics.drawTextWrapped(textRenderer, powerType.getDescription(), x + 18, y + 12 + texHeight / 2 - textHeight / 2, 144, 0xcccccc);

			graphics.drawCenteredShadowedText(textRenderer, (index + 1) + "/" + handler.viewPowerTypes().size(), x + texWidth / 2, y + texHeight - 24, 0xcccccc);
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		if(client == null || client.player == null || client.world == null)
			return;

		MakeADealComponent component = client.player.getComponent(ComponentRegistry.MAKE_A_DEAL);

		// grey out the accept button while waiting for other player
		if(!component.canCompleteDeal()) {
			acceptDeal.active = !component.hasChosenDeal();
			cycleRight.active = !component.hasChosenDeal();
			cycleLeft.active = !component.hasChosenDeal();
		}

		drawBackground(graphics, delta, mouseX, mouseY);
		drawForeground(graphics, mouseX, mouseY);
		renderWidgets(graphics, mouseX, mouseY, delta);

		// send packet to complete the deal and close screen
		if(component.hasChosenDeal() && component.canCompleteDeal()) {
			CloseDealPacket.send(handler.viewPowerTypes().get(index));
			closeScreen();
		}
	}

	private void renderWidgets(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		for(Drawable drawable : drawables)
			drawable.render(graphics, mouseX, mouseY, delta);

		MakeADealComponent component = client.player.getComponent(ComponentRegistry.MAKE_A_DEAL);

		if(component.hasChosenDeal() && !component.canCompleteDeal()) {
			message = TSASO.translate("screen", "origins", "waiting_on_other_player");
			int textWidth = textRenderer.getWidth(message) + 16;

			acceptDeal.setMessage(message);
			acceptDeal.setX(width / 2 - textWidth / 2);
			acceptDeal.setWidth(textWidth);
		}
	}

	private void renderWindowBackground(GuiGraphics graphics) {
		int border = 13;
		int endX = x + 176 - border;
		int endY = y + 182 - border;

		for(int x1 = x; x1 < endX; x1 += 16)
			for(int y1 = y + 16; y1 < endY; y1 += 16)
				graphics.drawTexture(TEXTURE, x1, y1, 176, 0, Math.max(16, endX - x1), Math.max(16, endY - y1));
	}
}
