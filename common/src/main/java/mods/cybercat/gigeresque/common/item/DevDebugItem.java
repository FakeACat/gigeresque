package mods.cybercat.gigeresque.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import mods.cybercat.gigeresque.common.Log;
import mods.cybercat.gigeresque.common.PandoraSpawning;
import mods.cybercat.gigeresque.common.entity.AlienEntity;

public class DevDebugItem extends Item {

    private enum Mode { TOGGLE_ALIEN_STASIS, TRY_TRIGGER_PANDORA_SPAWNING, PRINT_PANDY_INFO }
    private Mode mode = Mode.TOGGLE_ALIEN_STASIS;

    public DevDebugItem() {
        super(new Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
        @NotNull Level level,
        @NotNull Player player,
        @NotNull InteractionHand usedHand
    ) {
        if (!(level instanceof ServerLevel serverLevel)) return super.use(level, player, usedHand);

        if (player.isCrouching()) {
            var modes = Mode.values();
            mode = modes[(mode.ordinal() + 1) % modes.length];
            player.displayClientMessage(Component.literal("changed mode to " + mode), false);
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }

        switch (mode) {
            case Mode.TRY_TRIGGER_PANDORA_SPAWNING -> {
                var state = PandoraSpawning.State.get(serverLevel);
                state.enabled = true;
                state.ticksSinceLastSpawn = PandoraSpawning.TICKS_BETWEEN_SPAWNS;
                return InteractionResultHolder.success(player.getItemInHand(usedHand));
            }
            case Mode.PRINT_PANDY_INFO -> {
                var state = PandoraSpawning.State.get(serverLevel);
                Log.info("enabled: %b, timer: %d", state.enabled, state.ticksSinceLastSpawn);
            }
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
        @NotNull ItemStack stack,
        @NotNull Player player,
        @NotNull LivingEntity target,
        @NotNull InteractionHand usedHand
    ) {
        if ((mode == Mode.TOGGLE_ALIEN_STASIS) && (target instanceof AlienEntity alien)) {
            alien.stasisManager.setStasis(!alien.stasisManager.isStasis());
            return InteractionResult.sidedSuccess(player.level().isClientSide);
        }
        return super.interactLivingEntity(stack, player, target, usedHand);
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack stack,
        @NotNull TooltipContext context,
        @NotNull List<Component> tooltipComponents,
        @NotNull TooltipFlag tooltipFlag
    ) {
        tooltipComponents.add(
            Component.translatable("item.gigeresque.creativeonly.tooltip")
                .withStyle(ChatFormatting.DARK_RED)
                .withStyle(ChatFormatting.ITALIC)
        );
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
