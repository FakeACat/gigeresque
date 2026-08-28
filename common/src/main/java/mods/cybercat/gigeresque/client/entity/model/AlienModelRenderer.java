package mods.cybercat.gigeresque.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.common.render.AzLayerRenderer;
import mod.azure.azurelib.common.render.AzRendererPipeline;
import mod.azure.azurelib.common.render.entity.AzEntityModelRenderer;
import mod.azure.azurelib.common.render.entity.AzEntityRendererPipeline;
import org.joml.Math;

import java.util.UUID;

import mods.cybercat.gigeresque.common.Simple;
import mods.cybercat.gigeresque.common.entity.AlienEntity;

@Deprecated
public class AlienModelRenderer<T extends AlienEntity> extends AzEntityModelRenderer<T> {

    // this exists to let us use AlienModelRenderer::new in AzEntityRendererConfig builders
    public AlienModelRenderer(
        AzRendererPipeline<UUID, T> entityRendererPipeline,
        AzLayerRenderer<UUID, T> layerRenderer
    ) {
        super((AzEntityRendererPipeline<T>) entityRendererPipeline, layerRenderer);
    }

    public AlienModelRenderer(
        AzEntityRendererPipeline<T> entityRendererPipeline,
        AzLayerRenderer<UUID, T> layerRenderer
    ) {
        super(entityRendererPipeline, layerRenderer);
    }

    @Override
    protected void applyRotations(
        T animatable,
        PoseStack poseStack,
        float ageInTicks,
        float rotationYaw,
        float partialTick,
        float nativeScale
    ) {
        if (animatable.climbingManager.climbing) {
            applyClimbingRotations(animatable, poseStack, partialTick);
            return;
        }

        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
    }

    private static void applyClimbingRotations(AlienEntity alien, PoseStack poseStack, float partialTick) {
        var forward = alien.climbingManager.oldForward.lerp(alien.climbingManager.forward, partialTick).scale(-1);
        var up = alien.climbingManager.oldUp.lerp(alien.climbingManager.up, partialTick);
        poseStack.rotateAround(Simple.quaternionFromDirection(forward, up), 0, alien.getBbHeight() / 2.0f, 0);
        var distToBlock = Math.lerp(alien.climbingManager.oldDistFromBlock, alien.climbingManager.distFromBlock, partialTick) - alien
            .getBbHeight() / 2;
        poseStack.translate(0, -distToBlock, 0);
    }

}
