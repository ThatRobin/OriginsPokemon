package io.github.thatrobin.cobblemorigins.utils;

import com.github.oscar0812.pokeapi.models.pokemon.Pokemon;
import com.github.oscar0812.pokeapi.models.pokemon.PokemonMove;
import com.github.oscar0812.pokeapi.models.pokemon.PokemonMoveVersion;
import com.github.oscar0812.pokeapi.models.pokemon.PokemonSpecies;
import com.github.oscar0812.pokeapi.utils.Client;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.apace100.origins.Origins;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PokemonDataProvider implements DataProvider {

    protected final FabricDataOutput dataOutput;

    protected PokemonDataProvider(FabricDataOutput dataOutput) {
        this.dataOutput = dataOutput;

    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        final List<CompletableFuture<?>> futures = new ArrayList<>();

        Path originLayer = this.dataOutput.getResolver(DataOutput.OutputType.DATA_PACK, "origin_layers").resolveJson(Origins.identifier("origin"));

        JsonObject originLayerObject = new JsonObject();
        JsonArray origins = new JsonArray();
        List<PokemonSpecies> pokemonSpeciesList = Client.getGenerationById(1).getPokemonSpecies();
        for (PokemonSpecies pokemonSpecies : pokemonSpeciesList) {
            Pokemon pokemon = Pokemon.getByName(pokemonSpecies.getName());

            JsonArray movesArray = new JsonArray();
            JsonArray powersArray = new JsonArray();

            for (PokemonMove move : pokemon.getMoves()) {
                String moveName = move.getMove().getName().replace("-", "_");
                for(PokemonMoveVersion versionGroup : move.getVersionGroupDetails()) {
                    if(versionGroup.getVersionGroup().getGeneration().getName().equals("generation-ix")) {
                        JsonObject moveObject = new JsonObject();
                        if(versionGroup.getMoveLearnMethod().getName().equals("level-up")) {
                            if (versionGroup.getLevelLearnedAt() > 0 && versionGroup.getLevelLearnedAt() <= 5) {
                                powersArray.add("cobblemorigins:moves/" + moveName);
                            }
                            moveObject.addProperty("learn_at", versionGroup.getLevelLearnedAt());
                            moveObject.addProperty("move", "cobblemorigins:moves/" + moveName);
                            CompletableFuture<?> future = writePowerToFile(move, writer);
                            if(future != null) {
                                futures.add(future);
                            }
                        } else {
                            moveObject.addProperty("learn_at", versionGroup.getMoveLearnMethod().getName());
                            moveObject.addProperty("move", "cobblemorigins:moves/" + moveName);
                        }
                        if(!moveObject.asMap().isEmpty()) {
                            movesArray.add(moveObject);
                        }
                    }
                }
            }
            futures.add(writeOriginToFile(pokemonSpecies, writer, movesArray, powersArray));
        }

        for (PokemonSpecies pokemonSpecies : pokemonSpeciesList) {
            origins.add("cobblemorigins:" + pokemonSpecies.getName());
        }
        originLayerObject.add("origins", origins);
        originLayerObject.addProperty("replace", "true");
        futures.add(DataProvider.writeToPath(writer, originLayerObject, originLayer));

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    public CompletableFuture<?> writePowerToFile(PokemonMove move, DataWriter writer) {
        Path originPath = this.dataOutput.getResolver(DataOutput.OutputType.DATA_PACK, "powers").resolveJson(new Identifier(dataOutput.getModId(), move.getMove().getName()));

        JsonObject powerJson = new JsonObject();


        String moveName = move.getMove().getName().replace("-", "_");

        PokeAPIExtras.LangTextMap pokemonName = PokeAPIExtras.getMoveName(moveName);

        powerJson.addProperty("type", "origins:simple");
        powerJson.addProperty("name", pokemonName.getDescription("en"));

        PokeAPIExtras.LangTextMap pokemonDescription = PokeAPIExtras.getMoveDescription(moveName);
        powerJson.addProperty("description", pokemonDescription.getDescription("en"));

        return DataProvider.writeToPath(writer, powerJson, originPath);
    }

    public CompletableFuture<?> writeOriginToFile(PokemonSpecies pokemonSpecies, DataWriter writer, JsonArray movesArray, JsonArray powersArray) {
        Path originPath = this.dataOutput.getResolver(DataOutput.OutputType.DATA_PACK, "origins").resolveJson(new Identifier(dataOutput.getModId(), pokemonSpecies.getName()));
        JsonObject originJson = new JsonObject();
        JsonObject iconObject = new JsonObject();
        iconObject.addProperty("item", "minecraft:stick");
        iconObject.addProperty("tag", "{CustomItem:\"cobblemorigins:" + pokemonSpecies.getName() + "\"}");
        originJson.add("icon", iconObject);
        originJson.add("moves", movesArray);
        originJson.add("powers", powersArray);

        originJson.addProperty("order", 0);
        originJson.addProperty("impact", 0);

        PokeAPIExtras.LangTextMap pokemonName = PokeAPIExtras.getPokemonName(pokemonSpecies.getName());
        originJson.addProperty("name", pokemonName.getDescription("en"));

        PokeAPIExtras.LangTextMap pokemonDescription = PokeAPIExtras.getPokemonDescription(pokemonSpecies.getName());
        originJson.addProperty("description", pokemonDescription.getDescription("en"));
        return DataProvider.writeToPath(writer, originJson, originPath);
    }

    @Override
    public String getName() {
        return "Pokemon Data Gen";
    }
}
