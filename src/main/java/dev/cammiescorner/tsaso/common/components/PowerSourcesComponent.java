package dev.cammiescorner.tsaso.common.components;

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
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PowerSourcesComponent implements AutoSyncedComponent, ServerTickingComponent {
	private final PlayerEntity player;
	private final Set<Identifier> sourceIds = new HashSet<>();
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

		NbtList list = tag.getList("PowerSources", NbtElement.STRING_TYPE);

		for(int i = 0; i < list.size(); i++)
			sourceIds.add(new Identifier(list.getString(i)));
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		NbtList list = new NbtList();

		for(Identifier id : sourceIds)
			list.add(NbtString.of(id.toString()));

		tag.put("PowerSources", list);
	}

	public void addSource(Identifier id) {
		sourceIds.add(id);
		ComponentRegistry.POWER_SOURCES.sync(player);
	}

	public void removeSource(Identifier id) {
		sourceIds.remove(id);
		ComponentRegistry.POWER_SOURCES.sync(player);
	}

	public Set<Identifier> copyOfSources() {
		return Collections.unmodifiableSet(sourceIds);
	}

	private void applyHealthModifier() {
		EntityAttributeInstance healthAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);

		if(healthAttribute != null) {
			EntityAttributeModifier healthModifier = healthAttribute.getModifier(MODIFIER);
			PowerHolderComponent component = player.getComponent(PowerHolderComponent.KEY);
			Set<PowerType<?>> powers = component.getPowerTypes(false);
			long amount = -2 * sourceIds.stream().filter(id -> id.getNamespace().equals(TSASO.MOD_ID) && !id.getPath().equals(player.getUuidAsString())).flatMap(id -> component.getPowersFromSource(id).stream()).filter(powers::contains).count();

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
