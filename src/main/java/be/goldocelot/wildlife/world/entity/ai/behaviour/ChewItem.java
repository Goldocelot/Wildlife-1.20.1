package be.goldocelot.wildlife.world.entity.ai.behaviour;

import be.goldocelot.wildlife.world.entity.ChewingEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ChewItem<E extends LivingEntity & ChewingEntity> extends ExtendedBehaviour<E> {
    protected Predicate<E> canChewItem = (entity) -> entity.getMainHandItem() != null && entity.getMainHandItem().isEdible();
    protected Consumer<E> onFinishedChew = (entity) -> {
        ItemEntity itemEntity = new ItemEntity(EntityType.ITEM, entity.level());
        itemEntity.moveTo(entity.position());
        itemEntity.setItem(new ItemStack(Items.BONE));
        entity.level().addFreshEntity(itemEntity);
    };

    protected Function<E,Long> chewTime = (entity) -> 45L + entity.getRandom().nextInt(35);

    private long finishChewAt;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of();
    }

    public ChewItem<E> setCanChewItem(Predicate<E> canChewItem) {
        this.canChewItem = canChewItem;
        return this;
    }

    public ChewItem<E> setOnFinishedChew(Consumer<E> onFinishedChew) {
        this.onFinishedChew = onFinishedChew;
        return this;
    }

    public ChewItem<E> setChewTime(Function<E, Long> chewTime) {
        this.chewTime = chewTime;
        return this;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        return canChewItem.test(entity);
    }

    protected void start(E entity) {
        finishChewAt = entity.level().getGameTime() + chewTime.apply(entity);
        entity.setChewing(true);
    }

    protected void tick(E entity) {
        if(!entity.isChewing()) entity.setChewing(true);

        ServerLevel level = (ServerLevel) entity.level();
        if (level.getGameTime() % 5 == 0) {
            level.playSound(null, entity.getOnPos(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1, 1);
        }
    }

    protected void stop(E entity) {
        if(entity.level().getGameTime() >= finishChewAt){
            onFinishedChew.accept(entity);
            ItemStack item = entity.getMainHandItem();
            item.setCount(item.getCount() - 1);
            entity.setItemInHand(InteractionHand.MAIN_HAND, item);
        }
        finishChewAt = 0L;
        entity.setChewing(false);
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        return canChewItem.test(entity) && entity.level().getGameTime() <= finishChewAt;
    }


}
