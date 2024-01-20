package io.github.thatrobin.cobblemorigins.mixin;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.PowerTypes;
import io.github.apace100.calio.resource.OrderedResourceListenerManager;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.badge.BadgeManager;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginManager;
import io.github.thatrobin.cobblemorigins.CobblemoriginsManager;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Origins.class, remap = false)
public class OriginsMixin {

    @Inject(method = "registerResourceListeners", at = @At("HEAD"), cancellable = true)
    private void registerCobblemOrigins(OrderedResourceListenerManager manager, CallbackInfo ci) {
        Identifier powerData = Apoli.identifier("powers");
        Identifier originData = Origins.identifier("origins");

        OriginManager originLoader = new OriginManager();
        manager.register(ResourceType.SERVER_DATA, originLoader).after(powerData).complete();
        manager.register(ResourceType.SERVER_DATA, new CobblemoriginsManager()).after(originData).after(Identifier.tryParse("cobblemon:data_resources")).before(Origins.identifier("origin_layers")).complete();
        manager.register(ResourceType.SERVER_DATA, new OriginLayers()).after(originData).after(Identifier.tryParse("cobblemon:data_resources")).complete();
        BadgeManager.init();

        IdentifiableResourceReloadListener badgeLoader = BadgeManager.REGISTRY.getLoader();
        manager.register(ResourceType.SERVER_DATA, badgeLoader).before(powerData).complete();
        PowerTypes.DEPENDENCIES.add(badgeLoader.getFabricId());
        ci.cancel();
    }
}
