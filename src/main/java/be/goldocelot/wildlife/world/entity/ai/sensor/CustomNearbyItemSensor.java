package be.goldocelot.wildlife.world.entity.ai.sensor;

import be.goldocelot.wildlife.registeries.ModSensors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.item.ItemEntity;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

import java.util.List;

public class CustomNearbyItemSensor<E extends Mob> extends PredicateSensor<ItemEntity, E> {
    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(SBLMemoryTypes.NEARBY_ITEMS.get());

    protected SquareRadius radius = new SquareRadius(32, 16);

    public CustomNearbyItemSensor() {
        super((item, entity) -> entity.wantsToPickUp(item.getItem()) && entity.hasLineOfSight(item));
    }

    /**
     * Set the radius for the item sensor to scan.
     *
     * @param radius The coordinate radius, in blocks
     * @return this
     */
    public CustomNearbyItemSensor setRadius(double radius) {
        return setRadius(radius, radius);
    }

    /**
     * Set the radius for the item sensor to scan.
     *
     * @param xz The X/Z coordinate radius, in blocks
     * @param y  The Y coordinate radius, in blocks
     * @return this
     */
    public CustomNearbyItemSensor<E> setRadius(double xz, double y) {
        this.radius = new SquareRadius(xz, y);

        return this;
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ModSensors.NEARBY_ITEM.get();
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        BrainUtils.setMemory(entity, SBLMemoryTypes.NEARBY_ITEMS.get(), EntityRetrievalUtil.getEntities(level, this.radius.inflateAABB(entity.getBoundingBox()), obj -> obj instanceof ItemEntity item && predicate().test(item, entity)));
    }
}