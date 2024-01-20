package io.github.thatrobin.cobblemorigins.mixin;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.origins.component.PlayerOriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.thatrobin.cobblemorigins.component.MoveHolderComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerOriginComponent.class, remap = false)
public class PlayerOriginComponentMixin {

    @Shadow private PlayerEntity player;

    @Inject(method = "grantPowersFromOrigin", at = @At("HEAD"), cancellable = true)
    private void grantPowersFromOrigin(Origin origin, PowerHolderComponent powerComponent, CallbackInfo ci) {
        Identifier source = origin.getIdentifier();
        String path = origin.getIdentifier().getPath().replaceAll("generation\\d*/", "");
        Identifier newId = new Identifier(origin.getIdentifier().getNamespace(), path);
        Species species = PokemonSpecies.INSTANCE.getByIdentifier(newId);

        if(species != null) {
            MoveHolderComponent component = MoveHolderComponent.KEY.get(player);

            for(PowerType<?> powerType : origin.getPowerTypes()) {
                if(!component.hasMove(powerType)) {
                    component.learnMove(powerType);
                }
            }

            for (MoveTemplate moveTemplate : species.getMoves().getLevelUpMovesUpTo(5)) {
                Identifier moveId = new Identifier("cobblemorigins", moveTemplate.getName());
                if(PowerTypeRegistry.contains(moveId)) {
                    component.selectMove(PowerTypeRegistry.get(moveId));
                }
            }

            for (PowerType<?> powerType : component.getPowerTypes(false)) {
                if (!powerComponent.hasPower(powerType, source)) {
                    powerComponent.addPower(powerType, source);
                }
            }
            component.sync();
            ci.cancel();
        }
    }

    @Inject(method = "revokeRemovedPowers", at = @At("HEAD"))
    private void revokeMoves(Origin origin, PowerHolderComponent powerComponent, CallbackInfo ci) {
        MoveHolderComponent component = MoveHolderComponent.KEY.get(player);
        component.deselectMove(0);
        component.deselectMove(1);
        component.deselectMove(2);
        component.deselectMove(3);
        component.sync();
    }
}
