package io.github.thatrobin.cobblemorigins;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import io.github.thatrobin.cobblemorigins.component.MoveHolderComponent;
import io.github.thatrobin.cobblemorigins.component.MoveHolderComponentImpl;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.LivingEntity;

public class Cobblemorigins implements ModInitializer, EntityComponentInitializer {



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
}
