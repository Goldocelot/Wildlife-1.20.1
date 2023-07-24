package be.goldocelot.wildlife.client.renderers.entity.hyena;

import be.goldocelot.wildlife.client.models.entity.HyenaModel;
import be.goldocelot.wildlife.world.entity.hyena.Hyena;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import javax.annotation.Nullable;

public class HyenaRenderer extends GeoEntityRenderer<Hyena> {
    private int currentTick = -1;
    public HyenaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HyenaModel());

        this.shadowRadius = 0.45f;

        addRenderLayer(new HyenaPatternLayer(this));
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

    @Override
    public void renderFinal(PoseStack poseStack, Hyena animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.currentTick < 0 || this.currentTick != animatable.tickCount) {
            this.currentTick = animatable.tickCount;

            this.model.getBone("Miam").ifPresent(mouth -> {
                if(!animatable.isChewing()) return;
                RandomSource rand = animatable.getRandom();
                Vector3d mouthPos = mouth.getWorldPosition();

                ItemParticleOption itemParticleOption = new ItemParticleOption(ParticleTypes.ITEM, animatable.getMainHandItem());
                animatable.getCommandSenderWorld().addParticle(itemParticleOption,
                        mouthPos.x(),
                        mouthPos.y(),
                        mouthPos.z(),
                        (rand.nextDouble()-0.5)/10,
                        0,
                        (rand.nextDouble()-0.5)/10);
            });
        }


        super.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
