package io.github.thatrobin.cobblemorigins.utils;

import com.cobblemon.mod.common.battles.runner.ShowdownService;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MoveDataProvider implements DataProvider {

    protected final FabricDataOutput dataOutput;

    protected MoveDataProvider(FabricDataOutput dataOutput) {
        this.dataOutput = dataOutput;

    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        final List<CompletableFuture<?>> futures = new ArrayList<>();
        ShowdownService.Companion.getService().getMoves().forEach((je) -> {
            JsonObject jo = je.getAsJsonObject();
            JsonObject moveJson = new JsonObject();
            if (JsonHelper.hasString(jo, "name")) moveJson.addProperty("name", JsonHelper.getString(jo, "name"));
            if (JsonHelper.hasString(jo, "shortDesc")) moveJson.addProperty("description", JsonHelper.getString(jo, "shortDesc"));
            if (JsonHelper.hasString(jo, "type")) moveJson.addProperty("move_type", JsonHelper.getString(jo, "type"));
            moveJson.addProperty("type", "origins:simple");
            Path file = this.dataOutput.getResolver(DataOutput.OutputType.DATA_PACK, "powers").resolveJson(new Identifier(dataOutput.getModId(), JsonHelper.getString(jo, "id")));
            futures.add(DataProvider.writeToPath(writer, moveJson, file));
        });
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Move Datagen";
    }
}
