package be.goldocelot.wildlife.world.entity.ai.behaviour;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.AvoidEntity;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CustomAvoidEntity<E extends PathfinderMob> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));

    protected Predicate<LivingEntity> avoidingPredicate = target -> false;
    protected float noCloserThanSqr = 9f;
    protected float stopAvoidingAfterSqr = 49f;
    protected float speedModifier = 1.3f;

    protected Vec3 avoidPosition;

    protected double lastDist;
    protected LivingEntity avoidingTarget;



    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    /**
     * Set the minimum distance the target entity should be allowed to come before the entity starts retreating.
     * @param blocks The distance, in blocks
     * @return this
     */
    public CustomAvoidEntity<E> noCloserThan(float blocks) {
        this.noCloserThanSqr = blocks * blocks;

        return this;
    }

    /**
     * Set the maximum distance the target entity should be before the entity stops retreating.
     * @param blocks The distance, in blocks
     * @return this
     */
    public CustomAvoidEntity<E> stopCaringAfter(float blocks) {
        this.stopAvoidingAfterSqr = blocks * blocks;

        return this;
    }

    /**
     * Sets the predicate for entities to avoid.
     * @param predicate The predicate
     * @return this
     */
    public CustomAvoidEntity<E> avoiding(Predicate<LivingEntity> predicate) {
        this.avoidingPredicate = predicate;

        return this;
    }

    /**
     * Set the movespeed modifier for when the entity is running away.
     * @param mod The speed multiplier modifier
     * @return this
     */
    public CustomAvoidEntity<E> speedModifier(float mod) {
        this.speedModifier = mod;

        return this;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        Optional<LivingEntity> target = BrainUtils.getMemory(entity, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).findClosest(this.avoidingPredicate);

        if (target.isEmpty()) return false;

        if(BrainUtils.hasMemory(entity, MemoryModuleType.TEMPTING_PLAYER)) {
            if(BrainUtils.getMemory(entity, MemoryModuleType.TEMPTING_PLAYER).equals(target.get())) {
                return false;
            }
        }

        avoidingTarget = target.get();

        avoidPosition = getAvoidPosition(entity);

        return avoidPosition != null;
    }


    @Override
    protected boolean shouldKeepRunning(E entity) {
        return entity.distanceToSqr(avoidingTarget) < this.stopAvoidingAfterSqr;
    }

    @Override
    protected boolean timedOut(long gameTime) {
        return false;
    }

    protected void tick(E entity) {
        double dist = entity.position().distanceTo(avoidPosition);
        if(dist != lastDist && dist >= 1){
            lastDist = dist;
            return;
        }

        Vec3 newPosition = getAvoidPosition(entity);

        if (newPosition == null) return;

        avoidPosition = newPosition;
        BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(avoidPosition, this.speedModifier, 0));
    }

    public Vec3 getAvoidPosition(E entity) {
        double distToTarget = avoidingTarget.distanceToSqr(entity);

        if (distToTarget > this.noCloserThanSqr)
            return null;

        Vec3 position = DefaultRandomPos.getPosAway(entity, 16, 7, avoidingTarget.position());

        if (position == null || avoidingTarget.distanceToSqr(position) < distToTarget)
            return null;

        return position;
    }

    @Override
    protected void start(E entity) {
        BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(avoidPosition, this.speedModifier, 0));
    }

    @Override
    protected void stop(E entity) {
        BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
        lastDist=0;
        avoidPosition=null;
        avoidingTarget=null;
    }
}
