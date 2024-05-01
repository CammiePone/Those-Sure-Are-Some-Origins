package dev.cammiescorner.tsaso.common.screens;

import dev.cammiescorner.tsaso.common.registry.ScreenHandlerRegistry;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MakeADealScreenHandler extends ScreenHandler {
	private final List<PowerType<?>> powerTypes = new ArrayList<>();

	public MakeADealScreenHandler(int syncId, List<PowerType<?>> powerTypes) {
		super(ScreenHandlerRegistry.MAKE_A_DEAL.get(), syncId);

		this.powerTypes.addAll(powerTypes);
	}

	@Override
	public ItemStack quickTransfer(PlayerEntity player, int fromIndex) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	public List<PowerType<?>> viewPowerTypes() {
		return Collections.unmodifiableList(powerTypes);
	}
}
