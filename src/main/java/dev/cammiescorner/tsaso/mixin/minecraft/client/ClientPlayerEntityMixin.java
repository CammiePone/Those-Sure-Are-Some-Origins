package dev.cammiescorner.tsaso.mixin.minecraft.client;

import dev.cammiescorner.tsaso.common.utils.ShakeMyHand;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements ShakeMyHand {
	@Unique private int ticks = 0;

	@Override
	public int tsaso$getTicks() {
		return ticks;
	}

	@Override
	public void tsaso$setTicks(int ticks) {
		this.ticks = ticks;
	}
}
