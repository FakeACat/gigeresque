package mods.cybercat.gigeresque.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import mods.cybercat.gigeresque.CommonMod;

public class PandoraSpawning {
    public static final int TICKS_BETWEEN_SPAWNS = 6000;

    private static final int MIN_SPAWN_DISTANCE = 16;
    private static final int MAX_SPAWN_DISTANCE = 48;

    private static final int SPAWN_DISTANCE_RANGE = MAX_SPAWN_DISTANCE - MIN_SPAWN_DISTANCE;
    private static final int SPAWN_AREA_CENTRE_DISTANCE = (MIN_SPAWN_DISTANCE + MAX_SPAWN_DISTANCE) / 2;
    private static final int SPAWN_AREA_SIDE_LENGTH = (int) Math.sqrt(SPAWN_DISTANCE_RANGE * SPAWN_DISTANCE_RANGE / 2.0);

    public static class State extends SavedData {
        public boolean enabled;
        public int ticksSinceLastSpawn;

        @Override
        public @NotNull CompoundTag save(CompoundTag tag, @NotNull Provider registries) {
            tag.putBoolean("enabled", enabled);
            tag.putInt("ticksSinceLastSpawn", ticksSinceLastSpawn);

            return tag;
        }

        private static final Factory<State> FACTORY = new Factory<>(State::new, (tag, provider) -> {
            var s = new State();

            s.enabled = tag.getBoolean("enabled");
            s.ticksSinceLastSpawn = tag.getInt("ticksSinceLastSpawn");

            return s;
        }, DataFixTypes.LEVEL); // we don't need a data fixer but as far as i can tell we need to supply one

        public static State get(ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(FACTORY, "pandora_spawning");
        }

        @Override
        public boolean isDirty() {
            return true; // we're updating every tick after spawning begins anyway..
        }
    }

    public static void tick(ServerLevel level) {
        var state = State.get(level);
        if (!state.enabled || (level.getDifficulty() == Difficulty.PEACEFUL)) return;

        if (state.ticksSinceLastSpawn < TICKS_BETWEEN_SPAWNS) {
            state.ticksSinceLastSpawn += 1;
            return;
        }
        state.ticksSinceLastSpawn = 0;

        var suitablePlayers = new ArrayList<Player>();
        for (var p : level.players()) {
            if (p.isCreative() || p.isSpectator()) continue;
            // TODO(acats) also remove players in dungeons
            suitablePlayers.add(p);
        }
        if (suitablePlayers.isEmpty()) return;

        var target = suitablePlayers.get(level.random.nextInt(suitablePlayers.size()));
        var targetLookDirection = target.calculateViewVector(0, target.getYRot());
        var spawnAreaCentreOffset = targetLookDirection.scale(-SPAWN_AREA_CENTRE_DISTANCE);

        var spawnAreaCentreBlock = BlockPos.containing(spawnAreaCentreOffset.add(target.position()));

        var spawnPosition = new BlockPos.MutableBlockPos();
        final int tries = 1000;
        for (int i = 0; i < tries; i += 1) {
            spawnPosition.setX(spawnAreaCentreBlock.getX() + level.random.nextInt(SPAWN_AREA_SIDE_LENGTH) - SPAWN_AREA_SIDE_LENGTH / 2);
            spawnPosition.setY(spawnAreaCentreBlock.getY() + level.random.nextInt(SPAWN_AREA_SIDE_LENGTH) - SPAWN_AREA_SIDE_LENGTH / 2);
            spawnPosition.setZ(spawnAreaCentreBlock.getZ() + level.random.nextInt(SPAWN_AREA_SIDE_LENGTH) - SPAWN_AREA_SIDE_LENGTH / 2);

            if (level.getBrightness(LightLayer.BLOCK, spawnPosition) > 0) continue;
            if (level.getBrightness(LightLayer.SKY, spawnPosition) > 0) continue;

            if (CommonMod.config.generalConfigs.enableLogging) {
                CommonMod.LOGGER.info(
                    "successful pandora spawn at {}, {}, {} (relative to player: {}, {}, {})",
                    spawnPosition.getX(),
                    spawnPosition.getY(),
                    spawnPosition.getZ(),
                    spawnPosition.getX() - target.blockPosition().getX(),
                    spawnPosition.getY() - target.blockPosition().getY(),
                    spawnPosition.getZ() - target.blockPosition().getZ()
                );
            }
            level.setBlockAndUpdate(spawnPosition, Blocks.BEDROCK.defaultBlockState());
            break;
        }
    }
}
