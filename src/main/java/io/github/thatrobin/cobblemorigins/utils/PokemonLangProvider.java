package io.github.thatrobin.cobblemorigins.utils;

import com.github.oscar0812.pokeapi.models.pokemon.Pokemon;
import com.github.oscar0812.pokeapi.models.pokemon.PokemonSpecies;
import com.github.oscar0812.pokeapi.utils.Client;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import io.github.apace100.origins.origin.Origin;
import io.github.thatrobin.cobblemorigins.Cobblemorigins;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PokemonLangProvider extends FabricLanguageProvider {

    private final String langName;

    protected PokemonLangProvider(FabricDataOutput dataOutput, String langCode, String langName) {
        super(dataOutput, langCode);
        this.langName = langName;
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        //List<PokemonSpecies> pokemonSpeciesList = Client.getGenerationById(1).getPokemonSpecies();
        for (int i = 1; i < 10; i++) {
            try {
                PokemonSpecies pokemonSpecies = Client.getPokemonById(i).getSpecies();
                PokeAPIExtras.LangTextMap nameMap = PokeAPIExtras.getPokemonName(pokemonSpecies.getName());
                PokeAPIExtras.LangTextMap descMap = PokeAPIExtras.getPokemonDescription(pokemonSpecies.getName());
                String nameTransKey = "origin." + Cobblemorigins.MOD_ID + ".species/" + pokemonSpecies.getName() + ".name";
                String nameTransVal = new String(nameMap.getDescription(this.langName).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                String descTransKey = "origin." + Cobblemorigins.MOD_ID + ".species/" + pokemonSpecies.getName() + ".description";
                String descTransVal = new String(descMap.getDescription(this.langName).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                translationBuilder.add(nameTransKey, nameTransVal);
                translationBuilder.add(descTransKey, descTransVal);
            } catch (Exception ignored) {}

        }

    }
}
