package io.github.thatrobin.cobblemorigins.networking;

import io.github.thatrobin.cobblemorigins.utils.MoveData;
import io.github.thatrobin.cobblemorigins.utils.MoveManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ModPacketsS2C {

    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientPlayConnectionEvents.INIT.register(((clientPlayNetworkHandler, minecraftClient) -> {
            ClientPlayNetworking.registerReceiver(ModPackets.MOVE_LIST, ModPacketsS2C::receiveMoveList);
        }));
    }

    @Environment(EnvType.CLIENT)
    private static void receiveMoveList(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Map<Identifier, MoveData> moves = buf.readMap(
                PacketByteBuf::readIdentifier,
                valueBuf -> valueBuf.readNullable(MoveManager.REGISTRY::receiveDataObject)
        );

        client.execute(() -> {
            MoveManager.clear();
            moves.forEach(MoveManager::putMoveData
            );
        });

    }

}
