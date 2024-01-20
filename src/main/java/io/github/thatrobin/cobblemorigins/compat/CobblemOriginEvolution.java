package io.github.thatrobin.cobblemorigins.compat;

import com.cobblemon.mod.common.util.MiscUtilsKt;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.apace100.origins.origin.OriginUpgrade;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class CobblemOriginEvolution extends OriginUpgrade{

    public CobblemOriginEvolution(Identifier upgradeToOrigin, String announcement) {
        super(Identifier.tryParse("dawfgaeggerhrwe"), upgradeToOrigin, announcement);
        this.upgradeToOrigin = upgradeToOrigin;
        this.announcement = announcement;
    }

    private final Identifier upgradeToOrigin;
    private final String announcement;

    public void write(PacketByteBuf buffer) {
        buffer.writeIdentifier(upgradeToOrigin);
        buffer.writeString(announcement);
    }

    public static CobblemOriginEvolution read(PacketByteBuf buffer) {
        String variant = buffer.readString();
        int minLevel = buffer.readInt();
        Identifier origin = buffer.readIdentifier();
        String announcement = buffer.readString(32767);
        return new CobblemOriginEvolution(origin, announcement);
    }

    public static CobblemOriginEvolution fromJson(JsonElement jsonElement) {
        if(!jsonElement.isJsonObject()) {
            throw new JsonParseException("CobblemOrigin evolution needs to be a JSON object.");
        }
        JsonObject json = jsonElement.getAsJsonObject();

        if(JsonHelper.hasString(json, "result")) {
            String pokemonName = JsonHelper.getString(json, "result");
            pokemonName = pokemonName.split(" ")[0];
            JsonArray requirements = JsonHelper.getArray(json, "requirements");
            for (JsonElement requirement : requirements) {
                JsonObject jsonObject = requirement.getAsJsonObject();
                if(JsonHelper.hasString(jsonObject, "variant")) {
                    String variant = JsonHelper.getString(jsonObject, "variant");
                    if (variant.equals("level")) {
                        int minLevel = JsonHelper.getInt(jsonObject, "minLevel");
                    }
                }
            }
            Identifier pokemonIdentifier = MiscUtilsKt.cobblemonResource(pokemonName);
            return new CobblemOriginEvolution(pokemonIdentifier, "you have evolved into " + pokemonName);
        } else {
            throw new JsonParseException("CobblemOrigin upgrade JSON requires \"result\" string and \"requirements\" array.");
        }
    }
}
