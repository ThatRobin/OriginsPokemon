package io.github.thatrobin.cobblemorigins.component;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.*;
import io.github.apace100.apoli.util.GainedPowerCriterion;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;
@SuppressWarnings("unchecked")
public class MoveHolderComponentImpl implements MoveHolderComponent {

    private final LivingEntity owner;
    private final HashMap<PowerType<?>, Power> moveSet = new HashMap<>();
    private final HashMap<Integer, PowerType<?>> indexedMoves = new HashMap<>();
    private final List<PowerType<?>> learnedMoves = new LinkedList<>();
    private int selectedMoveIndex = 0;

    public MoveHolderComponentImpl(LivingEntity owner) {
        this.owner = owner;
    }

    @Override
    public void unlearnMove(PowerType<?> powerType) {
        learnedMoves.remove(powerType);
    }

    @Override
    public boolean learnMove(PowerType<?> powerType) {
        if(!learnedMoves.contains(powerType)) {
            learnedMoves.add(powerType);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasMove(PowerType<?> powerType) {
        return learnedMoves.contains(powerType);
    }

    @Override
    public void deselectMove(Integer index) {
        StringBuilder errorMessage = new StringBuilder("Cannot remove a non-existing power");
        if(!indexedMoves.containsKey(index)) return;
        PowerType<?> powerType = indexedMoves.get(index);
        if (powerType instanceof PowerTypeReference<?> powerTypeRef) {
            powerType = powerTypeRef.getReferencedPowerType();
            errorMessage.append(" (ID: \"").append(powerType.getIdentifier()).append("\")");
        }

        if (powerType == null) {
            Apoli.LOGGER.error(errorMessage.append(" from entity ").append(owner.getName().getString()));
            return;
        }

        Power power = moveSet.remove(powerType);
        if (power != null) {
            power.onRemoved();
            power.onLost();
        }

        if (powerType instanceof MultiplePowerType<?> multiplePowerType) {
            PowerHolderComponent component = PowerHolderComponent.KEY.get(this.owner);
            multiplePowerType.getSubPowers()
                    .stream()
                    .filter(PowerTypeRegistry::contains)
                    .map(PowerTypeRegistry::get)
                    .forEach((pt) -> component.removePower(pt, multiplePowerType.getIdentifier()));
            component.sync();
        }
    }

    @Override
    public boolean selectMove(PowerType<?> powerType) {
        if(indexedMoves.size() < 4) {
            return selectMove(powerType, indexedMoves.size());
        }
        return false;
    }

    @Override
    public boolean selectMove(PowerType<?> powerType, Integer index) {
        StringBuilder errorMessage = new StringBuilder("Cannot add a non-existing power");
        if (powerType == null) {
            Apoli.LOGGER.error(errorMessage.append(" to entity ").append(owner.getName().getString()));
            return false;
        }

        if (powerType instanceof MultiplePowerType<?> multiplePowerType) {
            PowerHolderComponent component = PowerHolderComponent.KEY.get(this.owner);
            multiplePowerType.getSubPowers()
                    .stream()
                    .filter(PowerTypeRegistry::contains)
                    .map(PowerTypeRegistry::get)
                    .forEach((pt) -> component.addPower(pt, multiplePowerType.getIdentifier()));
            component.sync();
        }

        Power power = powerType.create(owner);
        power.onGained();

        power.onAdded();

        indexedMoves.put(index, powerType);
        moveSet.put(powerType, power);

        if (owner instanceof ServerPlayerEntity player) {
            GainedPowerCriterion.INSTANCE.trigger(player, powerType);
        }

        return true;
    }

    @Override
    public boolean hasMoveSelected(PowerType<?> powerType) {
        return moveSet.containsKey(powerType);
    }

    @Override
    public <T extends Power> T getPower(Integer index) {
        if(indexedMoves.containsKey(index)) {
            PowerType<?> powerType = indexedMoves.get(index);
            if (moveSet.containsKey(powerType)) {
                return (T) moveSet.get(powerType);
            }
        }
        return null;
    }

    @Override
    public <T extends Power> T getPower(PowerType<T> powerType) {
        if(moveSet.containsKey(powerType)) {
            return (T) moveSet.get(powerType);
        }
        return null;
    }

    @Override
    public List<Power> getPowers() {
        return moveSet.values().stream().toList();
    }

    @Override
    public List<Integer> getUsedSlots() {
        return indexedMoves.keySet().stream().toList();
    }

    @Override
    public PowerType<?> getPowerType(Integer index) {
        if(indexedMoves.containsKey(index)) {
            return indexedMoves.get(index);
        }
        return null;
    }

    @Override
    public Set<PowerType<?>> getPowerTypes(boolean getSubPowerTypes) {
        Set<PowerType<?>> powerTypes = new HashSet<>(moveSet.keySet());
        if (!getSubPowerTypes) {

            for (PowerType<?> powerType : moveSet.keySet()) {

                if (!(powerType instanceof MultiplePowerType<?> multiplePowerType)) {
                    continue;
                }

                multiplePowerType.getSubPowers()
                        .stream()
                        .filter(PowerTypeRegistry::contains)
                        .map(PowerTypeRegistry::get)
                        .forEach(powerTypes::remove);

            }

        }

        return powerTypes;
    }

    @Override
    public <T extends Power> List<T> getPowers(Class<T> powerClass) {
        return getPowers(powerClass, false);
    }

    @Override
    public <T extends Power> List<T> getPowers(Class<T> powerClass, boolean includeInactive) {
        List<T> list = new LinkedList<>();
        for(Power power : moveSet.values()) {
            if(powerClass.isAssignableFrom(power.getClass()) && (includeInactive || power.isActive())) {
                list.add((T)power);
            }
        }
        return list;
    }

    @Override
    public void incrementSelection() {
        if(this.selectedMoveIndex==Collections.max(this.getUsedSlots())) {
            this.selectedMoveIndex = 0;
        } else {
            this.selectedMoveIndex++;
        }
    }

    @Override
    public void decrementSelection() {
        if(this.selectedMoveIndex-1==-1) {
        this.selectedMoveIndex = Collections.max(this.getUsedSlots());
    } else {
        this.selectedMoveIndex--;
    }
    }

    @Override
    public int getMoveSelection() {
        return this.selectedMoveIndex;
    }

    @Override
    public void sync() {
        MoveHolderComponent.sync(this.owner);
    }

    @Override
    public void serverTick() {
        this.getPowers(Power.class, true).stream().filter(p -> p.shouldTick() && (p.shouldTickWhenInactive() || p.isActive())).forEach(Power::tick);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {
        if (owner == null) {
            Apoli.LOGGER.error("Owner was null in PowerHolderComponent#fromTag! This is not supposed to happen :(");
            return;
        }

        for (Power power : moveSet.values()) {
            power.onRemoved();
            power.onLost();
        }

        learnedMoves.clear();
        moveSet.clear();
        indexedMoves.clear();
        selectedMoveIndex = tag.getInt("SelectedMoveIndex");
        NbtList moveSetTag = tag.getList("MoveSet", NbtElement.COMPOUND_TYPE);
        NbtList learnedMovesTag = tag.getList("LearnedMoves", NbtElement.COMPOUND_TYPE);
        for (NbtElement nbtElement : moveSetTag) {
            NbtCompound compound = (NbtCompound) nbtElement;
            Identifier powerTypeId = Identifier.tryParse(compound.getString("Type"));
            if (powerTypeId == null || PowerTypeRegistry.isDisabled(powerTypeId)) {
                continue;
            }
            try {
                PowerType<?> powerType = PowerTypeRegistry.get(powerTypeId);
                Power power = powerType.create(owner);
                try {
                    if(compound.contains("Data")) {
                        NbtElement data = compound.get("Data");
                        power.fromTag(data);
                    }
                } catch (ClassCastException e) {
                    Apoli.LOGGER.warn("Data type of power \"{}\" changed, skipping data for that power on entity {}", powerTypeId, owner.getName().getString());
                }

                if(indexedMoves.size() < 4) {
                    indexedMoves.put(compound.getInt("Index"), powerType);
                    moveSet.put(powerType, power);

                    power.onAdded();
                }
            } catch (IllegalArgumentException e) {
                //  Occurs when the power is either not registered in the power registry,
                //  or the power no longer exists
                Apoli.LOGGER.warn("Unregistered power \"{}\" found on entity {}, skipping...", powerTypeId, owner.getName().getString());
            }
        }

        for (NbtElement nbtElement : learnedMovesTag) {
            NbtCompound compound = (NbtCompound) nbtElement;
            Identifier powerTypeId = Identifier.tryParse(compound.getString("Type"));
            if (powerTypeId == null || PowerTypeRegistry.isDisabled(powerTypeId)) {
                continue;
            }
            try {
                PowerType<?> powerType = PowerTypeRegistry.get(powerTypeId);

                learnedMoves.add(powerType);

            } catch (IllegalArgumentException e) {
                //  Occurs when the power is either not registered in the power registry,
                //  or the power no longer exists
                Apoli.LOGGER.warn("Unregistered power \"{}\" found on entity {}, skipping...", powerTypeId, owner.getName().getString());
            }
        }

    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        NbtList moveSetTag = new NbtList();
        for (Map.Entry<Integer, PowerType<?>> entry : indexedMoves.entrySet()) {

            PowerType<?> power = entry.getValue();
            NbtCompound powerTag = new NbtCompound();

            powerTag.putString("Factory", power.getFactory().getFactory().getSerializerId().toString());
            powerTag.putString("Type", power.getIdentifier().toString());
            powerTag.putInt("Index", entry.getKey());
            if(moveSet.containsKey(power)) {
                powerTag.put("Data", moveSet.get(power).toTag());
            }
            moveSetTag.add(powerTag);
        }

        tag.put("MoveSet", moveSetTag);

        NbtList learnedMovesTag = new NbtList();
        for (PowerType<?> power : learnedMoves) {
            NbtCompound powerTag = new NbtCompound();

            powerTag.putString("Factory", power.getFactory().getFactory().getSerializerId().toString());
            powerTag.putString("Type", power.getIdentifier().toString());

            learnedMovesTag.add(powerTag);

        }

        tag.put("LearnedMoves", learnedMovesTag);

        tag.putInt("SelectedMoveIndex", selectedMoveIndex);
    }
}
