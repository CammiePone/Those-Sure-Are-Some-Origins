package dev.cammiescorner.tsaso.mixin.minecraft.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.cammiescorner.tsaso.common.powers.DealmakerPower;
import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
	@Shadow @Final private MinecraftClient client;

	@WrapOperation(method = "renderFirstPersonItem", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
			ordinal = 0
	))
	private boolean tsaso$shakeMyHand(ItemStack instance, Operation<Boolean> original, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand) {
		return original.call(instance) || (PowerHolderComponent.hasPower(player, DealmakerPower.class) && player.getComponent(ComponentRegistry.SHAKE_MY_HAND).isHandRaised() && player.getMainHandStack().equals(instance));
	}
}
