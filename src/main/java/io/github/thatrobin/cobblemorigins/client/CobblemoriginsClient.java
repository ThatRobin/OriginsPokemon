package io.github.thatrobin.cobblemorigins.client;

import io.github.apace100.apoli.ApoliClient;
import io.github.apace100.apoli.networking.packet.c2s.UseActivePowersC2SPacket;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.MultiplePowerType;
import io.github.apace100.apoli.power.Power;
import io.github.thatrobin.cobblemorigins.component.MoveHolderComponent;
import io.github.thatrobin.cobblemorigins.networking.ModPacketsS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CobblemoriginsClient implements ClientModInitializer {

    public static KeyBinding USE_MOVE = new KeyBinding("key.cobblemorigins.use_move", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.cobblemorigins");
    public static KeyBinding USE_MOVE_1 = new KeyBinding("key.cobblemorigins.use_move_1", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "category.cobblemorigins");
    public static KeyBinding USE_MOVE_2 = new KeyBinding("key.cobblemorigins.use_move_2", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, "category.cobblemorigins");
    public static KeyBinding USE_MOVE_3 = new KeyBinding("key.cobblemorigins.use_move_3", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "category.cobblemorigins");
    public static KeyBinding USE_MOVE_4 = new KeyBinding("key.cobblemorigins.use_move_4", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "category.cobblemorigins");
    public static KeyBinding CYCLE_MOVE_UP = new KeyBinding("key.cobblemorigins.cycle_move_up", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET, "category.cobblemorigins");
    public static KeyBinding CYCLE_MOVE_DOWN = new KeyBinding("key.cobblemorigins.cycle_key_down", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, "category.cobblemorigins");

    public static Map<String, Boolean> lastState = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ApoliClient.registerPowerKeybinding(USE_MOVE.getTranslationKey(), USE_MOVE);
        KeyBindingHelper.registerKeyBinding(USE_MOVE);
        KeyBindingHelper.registerKeyBinding(USE_MOVE_1);
        KeyBindingHelper.registerKeyBinding(USE_MOVE_2);
        KeyBindingHelper.registerKeyBinding(USE_MOVE_3);
        KeyBindingHelper.registerKeyBinding(USE_MOVE_4);
        KeyBindingHelper.registerKeyBinding(CYCLE_MOVE_UP);
        KeyBindingHelper.registerKeyBinding(CYCLE_MOVE_DOWN);

        ModPacketsS2C.register();

        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            if (client.player == null) {
                return;
            }
            if(USE_MOVE.isPressed()) {
                if (!lastState.get(USE_MOVE.getTranslationKey())) {
                    MoveHolderComponent component = MoveHolderComponent.KEY.get(client.player);
                    int selectedMove = component.getMoveSelection();
                    Power power = component.getPower(selectedMove);
                    List<Identifier> powerTypeIds = new LinkedList<>();
                    if (power != null) {
                        if (power instanceof Active active) {
                            active.onUse();
                            powerTypeIds.add(power.getType().getIdentifier());
                        } else if(power.getType() instanceof MultiplePowerType<?> multiplePowerType) {
                            powerTypeIds.addAll(multiplePowerType.getSubPowers());
                        }
                        ClientPlayNetworking.send(new UseActivePowersC2SPacket(powerTypeIds));
                    }
                }
                lastState.put(USE_MOVE.getTranslationKey(), true);
            } else {
                lastState.put(USE_MOVE.getTranslationKey(), false);
            }
            useMoveKeybind(USE_MOVE_1, 0, client);
            useMoveKeybind(USE_MOVE_2, 1, client);
            useMoveKeybind(USE_MOVE_3, 2, client);
            useMoveKeybind(USE_MOVE_4, 3, client);

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

        HudRenderCallback.EVENT.register(new MoveOverlay());
    }

    public void useMoveKeybind(KeyBinding moveKeybind, int moveToUse, MinecraftClient client) {
        if(client.player == null) return;
        if(moveKeybind.isPressed()) {
            if (!lastState.get(moveKeybind.getTranslationKey())) {
                MoveHolderComponent component = MoveHolderComponent.KEY.get(client.player);
                Power power = component.getPower(moveToUse);
                List<Identifier> powerTypeIds = new LinkedList<>();
                if (power != null) {
                    if (power instanceof Active active) {
                        active.onUse();
                        powerTypeIds.add(power.getType().getIdentifier());
                    } else if(power.getType() instanceof MultiplePowerType<?> multiplePowerType) {
                        powerTypeIds.addAll(multiplePowerType.getSubPowers());
                    }
                    ClientPlayNetworking.send(new UseActivePowersC2SPacket(powerTypeIds));
                }
            }
            lastState.put(moveKeybind.getTranslationKey(), true);
        } else {
            lastState.put(moveKeybind.getTranslationKey(), false);
        }
    }
}
