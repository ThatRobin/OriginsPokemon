package io.github.thatrobin.cobblemorigins.mixin;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginRegistry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(OriginLayer.class)
public class OriginLayerMixin {

    @Inject(method = "fromJson", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/util/JsonHelper;getBoolean(Lcom/google/gson/JsonObject;Ljava/lang/String;Z)Z"))
    private static void fromJson(Identifier id, JsonObject json, CallbackInfoReturnable<OriginLayer> cir, @Local List<OriginLayer.ConditionedOrigin> list) {
        OriginRegistry.identifiers().forEach((identifier) -> {
            String path = identifier.getPath().replaceAll("generation\\d*/", "");
            Identifier newId = new Identifier(identifier.getNamespace(), path);
            Species species = PokemonSpecies.INSTANCE.getByIdentifier(newId);
            if(species != null) {
                if(species.getPreEvolution() == null) {
                    list.add(new OriginLayer.ConditionedOrigin(null, List.of(identifier)));
                }
            }
        });
    }
}
