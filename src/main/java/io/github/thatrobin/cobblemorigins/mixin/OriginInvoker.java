package io.github.thatrobin.cobblemorigins.mixin;

import io.github.apace100.origins.origin.Origin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Origin.class, remap = false)
public interface OriginInvoker {

    @Invoker("setUnchoosable")
    Origin setUnchoosable2();

}
