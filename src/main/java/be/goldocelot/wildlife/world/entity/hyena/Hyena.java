package be.goldocelot.wildlife.world.entity.hyena;

import be.goldocelot.wildlife.registeries.ModEntities;
import be.goldocelot.wildlife.world.SleepingEntity;
import be.goldocelot.wildlife.world.entity.ai.behaviour.*;
import be.goldocelot.wildlife.world.entity.ai.sensor.WorldTimeSensor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyItemsSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.ItemTemptingSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class Hyena extends Animal implements GeoEntity, SmartBrainOwner<Hyena>, SleepingEntity {

    public static final float INTERESTING_HEALTH_THRESHOLD = 0.3f;
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(Hyena.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PATTERN = SynchedEntityData.defineId(Hyena.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(Hyena.class, EntityDataSerializers.BOOLEAN);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public Hyena(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.75f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.3f).build();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COLOR, 0);
        this.entityData.define(PATTERN, 0);
        this.entityData.define(SLEEPING, !level().isDay());
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("color", getHyenaColor().ordinal());
        pCompound.putInt("pattern", getHyenaPattern().ordinal());
        pCompound.putBoolean("sleeping", isSleeping());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setHyenaColor(HyenaColor.values()[pCompound.getInt("color")]);
        setHyenaPattern(HyenaPattern.values()[pCompound.getInt("pattern")]);
        setSleeping(pCompound.getBoolean("sleeping"));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @javax.annotation.Nullable SpawnGroupData pSpawnData, @javax.annotation.Nullable CompoundTag pDataTag) {
        if(!(pSpawnData instanceof HyenaGroupData)) pSpawnData = new HyenaGroupData(HyenaColor.values()[random.nextInt(HyenaColor.values().length)], HyenaPattern.values()[random.nextInt(HyenaPattern.values().length)]);

        setHyenaColor(((HyenaGroupData)pSpawnData).color);
        setHyenaPattern(((HyenaGroupData)pSpawnData).pattern);

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        if (!(pOtherParent instanceof Hyena)) return null;

        Hyena otherParent = (Hyena) pOtherParent;

        Hyena hyena = ModEntities.HYENA.get().create(pLevel);
        hyena.setHyenaColor(List.of(this.getHyenaColor(), otherParent.getHyenaColor()).get(this.random.nextInt(2)));
        hyena.setHyenaPattern(List.of(this.getHyenaPattern(), otherParent.getHyenaPattern()).get(this.random.nextInt(2)));
        return hyena;
    }

    //Needed cause of the SmartBrainLib issue
    @Override
    public boolean isAlliedTo(Entity pEntity) {
        return pEntity != null && super.isAlliedTo(pEntity);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        if(isSleeping()) return false;
        return pStack.getItem().equals(getTemptItem());
    }

    public Item getTemptItem() {
        return Items.ROTTEN_FLESH;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "controller", 5, this::predicate));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> geoAnimatableAnimationState) {
        if (isSleeping()) geoAnimatableAnimationState.setAndContinue(DefaultAnimations.REST);
        else if (geoAnimatableAnimationState.isMoving())
            geoAnimatableAnimationState.setAndContinue(DefaultAnimations.WALK);
        else geoAnimatableAnimationState.setAndContinue(DefaultAnimations.IDLE);

        return PlayState.CONTINUE;
    }


    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return stack.getItem() == Items.ROTTEN_FLESH;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public HyenaColor getHyenaColor() {
        return HyenaColor.values()[this.entityData.get(COLOR)];
    }

    public void setHyenaColor(HyenaColor hyenaColor) {
        this.entityData.set(COLOR, hyenaColor.ordinal());
    }

    public HyenaPattern getHyenaPattern() {
        return HyenaPattern.values()[this.entityData.get(PATTERN)];
    }

    public void setHyenaPattern(HyenaPattern hyenaPattern) {
        this.entityData.set(PATTERN, hyenaPattern.ordinal());
    }

    public boolean isSleeping() {
        return this.entityData.get(SLEEPING);
    }

    @Override
    public void setSleeping(boolean sleeping) {
        this.entityData.set(SLEEPING, sleeping);
    }

    @Override
    public List<? extends ExtendedSensor<? extends Hyena>> getSensors() {
        return ObjectArrayList.of(
                new WorldTimeSensor<>(),
                new NearbyPlayersSensor<>(),
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>(),
                new NearbyItemsSensor<>(),
                new ItemTemptingSensor<Hyena>().setTemptingItems(Ingredient.of(getTemptItem()))
        );
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
        super.customServerAiStep();
    }


    @Override
    public BrainActivityGroup<Hyena> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(
                        new TargetOrRetaliate<Hyena>().alertAlliesWhen((target, entity) -> true).attackablePredicate((entity) -> {
                            Entity lastHurt = BrainUtils.getMemory(this, MemoryModuleType.HURT_BY_ENTITY);
                            return entity.getHealth() <= (entity.getMaxHealth() * INTERESTING_HEALTH_THRESHOLD) || (lastHurt != null && lastHurt.equals(entity));
                        }),
                        new Breed<Hyena, Hyena>(),
                        new FollowTemptingPlayer<Hyena>().stopFollowingWithin(1f),
                        new CustomAvoidEntity<>().avoiding((entity) -> entity instanceof Player).noCloserThan(6).stopCaringAfter(10),
                        new SetRandomWalkTarget<Hyena>().setRadius(6,5).cooldownFor(entity -> entity.getRandom().nextInt(100, 400))
                ),
                new FirstApplicableBehaviour<>(
                        new SetPlayerLookTarget<>().predicate((entity) -> entity.position().distanceTo(this.position()) <6.5f).cooldownFor(entity -> entity.getRandom().nextInt(100,200)),
                        new SetRandomLookTarget<>().cooldownFor(entity -> entity.getRandom().nextInt(100,200))
                )
        );
    }

    @Override
    public BrainActivityGroup<Hyena> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new Sleep<Hyena>(),
                new CustomLookAtTarget<Hyena>().cooldownForIfNotTempted(entity -> entity.getRandom().nextInt(60, 100)).stopIf((hyena -> hyena.isSleeping())).runFor(entity -> entity.getRandom().nextInt(40, 80)).startCondition((entity) -> !entity.isSleeping()),
                new MoveToWalkTarget<Hyena>().startCondition((entity)->!entity.isSleeping()).stopIf((entity)-> entity.isSleeping())
        );
    }

    @Override
    public BrainActivityGroup<Hyena> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<Hyena>(),
                new SetWalkTargetToAttackTarget<Hyena>(),
                new AnimatableMeleeAttack<Hyena>(0));
    }

    public static class HyenaGroupData extends AgeableMob.AgeableMobGroupData {
        public final HyenaColor color;
        public final HyenaPattern pattern;

        public HyenaGroupData(HyenaColor color, HyenaPattern pattern) {
            super(true);
            this.color = color;
            this.pattern = pattern;
        }
    }
}
