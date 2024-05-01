package dev.cammiescorner.tsaso.common.registry;

import dev.cammiescorner.tsaso.TSASO;
import dev.cammiescorner.tsaso.common.powers.DealmakerPower;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.registry.Registry;

public class PowerRegistry {
	public static final PowerFactory<?> DEALMAKER = new PowerFactory<>(TSASO.id("dealmaker"), new SerializableData()
			.add("key", ApoliDataTypes.KEY, new Active.Key())
			.add("maximum_deals", SerializableDataTypes.INT, 1),
			data -> (type, entity) -> {
				DealmakerPower power = new DealmakerPower(type, entity);
				power.setMaximumDeals(data.get("maximum_deals"));
				power.setKey(data.get("key"));
				return power;
			});

	public static void register() {
		Registry.register(ApoliRegistries.POWER_FACTORY, DEALMAKER.getSerializerId(), DEALMAKER);
	}
}
