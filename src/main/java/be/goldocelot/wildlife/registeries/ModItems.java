package be.goldocelot.wildlife.registeries;

import be.goldocelot.wildlife.WildLife;
import be.goldocelot.wildlife.utils.CreativeTabFiller;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, WildLife.MOD_ID);

    public static <T extends Mob> RegistryObject<Item> registerEntityEgg(String entityId, RegistryObject<EntityType<T>> entityTypeRegistryObject, int primaryEggColour, int secondaryEggColour){
        RegistryObject<Item> egg = ITEMS.register(entityId+"_spawn_egg", () -> new ForgeSpawnEggItem(() -> entityTypeRegistryObject.get(), primaryEggColour, secondaryEggColour, new Item.Properties()));
        CreativeTabFiller.setItemCreativeTabs(egg, ObjectArrayList.of(CreativeModeTabs.SPAWN_EGGS));
        return egg;
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
