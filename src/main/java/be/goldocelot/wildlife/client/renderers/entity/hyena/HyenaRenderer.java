package be.goldocelot.wildlife.client.renderers.entity.hyena;

import be.goldocelot.wildlife.client.models.entity.HyenaModel;
import be.goldocelot.wildlife.world.entity.hyena.Hyena;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HyenaRenderer extends GeoEntityRenderer<Hyena>{

    public HyenaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HyenaModel());

        this.shadowRadius = 0.45f;

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
