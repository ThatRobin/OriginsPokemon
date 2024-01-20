package io.github.thatrobin.cobblemorigins.client;

import io.github.apace100.apoli.ApoliClient;
import io.github.apace100.apoli.networking.ModPackets;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import io.github.thatrobin.cobblemorigins.component.MoveHolderComponent;
import io.netty.buffer.Unpooled;
import kotlin.jvm.internal.Intrinsics;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class CobblemoriginsClient implements ClientModInitializer {

    public static KeyBinding USE_MOVE = new KeyBinding("key.cobblemorigins.use_move_1", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.cobblemorigins");
    public static KeyBinding CYCLE_MOVE_UP = new KeyBinding("key.cobblemorigins.cycle_move_up", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET, "category.cobblemorigins");
    public static KeyBinding CYCLE_MOVE_DOWN = new KeyBinding("key.cobblemorigins.cycle_key_down", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, "category.cobblemorigins");

    public static Map<String, Boolean> lastState = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ApoliClient.registerPowerKeybinding(USE_MOVE.getTranslationKey(), USE_MOVE);
        KeyBindingHelper.registerKeyBinding(USE_MOVE);

        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            if (client.player == null) {
                return;
            }

            if(USE_MOVE.isPressed()) {
                if (!lastState.get(USE_MOVE.getTranslationKey())) {
                    MoveHolderComponent component = MoveHolderComponent.KEY.get(client.player);
                    int selectedMove = component.getMoveSelection();
                    Power power = component.getPower(selectedMove);
                    if (power != null) {
                        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
                        buffer.writeInt(1);
                        buffer.writeIdentifier(power.getType().getIdentifier());
                        if (power instanceof Active active) {
                            active.onUse();
                        }
                        ClientPlayNetworking.send(ModPackets.USE_ACTIVE_POWERS, buffer);
                    }
                }
                lastState.put(USE_MOVE.getTranslationKey(), true);
            } else {
                lastState.put(USE_MOVE.getTranslationKey(), false);
            }

            if(CYCLE_MOVE_UP.isPressed()) {
                if (!lastState.get(CYCLE_MOVE_UP.getTranslationKey())) {
                    MoveHolderComponent component = MoveHolderComponent.KEY.get(client.player);
                    component.incrementSelection();
                    component.sync();
                }
                lastState.put(CYCLE_MOVE_UP.getTranslationKey(), true);
            } else {
                lastState.put(CYCLE_MOVE_UP.getTranslationKey(), false);
            }
            if(CYCLE_MOVE_DOWN.isPressed()) {
                if (!lastState.get(CYCLE_MOVE_DOWN.getTranslationKey())) {
                    MoveHolderComponent component = MoveHolderComponent.KEY.get(client.player);
                    component.decrementSelection();
                    component.sync();
                }
                lastState.put(CYCLE_MOVE_DOWN.getTranslationKey(), true);
            } else {
                lastState.put(CYCLE_MOVE_DOWN.getTranslationKey(), false);
            }
        });
    }

    public static void beforeChatRender(@NotNull DrawContext context, float partialDeltaTicks) {
        Intrinsics.checkNotNullParameter(context, "context");
        new MoveOverlay(MinecraftClient.getInstance(),MinecraftClient.getInstance().getItemRenderer()).render(context, partialDeltaTicks);
    }
}
