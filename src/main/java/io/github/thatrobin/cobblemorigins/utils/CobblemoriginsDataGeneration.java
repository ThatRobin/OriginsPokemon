package io.github.thatrobin.cobblemorigins.utils;

import com.github.oscar0812.pokeapi.models.utility.Language;
import com.github.oscar0812.pokeapi.models.utility.NamedAPIResource;
import com.github.oscar0812.pokeapi.utils.Client;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import java.util.LinkedList;
import java.util.List;

public class CobblemoriginsDataGeneration implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(PokemonAssetProvider::new);
        //pack.addProvider(PokemonDataProvider::new);
        List<String> langs = new LinkedList<>();
        for (NamedAPIResource result : Client.getLanguageList(999, 0).getResults()) {
            Language lang = Client.getLanguageByName(result.getName());
            String frontLang = lang.getIso639();
            if(frontLang.contains("-")) {
                frontLang = frontLang.split("-")[0].toLowerCase();
            }
            String minecraftLangCode = frontLang + "_" + lang.getIso3166();
            if(langs.stream().noneMatch((str) -> str.equalsIgnoreCase(minecraftLangCode))) {
                String finalFrontLang = frontLang;
                pack.addProvider((dataGenner, future) -> new PokemonLangProvider(dataGenner, minecraftLangCode, finalFrontLang));
                langs.add(minecraftLangCode);
            }
        }
        //Language lang = Client.getLanguageByName("en");
        //pack.addProvider((dataGenner, future) ->new PokemonLangProvider(dataGenner, lang.getIso639() + "_" + lang.getIso3166(), lang.getIso639()));
    }

}
