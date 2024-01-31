package io.github.thatrobin.cobblemorigins.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public interface MoveHolderComponent extends AutoSyncedComponent, ServerTickingComponent {

    ComponentKey<MoveHolderComponent> KEY = ComponentRegistry.getOrCreate(new Identifier("cobblemorigins", "moves"), MoveHolderComponent.class);


    void unlearnMove(PowerType<?> powerType);

    boolean learnMove(PowerType<?> powerType);

    boolean hasMove(PowerType<?> powerType);
    void clearLearnedMoves();

    void deselectMove(Integer index);

    boolean selectMove(PowerType<?> powerType);

    boolean selectMove(PowerType<?> powerType, Integer index);

    boolean hasMoveSelected(PowerType<?> powerType);
    void clearSelectedMoves();

    <T extends Power> T getPower(Integer index);

    <T extends Power> T getPower(PowerType<T> powerType);

    List<Power> getPowers();

    List<Integer> getUsedSlots();

    PowerType<?> getPowerType(Integer index);

    Set<PowerType<?>> getPowerTypes(boolean getSubPowerTypes);

    <T extends Power> List<T> getPowers(Class<T> powerClass);

    <T extends Power> List<T> getPowers(Class<T> powerClass, boolean includeInactive);

    void incrementSelection();

    void decrementSelection();
    int getUsedSlotCount();
    int getMoveSelection();

    void sync();

    static void sync(Entity entity) {
        KEY.sync(entity);
    }

}
