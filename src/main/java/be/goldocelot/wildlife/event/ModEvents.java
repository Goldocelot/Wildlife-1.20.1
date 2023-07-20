package be.goldocelot.wildlife.event;

import be.goldocelot.wildlife.WildLife;
import be.goldocelot.wildlife.registeries.ModEntities;
import be.goldocelot.wildlife.world.entity.hyena.Hyena;
import be.goldocelot.wildlife.world.entity.hyena.HyenaColorUniformizer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


public class ModEvents {
    @Mod.EventBusSubscriber(modid = WildLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public class WildLifeEvents{
        @SubscribeEvent
        public static void entityAttributeEvent(EntityAttributeCreationEvent event){
            event.put(ModEntities.HYENA.get(), Hyena.setAttributes());
        }

        @SubscribeEvent
        public static void entitySpawnRestriction(SpawnPlacementRegisterEvent event){
            event.register(ModEntities.HYENA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        }
    }
    @Mod.EventBusSubscriber(modid = WildLife.MOD_ID)
    public class ForgeServerEvents{
        @SubscribeEvent
        public static void entityFinalizeEvent(MobSpawnEvent.FinalizeSpawn event){
            if(event.getSpawnType() != MobSpawnType.NATURAL) return;
            if(event.getEntity() instanceof Hyena) event.setSpawnData(HyenaColorUniformizer.getInstance().uniformizeHyenaColor(event.getEntity().position()));
        }
    }

}
