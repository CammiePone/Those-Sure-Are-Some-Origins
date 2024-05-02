package dev.cammiescorner.tsaso.common.components.entity;

import com.google.common.collect.Iterables;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ModifyPlayerSpawnPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RandomPowersComponent implements ServerTickingComponent {
	private final PlayerEntity player;
	private final List<PowerType<?>> powerTypes = new ArrayList<>();
	private static final List<String> DISALLOWED_POWERS = Arrays.asList(
			"tsaso:dealmaker",

			"extraorigins:rooted", "extraorigins:radioactive",

			"origins:hunger_over_time", "origins:invisibility", "origins:phantomize", "origins:phasing",
			"origins:webbing", "origins:arcane_skin", "origins:end_spawn", "origins:nether_spawn"
	);

	public RandomPowersComponent(PlayerEntity player) {
		this.player = player;
	}

	@Override
	public void serverTick() {
		if(powerTypes.isEmpty()) {
			while(powerTypes.size() < 5) {
				PowerType<?> powerType = Iterables.get(PowerTypeRegistry.values(), player.getRandom().nextInt(PowerTypeRegistry.size()));

				if(!isPowerAllowed(powerType))
					continue;

				powerTypes.add(powerType);
			}
		}

		if(player.getWorld().getTimeOfDay() == 18000)
			powerTypes.clear();
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		powerTypes.clear();

		NbtList nbtList = tag.getList("PowerTypes", NbtElement.STRING_TYPE);

		for(int i = 0; i < nbtList.size(); i++)
			powerTypes.add(PowerTypeRegistry.get(Identifier.tryParse(nbtList.getString(i))));
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		NbtList nbtList = new NbtList();

		for(PowerType<?> powerType : powerTypes)
			nbtList.add(NbtString.of(powerType.getIdentifier().toString()));

		tag.put("PowerTypes", nbtList);
	}

	public void rerollPowers() {
		powerTypes.clear();
	}

	public List<PowerType<?>> viewPowerTypes() {
		return Collections.unmodifiableList(powerTypes);
	}

	public boolean isPowerAllowed(PowerType<?> powerType) {
		String name = powerType.getName().getString();

		if(powerTypes.contains(powerType) || player.getComponent(PowerHolderComponent.KEY).hasPower(powerType))
			return false;
		if(name.startsWith("power.") && name.endsWith(".name"))
			return false;
		if(powerType.create(null) instanceof ModifyPlayerSpawnPower)
			return false;
		if(powerType.isHidden()) // TODO check subpower
			return false;

		return !DISALLOWED_POWERS.contains(powerType.getIdentifier().toString());
	}
}
