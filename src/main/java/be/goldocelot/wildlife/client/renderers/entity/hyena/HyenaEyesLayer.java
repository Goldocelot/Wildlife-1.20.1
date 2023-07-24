package be.goldocelot.wildlife.client.renderers.entity.hyena;

import be.goldocelot.wildlife.WildLife;
import be.goldocelot.wildlife.world.entity.hyena.Hyena;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class HyenaEyesLayer extends GeoRenderLayer<Hyena> {
    private final RenderType EYE = RenderType.entityTranslucentCull(new ResourceLocation(WildLife.MOD_ID, "textures/entity/hyena/hyena_texture_sleep.png"));

    public HyenaEyesLayer(GeoRenderer<Hyena> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(PoseStack poseStack, Hyena animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if(!animatable.isResting()) return;

        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, EYE,
                bufferSource.getBuffer(EYE), partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                1, 1, 1, 1);
    }
}
