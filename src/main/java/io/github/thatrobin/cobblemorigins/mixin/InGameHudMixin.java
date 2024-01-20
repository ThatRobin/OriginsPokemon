package io.github.thatrobin.cobblemorigins.mixin;

import io.github.thatrobin.cobblemorigins.client.CobblemoriginsClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Unique
    private Long lastTimeMillis = null;
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;III)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private void beforeChatHook(DrawContext context, float f, CallbackInfo ci) {
        if (lastTimeMillis != null) {
            CobblemoriginsClient.beforeChatRender(context, (System.currentTimeMillis() - lastTimeMillis) / 1000F * 20);
        }
        lastTimeMillis = System.currentTimeMillis();
    }
}
