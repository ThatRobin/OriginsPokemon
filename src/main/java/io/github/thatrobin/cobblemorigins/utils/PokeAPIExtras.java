package io.github.thatrobin.cobblemorigins.utils;

import com.github.oscar0812.pokeapi.models.pokemon.Pokemon;
import com.github.oscar0812.pokeapi.utils.Information;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.apace100.origins.Origins;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.reader.UnicodeReader;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.core.util.StringEncoder;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PokeAPIExtras {

    private static <T> T get(JsonObject json, Class<T> clazz) {
        return new com.google.gson.Gson().fromJson(json, clazz);
    }

    public static LangTextMap getMoveDescription(String moveNameOrId) {
        return getLangTextMap("move/", moveNameOrId, "flavor_text_entries", "flavor_text");
    }

    public static LangTextMap getMoveName(String moveNameOrId) {
        return getLangTextMap("move/", moveNameOrId, "names", "name");
    }

    public static LangTextMap getPokemonDescription(String pokemonNameOrId) {
        return getLangTextMap("pokemon-species/", pokemonNameOrId, "flavor_text_entries", "flavor_text");
    }

    public static LangTextMap getPokemonName(String pokemonNameOrId) {
        return getLangTextMap("pokemon-species/", pokemonNameOrId, "names", "name");
    }

    public static LangTextMap getLangTextMap(String endpoint, String pokemonNameOrId, String fieldName, String subfieldName) {
        String apiUrl = "https://pokeapi.co/api/v2/" + endpoint + pokemonNameOrId;

        String data = Information.fromInternet(apiUrl);
        //String data = Information.fromInternet(apiUrl);

        JsonElement element = JsonParser.parseString(data);

        JsonObject json = element.getAsJsonObject();
        JsonArray flavorTextEntries = json.getAsJsonArray(fieldName);

        LangTextMap pokemonName = new LangTextMap();
        for (int i = 0; i < flavorTextEntries.size(); i++) {
            JsonObject entry = flavorTextEntries.get(i).getAsJsonObject();
            String language = entry.getAsJsonObject("language").get("name").getAsString();
            String name = entry.get(subfieldName).getAsString().replaceAll("\n", " ");

            pokemonName.addDescription(language, name);
        }
        return pokemonName;
    }

    static class LangTextMap {
        private final Map<String, String> descriptions;

        public LangTextMap() {
            this.descriptions = new HashMap<>();
        }

        public String getDescription(String language) {
            return descriptions.get(language);
        }

        public void addDescription(String language, String description) {
            this.descriptions.put(language, description);
        }
    }

}
