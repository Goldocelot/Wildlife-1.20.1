package be.goldocelot.wildlife.world.entity.ai.behaviour;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowEntity;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.ArrayList;
import java.util.List;
public class FollowTemptingPlayer<E extends PathfinderMob> extends FollowEntity<E, Player> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_PRESENT));

    public FollowTemptingPlayer(){
        super.following((entity) -> {
            return BrainUtils.getMemory(entity, MemoryModuleType.TEMPTING_PLAYER);
        });
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        List<Pair<MemoryModuleType<?>, MemoryStatus>> requirements = new ArrayList<>();
        requirements.addAll(super.getMemoryRequirements());
        requirements.addAll(MEMORY_REQUIREMENTS);

        return requirements;
    }
}
