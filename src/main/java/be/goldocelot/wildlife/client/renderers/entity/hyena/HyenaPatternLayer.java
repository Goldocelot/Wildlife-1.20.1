package be.goldocelot.wildlife.client.renderers.entity.hyena;

import be.goldocelot.wildlife.WildLife;
import be.goldocelot.wildlife.world.entity.hyena.Hyena;
import be.goldocelot.wildlife.world.entity.hyena.HyenaPattern;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class HyenaPatternLayer extends GeoRenderLayer<Hyena> {
    public HyenaPatternLayer(GeoRenderer<Hyena> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(PoseStack poseStack, Hyena animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        HyenaPattern pattern = animatable.getHyenaPattern();
        if(pattern == HyenaPattern.NONE) return;

        RenderType layerRenderType = RenderType.entityTranslucentCull(new ResourceLocation(WildLife.MOD_ID, "textures/entity/hyena/hyena_texture_pattern_"+pattern.toString().toLowerCase()+".png"));

        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, renderType,
                bufferSource.getBuffer(layerRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                1, 1, 1, 1);
    }
}

