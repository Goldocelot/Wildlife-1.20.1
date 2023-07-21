package be.goldocelot.wildlife.world.entity.ai.behaviour;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.item.ItemEntity;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class SetItemWalkTarget<E extends PathfinderMob> extends ExtendedBehaviour<E> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(SBLMemoryTypes.NEARBY_ITEMS.get(), MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED));

    protected BiPredicate<LivingEntity, ItemEntity> itemTargetPredicate = (entity, item) -> true;
    protected Function<LivingEntity,Float> maxRange = entity -> 64f;

    public SetItemWalkTarget<E> setItemTargetPredicate(BiPredicate<LivingEntity, ItemEntity> itemTargetPredicate) {
        this.itemTargetPredicate = itemTargetPredicate;

        return this;
    }

    public SetItemWalkTarget<E> setMaxRange(Function<LivingEntity,Float> maxRange) {
        this.maxRange = maxRange;

        return this;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        ItemEntity item = getNearestItemEntity(entity);
        return itemTargetPredicate.test(entity, item) && entity.distanceTo(item) <= maxRange.apply(entity);
    }

    protected void start(E entity) {
        BehaviorUtils.setWalkAndLookTargetMemories(entity, getNearestItemEntity(entity), 1,0);
    }

    private ItemEntity getNearestItemEntity(E entity) {
        List<ItemEntity> items = BrainUtils.getMemory(entity, SBLMemoryTypes.NEARBY_ITEMS.get());

        double dist = Double.MAX_VALUE;
        ItemEntity nearest = null;
        for (ItemEntity item : items) {
            double distance = entity.distanceToSqr(item);
            if(distance < dist){
                dist = distance;
                nearest = item;
            }
        }

        return nearest;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}
