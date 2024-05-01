package dev.cammiescorner.tsaso.mixin.minecraft;

import dev.cammiescorner.tsaso.TSASO;
import dev.cammiescorner.tsaso.common.powers.DealmakerPower;
import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	@Unique private final PlayerEntity self = (PlayerEntity) (Object) this;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void tsaso$makeADeal(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		if(entity instanceof PlayerEntity otherPlayer && otherPlayer.getComponent(ComponentRegistry.MAKE_A_DEAL).initiateDeal(this) && getComponent(ComponentRegistry.MAKE_A_DEAL).initiateDeal(otherPlayer) && otherPlayer.getComponent(ComponentRegistry.SHAKE_MY_HAND).isHandRaised() && PowerHolderComponent.hasPower(otherPlayer, DealmakerPower.class)) {
			// open screen for me
			TSASO.openMakeADealScreen(self, getComponent(ComponentRegistry.RANDOM_POWERS).viewPowerTypes());

			// open screen for faeborn
			TSASO.openMakeADealScreen(otherPlayer, new ArrayList<>(getComponent(PowerHolderComponent.KEY).getPowerTypes(false)));

			info.setReturnValue(ActionResult.SUCCESS);
		}
	}
}
