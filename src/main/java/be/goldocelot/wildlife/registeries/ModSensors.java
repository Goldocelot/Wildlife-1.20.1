package be.goldocelot.wildlife.registeries;

import be.goldocelot.wildlife.WildLife;
import be.goldocelot.wildlife.world.entity.ai.sensor.CustomNearbyItemSensor;
import be.goldocelot.wildlife.world.entity.ai.sensor.WorldTimeSensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSensors {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES =
            DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, WildLife.MOD_ID);

    public static final RegistryObject<SensorType<WorldTimeSensor<?>>> WORLD_TIME = SENSOR_TYPES.register("world_time_sensor", () -> new SensorType<>(WorldTimeSensor::new));
    public static final RegistryObject<SensorType<CustomNearbyItemSensor<?>>> NEARBY_ITEM = SENSOR_TYPES.register("nearby_item_sensor", () -> new SensorType<>(CustomNearbyItemSensor::new));
    public static void register(IEventBus eventBus) {
        SENSOR_TYPES.register(eventBus);
    }
}
