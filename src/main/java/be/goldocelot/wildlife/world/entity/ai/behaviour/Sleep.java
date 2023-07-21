package be.goldocelot.wildlife.world.entity.ai.behaviour;

import be.goldocelot.wildlife.registeries.ModMemories;
import be.goldocelot.wildlife.world.SleepingEntity;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.Function;

public class Sleep<E extends LivingEntity & SleepingEntity> extends ExtendedBehaviour<E> {
    protected Function<E, Long> startSleep = entity -> 13000L;
    protected Function<E, Long> endSleep = entity -> 1000L;
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(ModMemories.WORLD_TIME.get(), MemoryStatus.VALUE_PRESENT));

    protected boolean timedOut(long gameTime) {
        return false;
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        return canSleep(entity);
    }


    @Override
    protected void start(E entity) {
        entity.setSleeping(true);
    }

    @Override
    protected void stop(E entity) {
        entity.setSleeping(false);
    }

    private boolean canSleep(E entity){
        Long worldTime = BrainUtils.getMemory(entity, ModMemories.WORLD_TIME.get());
        return entity.getLastHurtByMob() == null && ((worldTime>startSleep.apply(entity) && worldTime <=24000) || (worldTime<endSleep.apply(entity) && worldTime>=0));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        return canSleep(entity);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}
