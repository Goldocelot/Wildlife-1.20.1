package be.goldocelot.wildlife.utils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class CreativeTabFiller {
    private static Map<RegistryObject<? extends Item>,ObjectArrayList<ResourceKey<CreativeModeTab>>> toRegister = new HashMap<>();

    public static <T extends Item> void setItemCreativeTabs(RegistryObject<T> registryObject, ObjectArrayList<ResourceKey<CreativeModeTab>> of) {
        toRegister.put(registryObject, of);
    }

    public static void addItemCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        for(RegistryObject<? extends Item> item : toRegister.keySet()) {
            for(ResourceKey<CreativeModeTab> tab : toRegister.get(item)) {
                if(event.getTabKey() == tab){
                    event.accept(item.get());
                }
            }
        }
    }
}
