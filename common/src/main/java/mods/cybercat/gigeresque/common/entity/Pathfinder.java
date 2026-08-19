package mods.cybercat.gigeresque.common.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

import java.util.ArrayDeque;

import mods.cybercat.gigeresque.common.Log;
import mods.cybercat.gigeresque.common.Simple;

// loosely ported from From Another World 2
public class Pathfinder {
    public enum NavigationType { NOT_NAVIGABLE, WALKABLE, CLIMBABLE }

    public static class Node {
        public NavigationType navigation;

        public int x;
        public int y;
        public int z;

        // multiplied by 10 to get a little more precision
        public int approxDistanceFromTarget10x;
        public int distanceFromStart10x;

        public Node previous;
    }

    public interface DistanceFunction {
        int call(int x, int y, int z);
    }

    public int maxSteps = 10000;
    public int stepsPerTick = 10;

    public ServerLevel level;
    public DistanceFunction distanceFunction;
    public float biasTowardsFollowingDistanceFunction = 1.5f;

    public float standingWidth;
    public float standingHeight;

    public boolean canClimb;
    public float climbingWidth;
    public float climbingHeight;
    public float climbingReach = .5f;

    public int initialStartX;
    public int initialStartY;
    public int initialStartZ;

    public final Int2ObjectOpenHashMap<Node> openNodes = new Int2ObjectOpenHashMap<>();
    public final Int2ObjectOpenHashMap<Node> closedNodes = new Int2ObjectOpenHashMap<>();

    public int steps;
    public final ArrayDeque<Node> path = new ArrayDeque<>();

    public enum Status { RUNNING, DONE, FAILED }
    public Status status;

    public void workOnPath(int startX, int startY, int startZ) {
        // assume that we only want to start generating a new path once all the nodes are popped off the old path
        if (status == Status.DONE && !path.isEmpty()) return;

        if (
            status == Status.FAILED || status == Status.DONE ||
                initialStartX != startX || initialStartY != startY || initialStartZ != startZ
        ) {
            initialStartX = startX;
            initialStartY = startY;
            initialStartZ = startZ;

            openNodes.clear();
            closedNodes.clear();

            steps = 0;
            path.clear();

            status = Status.RUNNING;

            addOrUpdateNodeAt(startX, startY, startZ, null);
        }

        if (steps > maxSteps) {
            Log.info("pathfinding failed: reached max steps");
            status = Status.FAILED;
            return;
        }

        for (int i = 0; i < stepsPerTick; i += 1) {
            var lowestCost = Integer.MAX_VALUE;
            Node best = null;

            for (var n : openNodes.values()) {
                var cost = n.distanceFromStart10x + n.approxDistanceFromTarget10x;
                if (cost < lowestCost) {
                    best = n;
                    lowestCost = cost;
                }
            }

            if (best == null) {
                Log.info("pathfinding failed: ran out of explorable nodes before finding target");
                status = Status.FAILED;
                return;
            }

            if (best.approxDistanceFromTarget10x == 0) {
                Log.info("pathfinding succeeded!!");
                status = Status.DONE;

                var node = best;
                while (node != null) {
                    path.addFirst(node);
                    node = node.previous;
                }

                return;
            }

            var hash = Simple.hash(best.x, best.y, best.z);
            openNodes.remove(hash, best);
            closedNodes.put(hash, best);

            var px = addOrUpdateNodeAt(best.x + 1, best.y, best.z, best);
            var nx = addOrUpdateNodeAt(best.x - 1, best.y, best.z, best);
            var py = addOrUpdateNodeAt(best.x, best.y + 1, best.z, best);
            var ny = addOrUpdateNodeAt(best.x, best.y - 1, best.z, best);
            var pz = addOrUpdateNodeAt(best.x, best.y, best.z + 1, best);
            var nz = addOrUpdateNodeAt(best.x, best.y, best.z - 1, best);

            if (px != null && py != null) addOrUpdateNodeAt(best.x + 1, best.y + 1, best.z, best);
            if (px != null && ny != null) addOrUpdateNodeAt(best.x + 1, best.y - 1, best.z, best);
            if (px != null && pz != null) addOrUpdateNodeAt(best.x + 1, best.y, best.z + 1, best);
            if (px != null && nz != null) addOrUpdateNodeAt(best.x + 1, best.y, best.z - 1, best);

            if (nx != null && py != null) addOrUpdateNodeAt(best.x - 1, best.y + 1, best.z, best);
            if (nx != null && ny != null) addOrUpdateNodeAt(best.x - 1, best.y - 1, best.z, best);
            if (nx != null && pz != null) addOrUpdateNodeAt(best.x - 1, best.y, best.z + 1, best);
            if (nx != null && nz != null) addOrUpdateNodeAt(best.x - 1, best.y, best.z - 1, best);

            if (py != null && pz != null) addOrUpdateNodeAt(best.x, best.y + 1, best.z + 1, best);
            if (py != null && nz != null) addOrUpdateNodeAt(best.x, best.y + 1, best.z - 1, best);

            if (ny != null && pz != null) addOrUpdateNodeAt(best.x, best.y - 1, best.z + 1, best);
            if (ny != null && nz != null) addOrUpdateNodeAt(best.x, best.y - 1, best.z - 1, best);

            steps += 1;
        }
    }

    public Node addOrUpdateNodeAt(int x, int y, int z, Node previous) {
        var hash = Simple.hash(x, y, z);

        if (closedNodes.containsKey(hash)) return closedNodes.get(hash);

        var navigation = pickNavigationTypeAt(x, y, z);
        var distanceFromStart10x = 0;

        if (previous != null) {
            var crossable = switch (navigation) {
                case NOT_NAVIGABLE -> false;
                case WALKABLE -> true;
                case CLIMBABLE -> canClimb;
            };

            if (!crossable) return null;

            distanceFromStart10x += previous.distanceFromStart10x;

            var numAxesChanged = Math.abs(x - previous.x) + Math.abs(y - previous.y) + Math.abs(z - previous.z);
            distanceFromStart10x += switch (numAxesChanged) {
                case 1 -> 10;
                case 2 -> 14;
                case 3 -> 17;
                default -> {
                    Log.error("unexpected number of axes: %d", numAxesChanged);
                    yield 0;
                }
            };
        }

        if (openNodes.containsKey(hash)) {
            var node = openNodes.get(hash);
            if (node.distanceFromStart10x > distanceFromStart10x) {
                node.navigation = navigation;
                node.distanceFromStart10x = distanceFromStart10x;
                node.previous = previous;
            }
            return node;
        }

        var node = new Node();
        openNodes.put(hash, node);

        node.navigation = navigation;
        node.x = x;
        node.y = y;
        node.z = z;
        node.approxDistanceFromTarget10x = (int) (distanceFunction.call(x, y, z) * 10
            * biasTowardsFollowingDistanceFunction);
        node.distanceFromStart10x = distanceFromStart10x;
        node.previous = previous;

        return node;
    }

    private static final BlockPos.MutableBlockPos REUSABLE_BLOCK_POS = new BlockPos.MutableBlockPos();

    public NavigationType pickNavigationTypeAt(int x, int y, int z) {
        tryWalkable: {
            assert standingWidth > 0;
            assert standingHeight > 0;

            if (!Simple.canFitStandingAt(level, x + .5f, y, z + .5f, standingWidth, standingHeight)) {
                break tryWalkable;
            }

            var below = REUSABLE_BLOCK_POS.set(x, y - 1, z);
            if (!Block.canSupportCenter(level, below, Direction.UP)) break tryWalkable;

            return NavigationType.WALKABLE;
        }

        tryClimbable: {
            if (!canClimb) break tryClimbable;

            assert climbingWidth > 0;
            assert climbingHeight > 0;

            if (!Simple.canFitCentredAt(level, x + .5f, y + .5f, z + .5f, climbingWidth, climbingHeight)) {
                break tryClimbable;
            }

            assert climbingReach > 0;

            var nothingNearbyToClimbOn = Simple.canFitCentredAt(
                level,
                x + .5f,
                y + .5f,
                z + .5f,
                climbingWidth + climbingReach,
                climbingHeight + climbingReach
            );

            if (nothingNearbyToClimbOn) break tryClimbable;

            return NavigationType.CLIMBABLE;
        }

        return NavigationType.NOT_NAVIGABLE;
    }
}
