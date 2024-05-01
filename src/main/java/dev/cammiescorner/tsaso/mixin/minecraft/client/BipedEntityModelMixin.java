package dev.cammiescorner.tsaso.mixin.minecraft.client;

import dev.cammiescorner.tsaso.common.powers.DealmakerPower;
import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import dev.cammiescorner.tsaso.common.utils.ShakeMyHand;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
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
	@Unique private final int maxTicksToShake = 7;

	@Inject(method = "positionLeftArm", at = @At("TAIL"))
	private void tsaso$shakeMyLeftHand(T entity, CallbackInfo ci) {
		if(client.options.getMainArm().get() == Arm.LEFT && entity instanceof ShakeMyHand a) {
			if(PowerHolderComponent.hasPower(entity, DealmakerPower.class) && entity.getComponent(ComponentRegistry.SHAKE_MY_HAND).isHandRaised()) {
				leftArm.pitch = MathHelper.lerp(Math.min(a.tsaso$getTicks() + client.getTickDelta(), maxTicksToShake) / maxTicksToShake, leftArm.pitch, (float) Math.toRadians(-75));
				a.tsaso$setTicks(a.tsaso$getTicks() + 1);
			}
			else
				a.tsaso$setTicks(0);
		}
	}

	@Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
	private void tsaso$shakeMyRightHand(T entity, float f, float g, float h, float i, float j, CallbackInfo ci) {
		if(client.options.getMainArm().get() == Arm.RIGHT && entity instanceof ShakeMyHand a) {
			if(PowerHolderComponent.hasPower(entity, DealmakerPower.class) && entity.getComponent(ComponentRegistry.SHAKE_MY_HAND).isHandRaised()) {
				rightArm.pitch = MathHelper.lerp(Math.min(a.tsaso$getTicks() + client.getTickDelta(), maxTicksToShake) / maxTicksToShake, rightArm.pitch, (float) Math.toRadians(-75));
				a.tsaso$setTicks(a.tsaso$getTicks() + 1);
			}
			else
				a.tsaso$setTicks(0);
		}
	}
}
