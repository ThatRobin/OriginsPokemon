package io.github.thatrobin.cobblemorigins.compat;

import io.github.apace100.calio.data.SerializableDataType;

import java.util.List;

public class CobblemOriginsDataTypes {

    public static final SerializableDataType<CobblemOriginEvolution> EVOLUTION = new SerializableDataType<>(
            CobblemOriginEvolution.class,
            (buf, upgrade) -> upgrade.write(buf),
            CobblemOriginEvolution::read,
            CobblemOriginEvolution::fromJson);

    public static final SerializableDataType<List<CobblemOriginEvolution>> EVOLUTIONS = SerializableDataType.list(EVOLUTION);
}
