package io.github.thatrobin.cobblemorigins.utils;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.registry.DataObjectFactory;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public record MoveFactory(Identifier id, SerializableData data, Function<SerializableData.Instance, MoveData> factory) implements DataObjectFactory<MoveData> {
    @Override
    public SerializableData getData() {
        return data;
    }

    @Override
    public MoveData fromData(SerializableData.Instance instance) {
        return factory.apply(instance);
    }

    @Override
    public SerializableData.Instance toData(MoveData moveData) {
        return moveData.toData(data.new Instance());
    }
}
