package dev.cammiescorner.tsaso.mixin.minecraft.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.cammiescorner.tsaso.common.powers.DealmakerPower;
import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HeldItemFeatureRenderer.class)
public class HeldItemFeatureRendererMixin {
	@Unique private final MinecraftClient client = MinecraftClient.getInstance();

	@WrapWithCondition(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/render/entity/feature/HeldItemFeatureRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;Lnet/minecraft/util/Arm;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
	))
	private boolean tsaso$hideItemInLeftHand(HeldItemFeatureRenderer<?, ?> owner, LivingEntity entity, ItemStack stack, ModelTransformationMode mode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
		return !(PowerHolderComponent.hasPower(entity, DealmakerPower.class) && entity.getComponent(ComponentRegistry.SHAKE_MY_HAND).isHandRaised() && client.options.getMainArm().get() == arm);
	}
}
