package be.goldocelot.wildlife.world.entity.ai.behaviour;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.Animal;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Breed<E extends Animal, T extends Animal> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED), Pair.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT));
    protected BiFunction<E, T, Double> breedRange = (entity, partner) -> 3d;
    protected BiFunction<E, T, Float> speedMod = (entity, partner) -> 1f;
    protected BiFunction<E, T, Boolean> isValidBreedPartner = Animal::canMate;
    protected BiFunction<E, T, Long> spawnChildTime = (entity, partner) -> 60L + entity.getRandom().nextInt(50);

    protected Function<E, Class<T>> validPartnerClass = (entity) -> (Class) entity.getClass();

    private long spawningIn;
    private Optional<T> possiblePartner;
    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        possiblePartner = this.findValidBreedPartner(entity, validPartnerClass.apply(entity));
        return possiblePartner.isPresent();
    }

    protected void start(E entity) {
        T animal = possiblePartner.get();
        entity.getBrain().setMemory(MemoryModuleType.BREED_TARGET, animal);
        animal.getBrain().setMemory(MemoryModuleType.BREED_TARGET, entity);
        BehaviorUtils.lockGazeAndWalkToEachOther(entity, animal, speedMod.apply(entity, animal));
        spawningIn = entity.level().getGameTime() + spawnChildTime.apply(entity, animal);
    }

    @Override
    protected void stop(E entity) {
        entity.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        entity.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        this.spawningIn = 0L;
    }

    @Override
    protected boolean timedOut(long gameTime) {
        return false;
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        T animal = possiblePartner.get();
        return animal.isAlive() && isValidBreedPartner.apply(entity, animal) && BehaviorUtils.entityIsVisible(entity.getBrain(), animal) && entity.level().getGameTime() <= spawningIn;
    }

    protected void tick(ServerLevel pLevel, E pOwner, long pGameTime) {
        T animal = possiblePartner.get();
        BehaviorUtils.lockGazeAndWalkToEachOther(pOwner, animal, speedMod.apply(pOwner, animal));
        if (pOwner.closerThan(animal, 3.0D)) {
            if (pGameTime >= spawningIn) {
                pOwner.spawnChildFromBreeding(pLevel, animal);
                pOwner.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
                animal.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
            }

        }
    }

    public Breed<E, T> setBreedRange(BiFunction<E, T, Double> breedRange) {
        this.breedRange = breedRange;
        return this;
    }

    public Breed<E, T> setSpeedMod(BiFunction<E, T, Float> speedMod) {
        this.speedMod = speedMod;
        return this;
    }

    public Breed<E, T> setIsValidBreedPartner(BiFunction<E, T, Boolean> isValidBreedPartner) {
        this.isValidBreedPartner = isValidBreedPartner;
        return this;
    }

    public Breed<E, T> setSpawnChildTime(BiFunction<E, T, Long> spawnChildTime) {
        this.spawnChildTime = spawnChildTime;
        return this;
    }

    private Optional<T> findValidBreedPartner(E pAnimal, Class<T> tClass) {
        return pAnimal.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get().findClosest((partner) -> {
            if(tClass.isInstance(partner)) return this.isValidBreedPartner.apply(pAnimal, (T) partner);
            return false;
        }).map(tClass::cast);
    }
}
