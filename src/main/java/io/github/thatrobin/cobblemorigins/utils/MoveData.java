package io.github.thatrobin.cobblemorigins.utils;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.registry.DataObject;
import io.github.apace100.calio.registry.DataObjectFactory;
import net.minecraft.util.Identifier;

public record MoveData(Identifier moveTexture) implements DataObject<MoveData> {


    public MoveData(SerializableData.Instance instance) {
        this(instance.getId("texture_location"));
    }

    public SerializableData.Instance toData(SerializableData.Instance instance) {
        instance.set("texture_location", this.moveTexture);
        return instance;
    }

    @Override
    public DataObjectFactory<MoveData> getFactory() {
        return MoveFactories.MOVE;
    }
}
