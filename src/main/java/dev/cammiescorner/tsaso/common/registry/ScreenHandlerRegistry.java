package dev.cammiescorner.tsaso.common.registry;

import dev.cammiescorner.tsaso.TSASO;
import dev.cammiescorner.tsaso.common.screens.MakeADealScreenHandler;
import dev.upcraft.sparkweave.api.registry.RegistryHandler;
import dev.upcraft.sparkweave.api.registry.RegistrySupplier;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;

public class ScreenHandlerRegistry {
	public static final RegistryHandler<ScreenHandlerType<?>> SCREEN_HANDLERS = RegistryHandler.create(RegistryKeys.SCREEN_HANDLER_TYPE, TSASO.MOD_ID);

	public static final RegistrySupplier<ScreenHandlerType<MakeADealScreenHandler>> MAKE_A_DEAL = SCREEN_HANDLERS.register("make_a_deal_screen_handler", () -> new ExtendedScreenHandlerType<>((syncId, inventory, buf) ->
			new MakeADealScreenHandler(syncId, buf.readList(byteBuf -> (PowerType<?>) PowerTypeRegistry.get(byteBuf.readIdentifier())))));
}
