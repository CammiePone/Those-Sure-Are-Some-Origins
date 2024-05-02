package dev.cammiescorner.tsaso.common.components.entity;

import dev.cammiescorner.tsaso.TSASO;
import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class PowerSourcesComponent implements AutoSyncedComponent, ServerTickingComponent {
	private final PlayerEntity player;
	private final Map<Identifier, Integer> sourceIds = new HashMap<>();
	public static final UUID MODIFIER = UUID.fromString("62ebe154-6763-46d7-b77e-3f091a4a1feb");

	public PowerSourcesComponent(PlayerEntity player) {
		this.player = player;
	}

	@Override
	public void serverTick() {
		applyHealthModifier();
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		sourceIds.clear();

		NbtList list = tag.getList("PowerSources", NbtElement.COMPOUND_TYPE);

		for(int i = 0; i < list.size(); i++) {
			NbtCompound nbt = list.getCompound(i);
			sourceIds.put(new Identifier(nbt.getString("SourceId")), nbt.getInt("DealCount"));
		}
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		NbtList list = new NbtList();

		for(Identifier id : sourceIds.keySet()) {
			NbtCompound nbt = new NbtCompound();

			nbt.putString("SourceId", id.toString());
			nbt.putInt("DealCount", sourceIds.get(id));
			list.add(nbt);
		}

		tag.put("PowerSources", list);
	}

	public void addSource(Identifier id) {
		if(sourceIds.containsKey(id))
			sourceIds.put(id, sourceIds.get(id) + 1);
		else
			sourceIds.put(id, 1);

		ComponentRegistry.POWER_SOURCES.sync(player);
	}

	public void removeSource(Identifier id) {
		sourceIds.remove(id);
		ComponentRegistry.POWER_SOURCES.sync(player);
	}

	public Set<Identifier> copyOfSources() {
		return Collections.unmodifiableSet(sourceIds.keySet());
	}

	public int dealCount(Identifier id) {
		return sourceIds.getOrDefault(id, 0);
	}

	private void applyHealthModifier() {
		EntityAttributeInstance healthAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);

		if(healthAttribute != null) {
			EntityAttributeModifier healthModifier = healthAttribute.getModifier(MODIFIER);
			PowerHolderComponent component = player.getComponent(PowerHolderComponent.KEY);
			Set<PowerType<?>> powers = component.getPowerTypes(false);
			long amount = -2 * sourceIds.keySet().stream().filter(id -> id.getNamespace().equals(TSASO.MOD_ID) && !id.getPath().equals(player.getUuidAsString())).flatMap(id -> component.getPowersFromSource(id).stream()).filter(powers::contains).count();

			if(healthModifier != null && healthModifier.getValue() != amount) {
				healthAttribute.removeModifier(healthModifier);
				return;
			}

			if(healthModifier == null) {
				healthAttribute.addPersistentModifier(new EntityAttributeModifier(MODIFIER, "Deal Health Modifier", amount, EntityAttributeModifier.Operation.ADDITION));

				if(player instanceof ServerPlayerEntity serverPlayer)
					serverPlayer.markHealthDirty();
			}
		}
	}
}
