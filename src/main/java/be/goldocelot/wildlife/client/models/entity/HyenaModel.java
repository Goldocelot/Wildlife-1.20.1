package be.goldocelot.wildlife.client.models.entity;

import be.goldocelot.wildlife.WildLife;
import be.goldocelot.wildlife.world.entity.hyena.Hyena;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class HyenaModel extends GeoModel<Hyena> {
    @Override
    public ResourceLocation getModelResource(Hyena animatable) {
        return new ResourceLocation(WildLife.MOD_ID, "geo/entity/hyena.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Hyena animatable) {
        if(animatable.isLordKast()) return new ResourceLocation(WildLife.MOD_ID, "textures/entity/hyena/hyena_texture_lordkast.png");
        return new ResourceLocation(WildLife.MOD_ID, "textures/entity/hyena/hyena_texture_color_" + animatable.getHyenaColor().name().toLowerCase() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(Hyena animatable) {
        return new ResourceLocation(WildLife.MOD_ID, "animations/entity/hyena.animation.json");
    }

    @Override
    public void setCustomAnimations(Hyena animatable, long instanceId, AnimationState<Hyena> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("Head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotZ(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
