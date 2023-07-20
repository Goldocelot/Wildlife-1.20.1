package be.goldocelot.wildlife.registeries;

import be.goldocelot.wildlife.WildLife;
import be.goldocelot.wildlife.world.entity.hyena.Hyena;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, WildLife.MOD_ID);

    public static final RegistryObject<EntityType<Hyena>> HYENA = registerEntity("hyena", Hyena::new, MobCategory.CREATURE, 0.6F, 0.85F, 0x8B4513, 0x000000);


    private static <T extends Mob> RegistryObject<EntityType<T>> registerEntity(String id, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height, int eggPrimary, int eggSecondary){
        RegistryObject<EntityType<T>> entity = ENTITY_TYPES.register(id, () -> EntityType.Builder.of(factory, category)
                .sized(width, height)
                .build(new ResourceLocation(WildLife.MOD_ID, id).toString()));

        if(eggPrimary != -1 && eggSecondary != -1) ModItems.registerEntityEgg(id, entity, eggPrimary, eggSecondary);

        return entity;
    }

    private static <T extends  Mob> RegistryObject<EntityType<T>> registerEntity(String id, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height) {
        return registerEntity(id, factory, category, width, height, -1, -1);
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
