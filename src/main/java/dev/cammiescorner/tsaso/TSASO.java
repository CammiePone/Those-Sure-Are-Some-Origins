package dev.cammiescorner.tsaso;

import dev.cammiescorner.tsaso.common.components.PowerSourcesComponent;
import dev.cammiescorner.tsaso.common.packets.ChosenDealPacket;
import dev.cammiescorner.tsaso.common.packets.CloseDealPacket;
import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import dev.cammiescorner.tsaso.common.registry.PowerRegistry;
import dev.cammiescorner.tsaso.common.registry.ScreenHandlerRegistry;
import dev.cammiescorner.tsaso.common.screens.MakeADealScreenHandler;
import dev.upcraft.sparkweave.api.registry.RegistryService;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.entity.event.api.LivingEntityDeathCallback;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;

public class TSASO implements ModInitializer {
	public static final String MOD_ID = "tsaso";

	/*
	 * TODO
	 *  - fix no origin screen
	 *  - fix hand not raising for other players in multiplayer
	 *  - fix hp not going to correct value upon death or relog
	 */

	@Override
	public void onInitialize(ModContainer mod) {
		RegistryService registryService = RegistryService.get();

		ScreenHandlerRegistry.SCREEN_HANDLERS.accept(registryService);
		PowerRegistry.register();

		ServerPlayNetworking.registerGlobalReceiver(ChosenDealPacket.ID, ChosenDealPacket::handler);
		ServerPlayNetworking.registerGlobalReceiver(CloseDealPacket.ID, CloseDealPacket::handler);

		LivingEntityDeathCallback.EVENT.register((entity, source) -> {
			if(entity instanceof ServerPlayerEntity dealmaker) {
				Identifier sourceId = TSASO.id(dealmaker.getUuidAsString());

				for(ServerPlayerEntity player : PlayerLookup.all(dealmaker.server)) {
					PowerHolderComponent powerComponent = player.getComponent(PowerHolderComponent.KEY);
					PowerSourcesComponent component = player.getComponent(ComponentRegistry.POWER_SOURCES);

					// TODO affect offline players too
					powerComponent.removeAllPowersFromSource(sourceId);
					powerComponent.sync();
					component.removeSource(sourceId);
				}
			}
		});
	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}

	public static MutableText translate(@Nullable String prefix, String... value) {
		return Text.translatable(translationKey(prefix, value));
	}

	public static String translationKey(@Nullable String prefix, String... value) {
		String translationKey = MOD_ID + "." + String.join(".", value);
		return prefix != null ? (prefix + "." + translationKey) : translationKey;
	}

	public static void openMakeADealScreen(PlayerEntity player, List<PowerType<?>> powerTypes) {
		player.getComponent(ComponentRegistry.MAKE_A_DEAL).setHasChosenDeal(false);
		player.getComponent(ComponentRegistry.SHAKE_MY_HAND).setHandRaised(false);

		player.openHandledScreen(new ExtendedScreenHandlerFactory() {
			@Override
			public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
				buf.writeCollection(powerTypes, (byteBuf, power) -> byteBuf.writeIdentifier(power.getIdentifier()));
			}

			@Override
			public Text getDisplayName() {
				return TSASO.translate("screen", "origins", "make_a_deal");
			}

			@Override
			public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
				return new MakeADealScreenHandler(i, powerTypes);
			}
		});
	}
}
