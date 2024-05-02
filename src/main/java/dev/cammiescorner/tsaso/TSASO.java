package dev.cammiescorner.tsaso;

import dev.cammiescorner.tsaso.common.components.entity.LastDeathPlayerComponent;
import dev.cammiescorner.tsaso.common.components.entity.PowerSourcesComponent;
import dev.cammiescorner.tsaso.common.components.level.LastDeathLevelComponent;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.entity.event.api.LivingEntityDeathCallback;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;
import java.util.UUID;

public class TSASO implements ModInitializer {
	public static final String MOD_ID = "tsaso";

	/*
	 * TODO all of a sudden, these are working without issue? keep an eye on them
	 *  - fix no origin screen
	 *  - fix disconnecting faeborn when they attack entities
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
				for(ServerPlayerEntity player : PlayerLookup.all(dealmaker.server))
					removeAllPowers(player, dealmaker.getUuid(), dealmaker.server);

				LastDeathLevelComponent lastDeathLevelComponent = dealmaker.server.getScoreboard().getComponent(ComponentRegistry.LAST_DEATH_LEVEL);
				lastDeathLevelComponent.setLastDeathTime(dealmaker.getUuid(), dealmaker.getWorld().getTime());
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.player;

			LastDeathPlayerComponent lastDeathPlayerComponent = player.getComponent(ComponentRegistry.LAST_DEATH_PLAYER);
			LastDeathLevelComponent lastDeathLevelComponent = server.getScoreboard().getComponent(ComponentRegistry.LAST_DEATH_LEVEL);

			for(UUID uuid : lastDeathLevelComponent.viewLastDeaths().keySet()) {
				long deathTimeLevel = lastDeathLevelComponent.getLastDeathTime(uuid);

				if(!lastDeathPlayerComponent.viewLastDeaths().containsKey(uuid)) {
					lastDeathPlayerComponent.setLastDeathTime(uuid, deathTimeLevel);
					continue;
				}

				long deathTimePlayer = lastDeathPlayerComponent.getLastDeathTime(uuid);

				if(deathTimeLevel > deathTimePlayer)
					removeAllPowers(player, uuid, server);
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

	public static void removeAllPowers(PlayerEntity player, UUID sourceId, MinecraftServer server) {
		PowerHolderComponent powerComponent = player.getComponent(PowerHolderComponent.KEY);
		PowerSourcesComponent sourcesComponent = player.getComponent(ComponentRegistry.POWER_SOURCES);
		LastDeathPlayerComponent lastDeathPlayerComponent = player.getComponent(ComponentRegistry.LAST_DEATH_PLAYER);
		Identifier id = TSASO.id(sourceId.toString());

		powerComponent.removeAllPowersFromSource(id);
		powerComponent.sync();
		sourcesComponent.removeSource(id);
		lastDeathPlayerComponent.setLastDeathTime(sourceId, player.getWorld().getTime());
	}
}
