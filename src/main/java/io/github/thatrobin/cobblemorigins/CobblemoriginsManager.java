package io.github.thatrobin.cobblemorigins;

import com.cobblemon.mod.common.battles.runner.ShowdownService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.MultiJsonDataLoader;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.thatrobin.cobblemorigins.mixin.OriginsRegistryInvoker;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CobblemoriginsManager extends MultiJsonDataLoader implements IdentifiableResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public CobblemoriginsManager() {
        super(GSON, "species");
    }

    @Override
    protected void apply(Map<Identifier, List<JsonElement>> loader, ResourceManager manager, Profiler profiler) {
        //OriginRegistry.reset();
        AtomicBoolean hasConfigChanged = new AtomicBoolean(false);
        loader.forEach((id, jel) -> {
            jel.forEach(je -> {
                try {
                    Origin origin = CobblemOrigin.fromJson(id, je.getAsJsonObject());

                    /*
                    for (JsonElement moves : JsonHelper.getArray(je.getAsJsonObject(), "moves")) {
                        String moveName = moves.getAsString().split(":")[1];
                    }
                     */
                    JsonObject obj = new JsonObject();
                    for (JsonElement move : ShowdownService.Companion.getService().getMoves()) {
                        JsonObject moveObject = (JsonObject) move;
                        for (Map.Entry<String, JsonElement> stringJsonElementEntry : moveObject.entrySet()) {
                            if(!obj.has(stringJsonElementEntry.getKey())) {
                                obj.add(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue());
                            }
                        }
                    }
                    if (!OriginRegistry.contains(id)) {
                        OriginRegistry.register(id, origin);
                    } else {
                        if (OriginRegistry.get(id).getLoadingPriority() < origin.getLoadingPriority()) {
                            OriginsRegistryInvoker.update(id, origin);
                        }
                    }
                } catch (Exception e) {
                    Origins.LOGGER.error("There was a problem reading Origin file " + id.toString() + " (skipping): " + e.getMessage());
                }
            });
            if (OriginRegistry.contains(id)) {
                Origin origin = OriginRegistry.get(id);
                hasConfigChanged.set(hasConfigChanged.get() | Origins.config.addToConfig(origin));
                if (Origins.config.isOriginDisabled(id)) {
                    OriginRegistry.remove(id);
                } else {
                    LinkedList<PowerType<?>> allPowers = new LinkedList<>();
                    origin.getPowerTypes().forEach(allPowers::add);
                    for (PowerType<?> powerType : allPowers) {
                        if (Origins.config.isPowerDisabled(id, powerType.getIdentifier())) {
                            origin.removePowerType(powerType);
                        }
                    }
                }
            }
        });
        Origins.LOGGER.info("Finished loading cobblemorigins from data files. Registry contains " + OriginRegistry.size() + " cobblemorigins.");
        if (hasConfigChanged.get()) {
            Origins.serializeConfig();
        }
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Origins.MODID, "cobblemorigins");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return Set.of(Origins.identifier("origins"), Objects.requireNonNull(Identifier.tryParse("cobblemon:data_resources")));
    }

}
