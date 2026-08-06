package mods.cybercat.gigeresque.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

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
}
