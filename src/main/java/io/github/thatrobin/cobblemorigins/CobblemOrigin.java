package io.github.thatrobin.cobblemorigins;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.data.CompatibilityDataTypes;
import io.github.apace100.origins.data.OriginsDataTypes;
import io.github.apace100.origins.origin.Impact;
import io.github.apace100.origins.origin.Origin;
import io.github.thatrobin.cobblemorigins.compat.CobblemOriginEvolution;
import io.github.thatrobin.cobblemorigins.compat.CobblemOriginsDataTypes;
import io.github.thatrobin.cobblemorigins.mixin.OriginInvoker;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class CobblemOrigin {

    public static final SerializableData DATA = new SerializableData()
            .add("moves", SerializableDataTypes.IDENTIFIERS, Lists.newArrayList())
            .add("icon", CompatibilityDataTypes.ITEM_OR_ITEM_STACK, new ItemStack(Items.AIR))
            .add("implemented", SerializableDataTypes.BOOLEAN, false)
            .add("nationalPokedexNumber", SerializableDataTypes.INT, Integer.MAX_VALUE)
            .add("impact", OriginsDataTypes.IMPACT, Impact.NONE)
            .add("loading_priority", SerializableDataTypes.INT, 0)
            .add("evolutions", CobblemOriginsDataTypes.EVOLUTIONS, null)
            .add("name", SerializableDataTypes.STRING, "")
            .add("pokedex", SerializableDataTypes.STRINGS, Lists.newArrayList());

    @SuppressWarnings("unchecked")
    public static Origin createFromData(Identifier id, SerializableData.Instance data) {
        ItemStack stack = data.get("icon");
        if(stack.isEmpty()) {
            try {
                String path = id.getPath().replaceAll("generation\\d*/", "");
                Identifier newId = new Identifier(id.getNamespace(), path);
                Species species = PokemonSpecies.INSTANCE.getByIdentifier(newId);
                if (species != null) {
                    stack = ItemStack.fromNbt(NbtHelper.fromNbtProviderString("{id:\"cobblemon:pokemon_model\",tag:{species:\"" + species.getResourceIdentifier() + "\"},Count:1b}"));
                }
            } catch (Exception ignored) {}
        }
        Origin origin = new Origin(id,
                stack,
                data.get("impact"),
                data.getInt("nationalPokedexNumber"),
                data.getInt("loading_priority"));

        if(!data.getBoolean("implemented")) {
            ((OriginInvoker)origin).setUnchoosable2();
        }

        ((List<Identifier>)data.get("moves")).forEach(powerId -> {
            try {
                String requirement = powerId.getNamespace();

                powerId = new Identifier("cobblemorigins", powerId.getPath());
                if(PowerTypeRegistry.contains(powerId)) {
                    PowerType<?> powerType = PowerTypeRegistry.get(powerId);
                    origin.add(powerType);
                }

            } catch(IllegalArgumentException e) {
                Origins.LOGGER.error("Cobblemorigin \"" + id + "\" contained unregistered power: \"" + powerId + "\"");
            }
        });

        if(data.isPresent("evolutions")) {
            ((List<CobblemOriginEvolution>)data.get("evolutions")).forEach(origin::addUpgrade);
        }

        origin.setName(data.getString("name"));
        List<String> descriptionList = data.get("pokedex");
        StringBuilder description = new StringBuilder();
        for (String s : descriptionList) {
            description.append(Text.translatable(s).getString()).append("\n");
        }
        origin.setDescription(description.toString());

        return origin;
    }

    public static Origin fromJson(Identifier id, JsonObject json) {
        return createFromData(id, DATA.read(json));
    }
}
