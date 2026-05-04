package mods.cybercat.gigeresque.client.entity.render.feature;

import mod.azure.azurelib.common.model.AzBone;
import mod.azure.azurelib.common.render.AzRendererPipeline;
import mod.azure.azurelib.common.render.AzRendererPipelineContext;
import mod.azure.azurelib.common.render.layer.AzRenderLayer;
import net.minecraft.client.renderer.RenderType;

import java.util.UUID;

import mods.cybercat.gigeresque.client.entity.texture.EntityTextures;
import mods.cybercat.gigeresque.common.entity.AlienEntity;

public class BloodAzLayer<T extends AlienEntity> implements AzRenderLayer<UUID, T> {

    @Override
    public void preRender(AzRendererPipelineContext<UUID, T> context) {}

    @Override
    public void render(AzRendererPipelineContext<UUID, T> context) {
        T alien = context.animatable();
        AzRendererPipeline<UUID, T> renderPipeline = context.rendererPipeline();
        var renderType = RenderType.entityTranslucentCull(EntityTextures.CHESTBURSTER_BLOOD);
        var maxGrowthTicks = alien.type.growth().maxTimeTicks();
        var maxGrowthForBloodFadeout = maxGrowthTicks / 2;
        if (alien.growthTimeTicks() < maxGrowthForBloodFadeout && alien.isAlive()) {
            context.setRenderType(renderType);
            context.setVertexConsumer(context.multiBufferSource().getBuffer(renderType));
            var progress = ((float) maxGrowthForBloodFadeout - alien.growthTimeTicks()) / maxGrowthForBloodFadeout;
            var alpha = (int) (progress * 0xFF) << 24;
            var color = (context.renderColor() & 0xFFFFFF) | alpha;
            context.setRenderColor(color);
            renderPipeline.reRender(context);
        }
    }

    @Override
    public void renderForBone(AzRendererPipelineContext<UUID, T> context, AzBone bone) {}
}
