package io.github.thatrobin.cobblemorigins;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.PowerTypes;
import io.github.apace100.calio.resource.OrderedResourceListenerInitializer;
import io.github.apace100.calio.resource.OrderedResourceListenerManager;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.badge.BadgeManager;
import io.github.thatrobin.cobblemorigins.component.MoveHolderComponent;
import io.github.thatrobin.cobblemorigins.component.MoveHolderComponentImpl;
import io.github.thatrobin.cobblemorigins.utils.MoveManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Cobblemorigins implements ModInitializer, EntityComponentInitializer, OrderedResourceListenerInitializer {

    public static final String MOD_ID =  "cobblemorigins";
    public static final Logger LOGGER = LogManager.getLogger(Cobblemorigins.class);

    @Override
    public void onInitialize() {
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(LivingEntity.class, MoveHolderComponent.KEY)
                .impl(MoveHolderComponentImpl.class)
                .respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY)
                .end(MoveHolderComponentImpl::new);
    }

    public static Identifier identifier(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void registerResourceListeners(OrderedResourceListenerManager manager) {
        Identifier powerData = Apoli.identifier("powers");
        MoveManager.init();
        IdentifiableResourceReloadListener moveLoader = MoveManager.REGISTRY.getLoader();
        manager.register(ResourceType.SERVER_DATA, moveLoader).before(powerData).complete();
        PowerTypes.DEPENDENCIES.add(moveLoader.getFabricId());
    }
}
