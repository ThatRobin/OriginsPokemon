package io.github.thatrobin.cobblemorigins.utils;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.cobblemorigins.Cobblemorigins;

public class MoveFactories {

    public static final MoveFactory MOVE = new MoveFactory(Cobblemorigins.identifier("move_data"),
            new SerializableData()
                    .add("texture_location", SerializableDataTypes.IDENTIFIER),
            MoveData::new);
}
