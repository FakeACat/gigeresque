package mods.cybercat.gigeresque.common;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;

import mods.cybercat.gigeresque.client.entity.render.AlienRenderer;
import mods.cybercat.gigeresque.common.entity.Alien;
import mods.cybercat.gigeresque.common.entity.GigEntities;

public class ClientMod {
    public static class RendererEntry<T extends Entity> {
        public EntityType<T> associatedType;
        public EntityRendererProvider<T> constructor;
    }

    public static final ArrayList<RendererEntry<?>> RENDERERS = new ArrayList<>();

    public static void init() {
        var alienRenderer = new RendererEntry<Alien>();
        RENDERERS.add(alienRenderer);
        alienRenderer.associatedType = GigEntities.ENTOMBED_INTERLOPER.type;
        alienRenderer.constructor = AlienRenderer::new;
    }
}
