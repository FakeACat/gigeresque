package mods.cybercat.gigeresque.client.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.controller.keyframe.AzKeyframeCallbacks;
import mod.azure.azurelib.common.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.common.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehaviors;
import mod.azure.azurelib.common.render.AzLayerRenderer;
import mod.azure.azurelib.common.render.entity.AzEntityModelRenderer;
import mod.azure.azurelib.common.render.entity.AzEntityRenderer;
import mod.azure.azurelib.common.render.entity.AzEntityRendererConfig;
import mod.azure.azurelib.common.render.entity.AzEntityRendererPipeline;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import mods.cybercat.gigeresque.Constants;
import mods.cybercat.gigeresque.client.entity.model.EntityModels;
import mods.cybercat.gigeresque.client.entity.texture.EntityTextures;
import mods.cybercat.gigeresque.common.Simple;
import mods.cybercat.gigeresque.common.entity.Alien;
import mods.cybercat.gigeresque.common.sound.GigSounds;

public class AlienRenderer extends AzEntityRenderer<Alien> {

    public AlienRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.<Alien>builder(EntityModels.ALIEN, EntityTextures.ALIEN)
                .setAnimatorProvider(Animator::new)
                .setRenderEntry(renderEntry -> {
                    animate(renderEntry.animatable());
                    return renderEntry;
                })
                .setModelRenderer((pipeline, layerRenderer) -> new ModelRenderer((AzEntityRendererPipeline<Alien>) pipeline, layerRenderer))
                .build(),
            context
        );
    }

    public static final String MOVEMENT_CONTROLLER = "controller";

    public static final AzCommand IDLE_LAND = AzCommand.create(MOVEMENT_CONTROLLER, "idle_land", AzPlayBehaviors.LOOP);
    public static final AzCommand RUN = AzCommand.create(MOVEMENT_CONTROLLER, "run", AzPlayBehaviors.LOOP);
    public static final AzCommand CRAWL = AzCommand.create(MOVEMENT_CONTROLLER, "crawl", AzPlayBehaviors.LOOP);

    public static void animate(Alien alien) {
        switch (alien.movement) {
            case RUNNING -> RUN.sendForEntity(alien);
            case CLIMBING -> CRAWL.sendForEntity(alien);
        }
    }

    public static class ModelRenderer extends AzEntityModelRenderer<Alien> {
        public ModelRenderer(AzEntityRendererPipeline<Alien> pipeline, AzLayerRenderer<UUID, Alien> layerRenderer) {
            super(pipeline, layerRenderer);
        }

        @Override
        protected void applyRotations(
            Alien alien,
            PoseStack poseStack,
            float ageInTicks,
            float rotationYaw,
            float partialTick,
            float nativeScale
        ) {
            var forward = new Vec3(
                alien.forward.x * partialTick + alien.clientPreviousForward.x * (1 - partialTick),
                alien.forward.y * partialTick + alien.clientPreviousForward.y * (1 - partialTick),
                alien.forward.z * partialTick + alien.clientPreviousForward.z * (1 - partialTick)
            ).scale(-1);
            var up = new Vec3(
                alien.up.x * partialTick + alien.clientPreviousUp.x * (1 - partialTick),
                alien.up.y * partialTick + alien.clientPreviousUp.y * (1 - partialTick),
                alien.up.z * partialTick + alien.clientPreviousUp.z * (1 - partialTick)
            );
            poseStack.rotateAround(Simple.quaternionFromDirection(forward, up), 0, alien.getBbHeight() / 2.0f, 0);
            poseStack.translate(0, alien.verticalOffset * partialTick + alien.clientPreviousVerticalOffset * (1 - partialTick), 0);
        }
    }

    public static class Animator extends AzEntityAnimator<Alien> {
        @Override
        public void registerControllers(AzAnimationControllerContainer<Alien> container) {
            container.add(
                AzAnimationController.builder(this, MOVEMENT_CONTROLLER)
                    .setTransitionLength(5)
                    .setKeyframeCallbacks(
                        AzKeyframeCallbacks.<Alien>builder()
                            .setSoundKeyframeHandler(
                                event -> {
                                    if (event.getKeyframeData().getSound().equals("footstepSoundkey")) {
                                        event.getAnimatable()
                                            .level()
                                            .playLocalSound(
                                                event.getAnimatable().getX(),
                                                event.getAnimatable().getY(),
                                                event.getAnimatable().getZ(),
                                                GigSounds.ALIEN_FOOTSTEP.get(),
                                                SoundSource.HOSTILE,
                                                0.5F,
                                                1.0F,
                                                true
                                            );
                                    }
                                    if (event.getKeyframeData().getSound().equals("handstepSoundkey")) {
                                        event.getAnimatable()
                                            .level()
                                            .playLocalSound(
                                                event.getAnimatable().getX(),
                                                event.getAnimatable().getY(),
                                                event.getAnimatable().getZ(),
                                                GigSounds.ALIEN_HANDSTEP.get(),
                                                SoundSource.HOSTILE,
                                                0.5F,
                                                1.0F,
                                                true
                                            );
                                    }
                                    if (event.getKeyframeData().getSound().equals("ambientSoundkey")) {
                                        event.getAnimatable()
                                            .level()
                                            .playLocalSound(
                                                event.getAnimatable().getX(),
                                                event.getAnimatable().getY(),
                                                event.getAnimatable().getZ(),
                                                GigSounds.ALIEN_AMBIENT.get(),
                                                SoundSource.HOSTILE,
                                                0.5F,
                                                1.0F,
                                                true
                                            );
                                    }
                                    if (event.getKeyframeData().getSound().equals("thudSoundkey")) {
                                        event.getAnimatable()
                                            .level()
                                            .playLocalSound(
                                                event.getAnimatable().getX(),
                                                event.getAnimatable().getY(),
                                                event.getAnimatable().getZ(),
                                                GigSounds.ALIEN_DEATH_THUD.get(),
                                                SoundSource.HOSTILE,
                                                0.5F,
                                                1.0F,
                                                true
                                            );
                                    }
                                    if (event.getKeyframeData().getSound().equals("biteSoundkey")) {
                                        event.getAnimatable()
                                            .level()
                                            .playLocalSound(
                                                event.getAnimatable().getX(),
                                                event.getAnimatable().getY(),
                                                event.getAnimatable().getZ(),
                                                GigSounds.ALIEN_HEADBITE.get(),
                                                SoundSource.HOSTILE,
                                                0.5F,
                                                1.0F,
                                                true
                                            );
                                    }
                                    if (event.getKeyframeData().getSound().equals("crunchSoundkey")) {
                                        event.getAnimatable()
                                            .level()
                                            .playLocalSound(
                                                event.getAnimatable().getX(),
                                                event.getAnimatable().getY(),
                                                event.getAnimatable().getZ(),
                                                GigSounds.ALIEN_CRUNCH.get(),
                                                SoundSource.HOSTILE,
                                                0.5F,
                                                1.0F,
                                                true
                                            );
                                    }
                                }
                            )
                            .build()
                    )
                    .build()
            );
        }

        public static final ResourceLocation TOMBY_ANIMATIONS = Constants.modResource("animations/entity/alien/alien.animation.json");

        @Override
        public @NotNull ResourceLocation getAnimationLocation(Alien alien) {
            return TOMBY_ANIMATIONS;
        }
    }

}
