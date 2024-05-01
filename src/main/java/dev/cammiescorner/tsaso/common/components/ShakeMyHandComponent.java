package dev.cammiescorner.tsaso.common.components;

import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class ShakeMyHandComponent implements AutoSyncedComponent {
	private final PlayerEntity player;
	private boolean handRaised;

	public ShakeMyHandComponent(PlayerEntity player) {
		this.player = player;
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		handRaised = tag.getBoolean("HandRaised");
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		tag.putBoolean("HandRaised", handRaised);
	}

	public boolean isHandRaised() {
		return handRaised;
	}

	public void setHandRaised(boolean handRaised) {
		this.handRaised = handRaised;
		player.syncComponent(ComponentRegistry.SHAKE_MY_HAND);
	}
}
