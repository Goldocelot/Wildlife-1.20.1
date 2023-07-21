package be.goldocelot.wildlife.client.renderers.entity.hyena;

import be.goldocelot.wildlife.client.models.entity.HyenaModel;
import be.goldocelot.wildlife.world.entity.hyena.Hyena;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import javax.annotation.Nullable;

public class HyenaRenderer extends GeoEntityRenderer<Hyena> {

    public HyenaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HyenaModel());

        this.shadowRadius = 0.45f;

        addRenderLayer(new BlockAndItemGeoLayer<Hyena>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, Hyena animatable) {
                if(bone.getName().equals("Miam")) return animatable.getMainHandItem();
                return super.getStackForBone(bone, animatable);
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, Hyena animatable) {
                if(bone.getName().equals("Miam")) return  ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                return super.getTransformTypeForStack(bone, stack, animatable);
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, Hyena animatable,
                                              MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.translate(1/8f,1/8f,-1f/8);

                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(45));

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });

        addRenderLayer(new HyenaPatternLayer(this));
        addRenderLayer(new HyenaEyesLayer(this));
    }

    @Override
    public void render(Hyena entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {

        if(entity.isBaby()) {
            poseStack.scale(0.7f, 0.7f, 0.7f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
