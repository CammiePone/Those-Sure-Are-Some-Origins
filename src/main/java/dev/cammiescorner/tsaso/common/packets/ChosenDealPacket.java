package dev.cammiescorner.tsaso.common.packets;

import dev.cammiescorner.tsaso.TSASO;
import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class ChosenDealPacket {
	public static final Identifier ID = TSASO.id("chosen_deal");

	public static void send() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		ClientPlayNetworking.send(ID, buf);
	}

	public static void handler(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
		server.execute(() -> player.getComponent(ComponentRegistry.MAKE_A_DEAL).setHasChosenDeal(true));
	}
}
