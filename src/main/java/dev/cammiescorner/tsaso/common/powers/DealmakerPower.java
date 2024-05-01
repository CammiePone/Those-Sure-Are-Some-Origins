package dev.cammiescorner.tsaso.common.powers;

import dev.cammiescorner.tsaso.common.components.ShakeMyHandComponent;
import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;

import java.util.UUID;

public class DealmakerPower extends Power implements Active {
	private Key key;
	private int maximumDeals;

	public DealmakerPower(PowerType<?> type, LivingEntity entity) {
		super(type, entity);
	}

	@Override
	public void onUse() {
		if(!hasMaximumDeals()) {
			ShakeMyHandComponent component = entity.getComponent(ComponentRegistry.SHAKE_MY_HAND);
			component.setHandRaised(!component.isHandRaised());
		}
	}

	@Override
	public Key getKey() {
		return key;
	}

	@Override
	public void setKey(Key key) {
		this.key = key;
	}

	public int getMaximumDeals() {
		return maximumDeals;
	}

	public void setMaximumDeals(int maximumDeals) {
		this.maximumDeals = maximumDeals;
	}

	public boolean hasMaximumDeals() {
		return PowerHolderComponent.hasPower(entity, DealmakerPower.class, power -> {
			long dealCount = entity.getComponent(ComponentRegistry.POWER_SOURCES).copyOfSources().stream().filter(identifier -> {
				return UUID.fromString(identifier.getPath()).equals(entity.getUuid());
			}).count();

			return dealCount >= power.getMaximumDeals();
		});
	}
}
