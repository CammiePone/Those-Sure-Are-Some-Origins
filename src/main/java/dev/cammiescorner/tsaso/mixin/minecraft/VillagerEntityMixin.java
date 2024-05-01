package dev.cammiescorner.tsaso.mixin.minecraft;

import dev.cammiescorner.tsaso.TSASO;
import dev.cammiescorner.tsaso.common.powers.DealmakerPower;
import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends LivingEntity {
	protected VillagerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	private void tsaso$makeADealWithVillager(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		if(player.getComponent(ComponentRegistry.MAKE_A_DEAL).initiateDeal(this) && PowerHolderComponent.hasPower(player, DealmakerPower.class) && player.getComponent(ComponentRegistry.SHAKE_MY_HAND).isHandRaised() && player.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
			TSASO.openMakeADealScreen(player, player.getComponent(ComponentRegistry.RANDOM_POWERS).viewPowerTypes());
			info.setReturnValue(ActionResult.SUCCESS);
		}
	}
}
