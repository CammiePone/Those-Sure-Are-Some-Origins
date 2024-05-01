package dev.cammiescorner.tsaso.common.packets;

import dev.cammiescorner.tsaso.TSASO;
import dev.cammiescorner.tsaso.common.components.MakeADealComponent;
import dev.cammiescorner.tsaso.common.components.PowerSourcesComponent;
import dev.cammiescorner.tsaso.common.components.ShakeMyHandComponent;
import dev.cammiescorner.tsaso.common.powers.DealmakerPower;
import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import dev.upcraft.sparkweave.api.util.scheduler.Tasks;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class CloseDealPacket {
	public static final Identifier ID = TSASO.id("close_deal");

	public static void send(PowerType<?> powerType) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeIdentifier(powerType.getIdentifier());
		ClientPlayNetworking.send(ID, buf);
	}

	public static void handler(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
		Identifier id = buf.readIdentifier();

		server.execute(() -> {
			PowerType<?> powerType = PowerTypeRegistry.get(id);
			PowerHolderComponent powerComponent = player.getComponent(PowerHolderComponent.KEY);
			PowerSourcesComponent sourcesComponent = player.getComponent(ComponentRegistry.POWER_SOURCES);
			MakeADealComponent dealComponent = player.getComponent(ComponentRegistry.MAKE_A_DEAL);
			ShakeMyHandComponent shakeComponent = player.getComponent(ComponentRegistry.SHAKE_MY_HAND);
			Identifier sourceId = TSASO.id(PowerHolderComponent.hasPower(player, DealmakerPower.class) ? player.getUuidAsString() : dealComponent.getOtherPlayer().getUuidAsString());

			Tasks.scheduleEphemeral(() -> {
				powerComponent.addPower(powerType, sourceId);
				powerComponent.sync();
				sourcesComponent.addSource(sourceId);
				dealComponent.setHasChosenDeal(false);
				shakeComponent.setHandRaised(false);

				if(dealComponent.getType() == MakeADealComponent.Type.VILLAGER)
					player.removeStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE);
			}, 10);
		});
	}
}
