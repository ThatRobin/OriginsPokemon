package io.github.thatrobin.cobblemorigins.mixin;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.origin.OriginLayers;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(value = OriginLayers.class, remap = false)
public class OriginLayersMixin {

    @Inject(method = "getFabricDependencies", at = @At("RETURN"), cancellable = true)
    private void getDeps(CallbackInfoReturnable<Collection<Identifier>> cir) {
        cir.setReturnValue(Set.of(Origins.identifier("origins"), Objects.requireNonNull(Identifier.tryParse("cobblemon:data_resources"))));
    }
}
