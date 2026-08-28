package mods.cybercat.gigeresque.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;

// Simple methods for things that really should be simple in vanilla
public class Simple {
    public static void giveAdvancement(ServerPlayer player, ResourceLocation advancementId) {
        var advancement = player.server.getAdvancements().get(advancementId);

        if (advancement == null) {
            Log.error("unable to find advancement '%s'", advancementId.toString());
            return;
        }

        var progress = player.getAdvancements().getOrStartProgress(advancement);
        if (progress.isDone()) return;

        for (var s : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, s);
    }

    public static int hash(int x, int y, int z) {
        // borrowed from vanilla node class
        return y & 0xFF | (x & 32767) << 8 | (z & 32767) << 24 | (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? 32768 : 0);
    }

    public static boolean canFitStandingAt(ServerLevel level, float x, float y, float z, float width, float height) {
        var boundingBox = new AABB(x - width / 2, y, z - width / 2, x + width / 2, y + height, z + width / 2);
        return level.noBlockCollision(null, boundingBox);
    }

    public static boolean canFitCentredAt(ServerLevel level, float x, float y, float z, float width, float height) {
        var boundingBox = new AABB(x - width / 2, y - height / 2, z - width / 2, x + width / 2, y + height / 2, z + width / 2);
        return level.noBlockCollision(null, boundingBox);
    }

    public static Matrix4f rotationMatrixFromDirection(Vec3 forward, Vec3 up) {
        var xAxis = up.cross(forward).normalize();
        var yAxis = forward.cross(xAxis).normalize();
        var zAxis = xAxis.cross(yAxis).normalize();

        if (xAxis.equals(yAxis) || xAxis.equals(zAxis) || yAxis.equals(zAxis)) {
            return new Matrix4f(
                1.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                1.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                1.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                1.0f
            );
        }

        return new Matrix4f(
            (float) xAxis.x,
            (float) xAxis.y,
            (float) xAxis.z,
            0.0f,
            (float) yAxis.x,
            (float) yAxis.y,
            (float) yAxis.z,
            0.0f,
            (float) zAxis.x,
            (float) zAxis.y,
            (float) zAxis.z,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            1.0f
        );
    }

    public static Quaternionf quaternionFromDirection(Vec3 forward, Vec3 up) {
        return rotationMatrixFromDirection(forward, up).getNormalizedRotation(new Quaternionf());
    }

    public static Vec3 closestBlockCollision(Level level, Vec3 pos, double range, double precision) {
        double currentPrecision = range * 2;

        var points = new ArrayList<Vec3>();
        points.add(pos);

        var pointsNext = new ArrayList<Vec3>();

        Vec3 closestSoFar = null;

        while (!points.isEmpty()) {
            double halfCurrentPrecision = currentPrecision / 2;
            boolean finalCheck = halfCurrentPrecision <= precision;

            double closestDistSqSoFar = Double.MAX_VALUE;

            for (var point : points) {
                var distSq = point.distanceToSqr(pos);

                if (distSq > closestDistSqSoFar)
                    continue;

                if (
                    level.noBlockCollision(
                        null,
                        new AABB(
                            point.x - halfCurrentPrecision,
                            point.y - halfCurrentPrecision,
                            point.z - halfCurrentPrecision,
                            point.x + halfCurrentPrecision,
                            point.y + halfCurrentPrecision,
                            point.z + halfCurrentPrecision
                        )
                    )
                )
                    continue;

                closestDistSqSoFar = distSq;
                closestSoFar = point;

                if (!finalCheck) {
                    var dist = currentPrecision / 3;
                    for (int i = -1; i < 2; i++) {
                        for (int j = -1; j < 2; j++) {
                            for (int k = -1; k < 2; k++) {
                                pointsNext.add(point.add(i * dist, j * dist, k * dist));
                            }
                        }
                    }
                }
            }

            points = pointsNext;
            pointsNext = new ArrayList<>();

            currentPrecision /= 3;
        }

        return closestSoFar;
    }
}
