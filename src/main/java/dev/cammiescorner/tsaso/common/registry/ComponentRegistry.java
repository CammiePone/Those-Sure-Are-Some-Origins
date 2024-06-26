package dev.cammiescorner.tsaso.common.registry;

import dev.cammiescorner.tsaso.TSASO;
import dev.cammiescorner.tsaso.common.components.entity.*;
import dev.cammiescorner.tsaso.common.components.level.LastDeathLevelComponent;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.minecraft.entity.player.PlayerEntity;

public class ComponentRegistry implements EntityComponentInitializer, ScoreboardComponentInitializer {
	public static final ComponentKey<PowerSourcesComponent> POWER_SOURCES = createComponent("power_sources", PowerSourcesComponent.class);
	public static final ComponentKey<RandomPowersComponent> RANDOM_POWERS = createComponent("random_powers", RandomPowersComponent.class);
	public static final ComponentKey<MakeADealComponent> MAKE_A_DEAL = createComponent("make_a_deal", MakeADealComponent.class);
	public static final ComponentKey<ShakeMyHandComponent> SHAKE_MY_HAND = createComponent("shake_my_hand", ShakeMyHandComponent.class);
	public static final ComponentKey<LastDeathPlayerComponent> LAST_DEATH_PLAYER = createComponent("last_death_player", LastDeathPlayerComponent.class);

	public static final ComponentKey<LastDeathLevelComponent> LAST_DEATH_LEVEL = createComponent("last_death_level", LastDeathLevelComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.beginRegistration(PlayerEntity.class, POWER_SOURCES).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(PowerSourcesComponent::new);
		registry.beginRegistration(PlayerEntity.class, RANDOM_POWERS).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(RandomPowersComponent::new);
		registry.beginRegistration(PlayerEntity.class, MAKE_A_DEAL).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(MakeADealComponent::new);
		registry.beginRegistration(PlayerEntity.class, SHAKE_MY_HAND).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(ShakeMyHandComponent::new);
		registry.beginRegistration(PlayerEntity.class, LAST_DEATH_PLAYER).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(LastDeathPlayerComponent::new);
	}

	@Override
	public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
		registry.registerScoreboardComponent(LAST_DEATH_LEVEL, LastDeathLevelComponent::new);
	}

	private static <T extends Component> ComponentKey<T> createComponent(String name, Class<T> component) {
		return dev.onyxstudios.cca.api.v3.component.ComponentRegistry.getOrCreate(TSASO.id(name), component);
	}
}
