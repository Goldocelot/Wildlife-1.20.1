package be.goldocelot.wildlife.world.entity.ai.sensor;

import be.goldocelot.wildlife.registeries.ModMemories;
import be.goldocelot.wildlife.registeries.ModSensors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class WorldTimeSensor<E extends LivingEntity> extends ExtendedSensor<E> {
    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(ModMemories.WORLD_TIME.get());

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ModSensors.WORLD_TIME.get();
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        BrainUtils.setMemory(entity, ModMemories.WORLD_TIME.get(), level.getDayTime()%24000);
    }
}
