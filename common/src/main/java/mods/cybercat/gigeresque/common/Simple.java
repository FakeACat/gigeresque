package mods.cybercat.gigeresque.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

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
}
