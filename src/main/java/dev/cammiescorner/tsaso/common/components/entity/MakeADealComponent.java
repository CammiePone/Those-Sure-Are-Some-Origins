package dev.cammiescorner.tsaso.common.components.entity;

import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Util;

import java.util.UUID;

public class MakeADealComponent implements AutoSyncedComponent {
	private final PlayerEntity player;
	private UUID otherPlayerId = Util.NIL_UUID;
	private Type type = Type.NONE;
	private boolean hasChosenDeal = false;

	public MakeADealComponent(PlayerEntity player) {
		this.player = player;
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		otherPlayerId = tag.getUuid("OtherPlayerId");
		type = Type.valueOf(tag.getString("Type"));
		hasChosenDeal = tag.getBoolean("HasChosenDeal");
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		tag.putUuid("OtherPlayerId", otherPlayerId);
		tag.putString("Type", type.toString());
		tag.putBoolean("HasChosenDeal", hasChosenDeal);
	}

	public boolean hasChosenDeal() {
		return hasChosenDeal;
	}

	public void setHasChosenDeal(boolean hasChosenDeal) {
		this.hasChosenDeal = hasChosenDeal;
		player.syncComponent(ComponentRegistry.MAKE_A_DEAL);
	}

	public boolean initiateDeal(LivingEntity entity) {
		otherPlayerId = Util.NIL_UUID;
		type = Type.NONE;

		if(entity instanceof PlayerEntity) {
			otherPlayerId = entity.getUuid();
			type = Type.PLAYER;
		}
		else if(entity instanceof VillagerEntity) {
			type = Type.VILLAGER;
		}

		player.syncComponent(ComponentRegistry.MAKE_A_DEAL);
		return true;
	}

	public Type getType() {
		return type;
	}

	public PlayerEntity getOtherPlayer() {
		return player.getWorld().getPlayerByUuid(otherPlayerId);
	}

	public boolean canCompleteDeal() {
		PlayerEntity otherPlayer = player.getWorld().getPlayerByUuid(otherPlayerId);

		if(type == Type.VILLAGER)
			return true;

		return otherPlayer != null && otherPlayer.getComponent(ComponentRegistry.MAKE_A_DEAL).hasChosenDeal() && hasChosenDeal();
	}

	public enum Type {
		NONE, PLAYER, VILLAGER
	}
}
