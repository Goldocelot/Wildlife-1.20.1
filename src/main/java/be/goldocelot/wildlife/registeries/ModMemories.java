package be.goldocelot.wildlife.registeries;

import be.goldocelot.wildlife.WildLife;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public class ModMemories {
    private static final DeferredRegister<MemoryModuleType<?>> MEMORY_TYPES =
            DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, WildLife.MOD_ID);

    public static final RegistryObject<MemoryModuleType<Long>> WORLD_TIME = MEMORY_TYPES.register("world_time_memory",() -> new MemoryModuleType<Long>(Optional.empty()));
    public static void register(IEventBus eventBus) {
        MEMORY_TYPES.register(eventBus);
    }
}
