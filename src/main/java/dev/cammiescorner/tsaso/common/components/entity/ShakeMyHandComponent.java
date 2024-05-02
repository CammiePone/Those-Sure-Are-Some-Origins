package dev.cammiescorner.tsaso.common.components.entity;

import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;

public class ShakeMyHandComponent implements AutoSyncedComponent {
	private final PlayerEntity player;
	private static final int MAX_TICKS = 7;
	private boolean handRaised;
	private long endTime;

	public ShakeMyHandComponent(PlayerEntity player) {
		this.player = player;
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		handRaised = tag.getBoolean("HandRaised");
		endTime = tag.getLong("EndTime");
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		tag.putBoolean("HandRaised", handRaised);
		tag.putLong("EndTime", endTime);
	}

	public boolean isHandRaised() {
		return handRaised;
	}

	public void setHandRaised(boolean handRaised) {
		this.handRaised = handRaised;

		if(handRaised)
			startTimer();
		else
			endTime = 0;

		player.syncComponent(ComponentRegistry.SHAKE_MY_HAND);
	}

	public long getTicks() {
		return player.getWorld().getTime() - endTime;
	}

	public float getHandShakeProgress(float delta) {
		return MathHelper.clamp(getTicks() + delta, 0, MAX_TICKS) / MAX_TICKS;
	}

	public void startTimer() {
		this.endTime = player.getWorld().getTime() + MAX_TICKS;
		player.syncComponent(ComponentRegistry.SHAKE_MY_HAND);
	}
}
