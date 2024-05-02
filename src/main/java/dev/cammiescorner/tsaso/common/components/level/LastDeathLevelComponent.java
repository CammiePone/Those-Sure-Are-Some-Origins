package dev.cammiescorner.tsaso.common.components.level;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LastDeathLevelComponent implements Component {
	private static final Map<UUID, Long> LAST_DEATHS = new HashMap<>();

	public LastDeathLevelComponent(Scoreboard scoreboard, MinecraftServer server) {

	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		LAST_DEATHS.clear();

		NbtList list = tag.getList("LastDeath", NbtElement.COMPOUND_TYPE);

		for(int i = 0; i < list.size(); i++) {
			NbtCompound nbt = list.getCompound(i);
			LAST_DEATHS.put(nbt.getUuid("PlayerId"), nbt.getLong("Time"));
		}
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		NbtList list = new NbtList();

		for(UUID id : LAST_DEATHS.keySet()) {
			NbtCompound nbt = new NbtCompound();

			nbt.putUuid("PlayerId", id);
			nbt.putLong("Time", LAST_DEATHS.get(id));
			list.add(nbt);
		}

		tag.put("LastDeath", list);
	}

	public Map<UUID, Long> viewLastDeaths() {
		return Collections.unmodifiableMap(LAST_DEATHS);
	}

	public long getLastDeathTime(UUID uuid) {
		return LAST_DEATHS.getOrDefault(uuid, 0L);
	}

	public void setLastDeathTime(UUID uuid, long time) {
		LAST_DEATHS.put(uuid, time);
	}
}
