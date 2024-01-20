package io.github.thatrobin.cobblemorigins.mixin;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.apace100.apoli.component.PowerHolderComponentImpl;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PowerHolderComponentImpl.class, remap = false)
public class PowerHolderComponentMixin {

    @Shadow @Final private LivingEntity owner;

    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private void preventTick(CallbackInfo ci) {
        if(this.owner instanceof PlayerEntity player) {
            OriginComponent originComponent = ModComponents.ORIGIN.get(player);
            Identifier originId = originComponent.getOrigin(OriginLayers.getLayer(Origins.identifier("origin"))).getIdentifier();
            String path = originId.getPath().replaceAll("generation\\d*/", "");
            Identifier newId = new Identifier(originId.getNamespace(), path);
            Species species = PokemonSpecies.INSTANCE.getByIdentifier(newId);
            if (species != null) {
                ci.cancel();
            }
        }
    }

}
