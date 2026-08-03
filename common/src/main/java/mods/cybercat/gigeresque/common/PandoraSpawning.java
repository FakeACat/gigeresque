package mods.cybercat.gigeresque.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import mods.cybercat.gigeresque.common.block.GigBlocks;
import mods.cybercat.gigeresque.common.block.NestResinBlock;
import mods.cybercat.gigeresque.common.entity.GigEntities;

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
        if (!state.enabled || level.getDifficulty() == Difficulty.PEACEFUL) return;

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

        var pos = new BlockPos.MutableBlockPos();
        final int tries = 1000;
        for (int i = 0; i < tries; i += 1) {
            pos.setX(spawnAreaCentreBlock.getX() + level.random.nextInt(SPAWN_AREA_SIDE_LENGTH) - SPAWN_AREA_SIDE_LENGTH / 2);
            pos.setY(spawnAreaCentreBlock.getY() + level.random.nextInt(SPAWN_AREA_SIDE_LENGTH) - SPAWN_AREA_SIDE_LENGTH / 2);
            pos.setZ(spawnAreaCentreBlock.getZ() + level.random.nextInt(SPAWN_AREA_SIDE_LENGTH) - SPAWN_AREA_SIDE_LENGTH / 2);

            if (spawnSuitability(level, pos) != SpawnSuitability.VALID) continue;

            doSpawn(level, pos);
            Log.info(
                "successful pandora spawn at %d, %d, %d (relative to player: %d, %d, %d)",
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                pos.getX() - target.blockPosition().getX(),
                pos.getY() - target.blockPosition().getY(),
                pos.getZ() - target.blockPosition().getZ()
            );
            break;
        }
    }

    public enum SpawnSuitability { INVALID, NO_SURFACE, VALID }

    public static SpawnSuitability spawnSuitability(ServerLevel level, BlockPos pos) {
        if (level.getBrightness(LightLayer.BLOCK, pos) > 0) return SpawnSuitability.INVALID;
        if (level.getBrightness(LightLayer.SKY, pos) > 0) return SpawnSuitability.INVALID;

        // we're assuming all potential spawns can fit in 1x1x1
        if (!level.noCollision(new AABB(pos))) return SpawnSuitability.INVALID;
        if (level.getFluidState(pos).is(Fluids.LAVA)) return SpawnSuitability.INVALID;

        if (!level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) return SpawnSuitability.NO_SURFACE;

        return SpawnSuitability.VALID;
    }

    public static void doSpawn(ServerLevel level, BlockPos centre) {
        // NOTE(acats) when we add vents, we must!!! add an extra check since right now there is no guarantee that it is
        // ok to replace the block at/below where we're spawning stuff

        int count;
        var types = new ArrayList<EntityType<?>>();

        if (centre.getY() > 63) count = 1;
        else if (centre.getY() > 31) count = 2;
        else if (centre.getY() > -33) count = 3;
        else count = 4;

        // sometimes spawn more for no reason
        for (int i = 0; i < 3; i += 1) if (level.random.nextInt(20) == 0) count += 1;

        var fluidState = level.getFluidState(centre);
        var underwater = fluidState.is(Fluids.WATER) && fluidState.isSource();

        if (underwater) types.add(GigEntities.AQUA_EGG.get());
        else types.add(GigEntities.EGG.get());

        if (centre.getY() < 64 || level.random.nextInt(10) == 0) types.add(GigEntities.FACEHUGGER.get());

        if (centre.getY() < 32 || level.random.nextInt(20) == 0) if (underwater) {
            types.add(GigEntities.AQUATIC_CHESTBURSTER.get());
        } else {
            types.add(GigEntities.CHESTBURSTER.get());
            types.add(GigEntities.RUNNERBURSTER.get());
        }

        var positions = new ArrayList<BlockPos>();
        positions.add(centre);

        findSpawnPositions: while (positions.size() < count) {
            var previous = positions.toArray(new BlockPos[] {});
            for (var pos : previous) for (var dirFromPrev : Direction.allShuffled(level.random)) {

                var neighbour = pos.relative(dirFromPrev);
                var suitability = spawnSuitability(level, neighbour);

                if (suitability == SpawnSuitability.VALID && !positions.contains(neighbour)) {
                    positions.add(neighbour);
                    if (positions.size() == count) break findSpawnPositions;

                    continue;
                }

                if (suitability != SpawnSuitability.NO_SURFACE) continue;

                if (dirFromPrev == Direction.UP) {
                    for (var dirFromNeighbour : Direction.Plane.HORIZONTAL.shuffledCopy(level.random)) {
                        var stepUp = neighbour.relative(dirFromNeighbour);

                        if (spawnSuitability(level, stepUp) == SpawnSuitability.VALID && !positions.contains(stepUp)) {
                            positions.add(stepUp);
                            if (positions.size() == count) break findSpawnPositions;
                        }
                    }

                    continue;
                }

                if (dirFromPrev.getAxis().isHorizontal()) {
                    var below = neighbour.below();

                    if (spawnSuitability(level, below) == SpawnSuitability.VALID && !positions.contains(below)) {
                        positions.add(below);
                        if (positions.size() == count) break findSpawnPositions;
                    }

                    continue;
                }
            }

            if (positions.size() == previous.length) break;
        }

        assert positions.size() <= count;
        // TODO(acats) if positions.size() < count we should put remaining spawns in ventspace probably

        var i = 0;
        for (var pos : positions) {
            i += 1;

            // since positions are added in order from the centre, this is reliable
            var distanceNormalised = (float) i / positions.size();

            // placeholder
            placeResin: {
                if (!level.getBlockState(pos).canBeReplaced()) break placeResin;

                final int MAX_LAYERS = 3;
                var layers = MAX_LAYERS - (int) (distanceNormalised * MAX_LAYERS) + 1;
                if (layers > MAX_LAYERS) layers = MAX_LAYERS;

                var resin = GigBlocks.NEST_RESIN.get().defaultBlockState().setValue(NestResinBlock.LAYERS, layers);

                level.setBlockAndUpdate(pos, resin);
            }

            var entity = types.get(level.random.nextInt(types.size())).create(level);
            assert entity != null;

            entity.setPos(pos.getX() + .5, pos.getY(), pos.getZ() + .5);

            level.addFreshEntity(entity);
        }
    }
}
