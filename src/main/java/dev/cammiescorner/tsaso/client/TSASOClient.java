package dev.cammiescorner.tsaso.client;

import dev.cammiescorner.tsaso.client.gui.screens.MakeADealScreen;
import dev.cammiescorner.tsaso.common.registry.ScreenHandlerRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class TSASOClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		HandledScreens.register(ScreenHandlerRegistry.MAKE_A_DEAL.get(), MakeADealScreen::new);
	}
}
