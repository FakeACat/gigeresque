package mods.cybercat.gigeresque;

import mod.azure.azurelib.AzureLibMod;
import mod.azure.azurelib.common.config.format.ConfigFormats;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mods.cybercat.gigeresque.client.particle.GigParticles;
import mods.cybercat.gigeresque.common.PandoraSpawning;
import mods.cybercat.gigeresque.common.block.GigBlocks;
import mods.cybercat.gigeresque.common.config.GigeresqueConfig;
import mods.cybercat.gigeresque.common.entity.GigEntities;
import mods.cybercat.gigeresque.common.fluid.GigFluids;
import mods.cybercat.gigeresque.common.item.GigItems;
import mods.cybercat.gigeresque.common.item.group.GigItemGroups;
import mods.cybercat.gigeresque.common.sound.GigSounds;
import mods.cybercat.gigeresque.common.status.effect.GigStatusEffects;

public record CommonMod() {

    public static final String MOD_ID = "gigeresque";

    @Deprecated // TODO(acats) remove in favour of the custom Log class
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static GigeresqueConfig config;

    public static void initRegistries() {
        config = AzureLibMod.registerConfig(GigeresqueConfig.class, ConfigFormats.json()).getConfigInstance();
        GigFluids.initialize();
        GigEntities.initialize();
        GigBlocks.initialize();
        GigItemGroups.initialize();
        GigItems.initialize();
        GigSounds.initialize();
        GigStatusEffects.initialize();
        GigParticles.initialize();
    }

    public static void beforeLevelTick(ServerLevel level) {
        PandoraSpawning.tick(level);
    }

    public static void afterLevelTick(ServerLevel level) {
        boolean hasAdvancement = false;

        for (ServerPlayer player : level.getPlayers(player -> true)) {
            var advancement = player.server.getAdvancements().get(Constants.modResource("xeno_dungeon"));
            if (advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone()) {
                hasAdvancement = true;
                break;
            }
        }

        if (hasAdvancement) PandoraSpawning.State.get(level).enabled = true;
    }
}
