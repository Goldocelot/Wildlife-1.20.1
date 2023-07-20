package be.goldocelot.wildlife.world.entity.ai.behaviour;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.Function;

public class CustomLookAtTarget<E extends Mob> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_PRESENT));

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    public final CustomLookAtTarget<E> cooldownForIfNotTempted(Function<E, Integer> timeProvider) {
        this.cooldownProvider = (entity) -> {
            if(BrainUtils.hasMemory(entity, MemoryModuleType.TEMPTING_PLAYER)) return 0;
            if(BrainUtils.hasMemory(entity, MemoryModuleType.BREED_TARGET)) return 0;
            return timeProvider.apply(entity);
        };

        return this;
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        return BrainUtils.hasMemory(entity, MemoryModuleType.LOOK_TARGET);
    }

    @Override
    protected void tick(E entity) {
        BrainUtils.withMemory(entity, MemoryModuleType.LOOK_TARGET, target -> {
            //System.out.println("TARGET IS: "+target.currentPosition());
            entity.getLookControl().setLookAt(target.currentPosition());
        });
    }
}

