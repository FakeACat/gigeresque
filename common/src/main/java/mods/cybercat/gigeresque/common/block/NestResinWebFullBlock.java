package mods.cybercat.gigeresque.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import mods.cybercat.gigeresque.CommonMod;
import mods.cybercat.gigeresque.Constants;
import mods.cybercat.gigeresque.common.entity.AlienEntity;
import mods.cybercat.gigeresque.common.entity.GigPlayer;
import mods.cybercat.gigeresque.common.status.effect.GigStatusEffects;
import mods.cybercat.gigeresque.common.util.GigEntityUtils;

public class NestResinWebFullBlock extends AbstractNestBlock {

    private static final Vec3 STUCK_SPEED_MULTIPLIER = new Vec3(0.25, 0.05, 0.25);
    private static final int TICKS_UNTIL_PLAYER_STARTS_GETTING_EGGMORPHED = Constants.TPS * 10;

    public NestResinWebFullBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (!(entity instanceof LivingEntity living) || (entity instanceof AlienEntity)) return;
        if (Constants.isCreativeSpecPlayer.test(entity)) return;
        if (!GigEntityUtils.isTargetHostable(living)) return;

        entity.makeStuckInBlock(state, STUCK_SPEED_MULTIPLIER);

        if (!GigEntityUtils.inResinEnoughToBeEggmorphed(entity)) return;
        if (entity instanceof GigPlayer p) {
            assert entity instanceof Player;
            if (p.ticksInResin() < TICKS_UNTIL_PLAYER_STARTS_GETTING_EGGMORPHED) {
                p.setTicksInResin(p.ticksInResin() + 1);
                return;
            }
        }

        if (living.hasEffect(GigStatusEffects.IMPREGNATION) || living.hasEffect(GigStatusEffects.EGGMORPHING)) return;

        living.addEffect(
            new MobEffectInstance(GigStatusEffects.EGGMORPHING, (int) CommonMod.config.getEggmorphTickTimer(), 0, false, false)
        );
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(
        @NotNull BlockState state,
        @NotNull BlockGetter world,
        @NotNull BlockPos pos,
        @NotNull CollisionContext context
    ) {
        return (context instanceof EntityCollisionContext ctx) && (ctx.getEntity() instanceof AlienEntity)
            ? Block.box(0, 0, 0, 0, 0, 0)
            : super.getCollisionShape(state, world, pos, context);
    }

}
