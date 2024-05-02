package dev.cammiescorner.tsaso.mixin.origins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.cammiescorner.tsaso.common.components.entity.PowerSourcesComponent;
import dev.cammiescorner.tsaso.common.registry.ComponentRegistry;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.screen.OriginDisplayScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Mixin(OriginDisplayScreen.class)
public abstract class OriginDisplayScreenMixin extends Screen {
	protected OriginDisplayScreenMixin(Text title) { super(title); }

	@WrapOperation(method = "renderOriginContent", at = @At(value = "INVOKE",
			target = "Lio/github/apace100/origins/origin/Origin;getPowerTypes()Ljava/lang/Iterable;"
	))
	private Iterable<PowerType<?>> tsaso$powerTypes(Origin ownerType, Operation<Iterable<PowerType<?>>> original) {
		if(client != null && client.player != null) {
			PowerHolderComponent powerComponent = client.player.getComponent(PowerHolderComponent.KEY);
			PowerSourcesComponent component = client.player.getComponent(ComponentRegistry.POWER_SOURCES);

			Stream<PowerType<?>> stream = StreamSupport.stream(original.call(ownerType).spliterator(), false);

			for(Identifier id : component.copyOfSources())
				stream = Stream.concat(stream, powerComponent.getPowersFromSource(id).stream());

			return stream.toList();
		}

		return original.call(ownerType);
	}
}
