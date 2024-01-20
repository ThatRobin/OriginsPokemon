package io.github.thatrobin.cobblemorigins.mixin;

import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginRegistry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(OriginRegistry.class)
public interface OriginsRegistryInvoker {

    @Invoker("update")
    static Origin update(Identifier id, Origin origin) {
        throw new AssertionError();
    }


}
