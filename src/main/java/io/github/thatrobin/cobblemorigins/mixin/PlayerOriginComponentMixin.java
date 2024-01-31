package io.github.thatrobin.cobblemorigins.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.origins.component.PlayerOriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.util.ChoseOriginCriterion;
import io.github.thatrobin.cobblemorigins.Cobblemorigins;
import io.github.thatrobin.cobblemorigins.component.MoveHolderComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Map;

@Mixin(value = PlayerOriginComponent.class, remap = false)
public abstract class PlayerOriginComponentMixin {

    @Final
    @Shadow private PlayerEntity player;

    @Shadow public abstract Origin getOrigin(OriginLayer layer);

    @Shadow @Final private Map<OriginLayer, Origin> origins;

    @Shadow protected abstract void revokeRemovedPowers(Origin origin, PowerHolderComponent powerComponent);

    @Shadow protected abstract void grantPowersFromOrigin(Origin origin, PowerHolderComponent powerComponent);

    @Shadow public abstract boolean hasAllOrigins();

    @Shadow private boolean hadOriginBefore;

    //@Inject(method = "setOrigin", at = @At("HEAD"), cancellable = true)
    private void shouldResetOriginPowers(OriginLayer layer, Origin origin, CallbackInfo ci) {
        Origin oldOrigin = getOrigin(layer);
        if (origin == oldOrigin) {
            ci.cancel();
        }

        //if(!EvolutionManager.getEvolution(oldOrigin).Equals(origin)) {
        PowerHolderComponent powerComponent = PowerHolderComponent.KEY.get(player);
        if (oldOrigin != null) {

            if (!oldOrigin.getIdentifier().equals(origin.getIdentifier())) {
                for (PowerType<?> powerType : powerComponent.getPowerTypes(false)) {
                    powerComponent.removePower(powerType, powerType.getIdentifier());
                }
                powerComponent.removeAllPowersFromSource(oldOrigin.getIdentifier());
                MoveHolderComponent component = MoveHolderComponent.KEY.get(player);
                component.clearSelectedMoves();
                component.sync();
            }

            else if (!oldOrigin.toJson().equals(origin.toJson())) {
                revokeRemovedPowers(origin, powerComponent);
            }

        }

        grantPowersFromOrigin(origin, powerComponent);
        //}
        this.origins.put(layer, origin);

        if (this.hasAllOrigins()) {
            this.hadOriginBefore = true;
        }

        if (player instanceof ServerPlayerEntity spe) {
            ChoseOriginCriterion.INSTANCE.trigger(spe, origin);
        }
        powerComponent.sync();
    }

    @Inject(method = "grantPowersFromOrigin", at = @At("TAIL"))
    private void grantPowersFromOrigin(Origin origin, PowerHolderComponent powerComponent, CallbackInfo ci) {
        OriginLayer layer = OriginLayers.getLayer(new Identifier("origins", "origin"));
        if(layer.getOrigins().contains(origin.getIdentifier())) {
            MoveHolderComponent component = MoveHolderComponent.KEY.get(player);
            component.clearLearnedMoves();
            component.clearSelectedMoves();
            component.sync();

            for (PowerType<?> powerType : origin.getPowerTypes()) {
                if (!component.hasMove(powerType)) {
                    component.learnMove(powerType);
                }
            }
            component.sync();


            for (PowerType<?> powerType : origin.getPowerTypes()) {
                component.selectMove(powerType);
            }
            component.sync();
        }
    }
}
