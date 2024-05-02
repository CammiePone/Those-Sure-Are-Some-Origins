package dev.cammiescorner.tsaso.mixin.minecraft.client;

import dev.cammiescorner.tsaso.common.components.entity.ShakeMyHandComponent;
import dev.cammiescorner.tsaso.common.powers.DealmakerPower;
import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {
	@Shadow @Final public ModelPart leftArm;
	@Shadow @Final public ModelPart rightArm;

	@Unique private final MinecraftClient client = MinecraftClient.getInstance();

	@Inject(method = "positionLeftArm", at = @At("TAIL"))
	private void tsaso$shakeMyLeftHand(T entity, CallbackInfo ci) {
		if(entity instanceof PlayerEntity player && client.options.getMainArm().get() == Arm.LEFT) {
			ShakeMyHandComponent component = player.getComponent(ComponentRegistry.SHAKE_MY_HAND);

			if(PowerHolderComponent.hasPower(player, DealmakerPower.class) && component.isHandRaised())
				leftArm.pitch = MathHelper.lerp(component.getHandShakeProgress(client.getTickDelta()), leftArm.pitch, (float) Math.toRadians(-75));
		}
	}

	@Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
	private void tsaso$shakeMyRightHand(T entity, float f, float g, float h, float i, float j, CallbackInfo ci) {
		if(entity instanceof PlayerEntity player && client.options.getMainArm().get() == Arm.RIGHT) {
			ShakeMyHandComponent component = player.getComponent(ComponentRegistry.SHAKE_MY_HAND);

			if(PowerHolderComponent.hasPower(player, DealmakerPower.class) && component.isHandRaised())
				rightArm.pitch = MathHelper.lerp(component.getHandShakeProgress(client.getTickDelta()), rightArm.pitch, (float) Math.toRadians(-75));
		}
	}
}
