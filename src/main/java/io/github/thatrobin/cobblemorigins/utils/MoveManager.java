package io.github.thatrobin.cobblemorigins.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import io.github.apace100.apoli.integration.PrePowerReloadCallback;
import io.github.apace100.apoli.power.MultiplePowerType;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.apoli.power.PowerTypes;
import io.github.apace100.calio.registry.DataObjectRegistry;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.badge.Badge;
import io.github.apace100.origins.badge.BadgeManager;
import io.github.apace100.origins.integration.AutoBadgeCallback;
import io.github.thatrobin.cobblemorigins.Cobblemorigins;
import io.github.thatrobin.cobblemorigins.networking.ModPackets;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MoveManager {
    public static final DataObjectRegistry<MoveData> REGISTRY = new DataObjectRegistry.Builder<>(Origins.identifier("move_data"), MoveData.class)
            .readFromData("move_data", true)
            .dataErrorHandler((id, exception) -> Origins.LOGGER.error("Failed to read badge " + id + ", caused by", exception))
            .defaultFactory(MoveFactories.MOVE)
            .buildAndRegister();
    public static final Identifier PHASE = Cobblemorigins.identifier("phase/move_manager");
    private static final Map<Identifier, MoveData> MOVES = new HashMap<>();

    public static void init() {
        //register callbacks
        register(MoveFactories.MOVE);
        PrePowerReloadCallback.EVENT.register(BadgeManager::clear);
        PowerTypes.registerAdditionalData("move_data", MoveManager::readAdditionalPowerData);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(PowerTypes.PHASE, PHASE);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(PHASE, (player, joined) -> sync(player));
    }

    public static void sync(ServerPlayerEntity player) {
        PacketByteBuf badgesBuf = PacketByteBufs.create();
        REGISTRY.sync(player);

        badgesBuf.writeMap(MOVES,
                PacketByteBuf::writeIdentifier,
                (valueBuf, badges) -> valueBuf.writeNullable(badges, REGISTRY::writeDataObject));

        ServerPlayNetworking.send(player, ModPackets.MOVE_LIST, badgesBuf);

    }

    public static void register(MoveFactory factory) {
        REGISTRY.registerFactory(factory.id(), factory);
    }

    public static void putMoveData(Identifier powerId, MoveData moveData) {
        MOVES.put(powerId, moveData);
    }

    public static MoveData getMoveData(Identifier powerId) {
        return MOVES.get(powerId);
    }

    public static void clear() {
        MOVES.clear();
    }

    public static void readAdditionalPowerData(Identifier powerId, Identifier factoryId, boolean isSubPower, JsonElement data, PowerType<?> powerType) {
        try {
            Cobblemorigins.LOGGER.error(powerId);
            if(data.isJsonObject()) {
                MoveManager.putMoveData(powerId, REGISTRY.readDataObject(data));
            } else if (data instanceof JsonPrimitive movePrimative) {

                Identifier moveId = new Identifier(movePrimative.getAsString());
                MoveData moveData = REGISTRY.get(moveId);

                if (moveData != null) {
                    MoveManager.putMoveData(powerId, moveData);
                } else {
                    throw new JsonSyntaxException("Move data \"" + moveId + "\" is undefined!");
                }

            }
        } catch (Exception e) {
            Cobblemorigins.LOGGER.error("There was a problem parsing move data of power \"{}\": {}", powerId, e.getMessage());
        }
    }

}
