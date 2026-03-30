package mods.cybercat.gigeresque.common.entity.impl.aqua;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import mods.cybercat.gigeresque.CommonMod;
import mods.cybercat.gigeresque.common.entity.AlienEntity;
import mods.cybercat.gigeresque.common.entity.GigEntities;
import mods.cybercat.gigeresque.common.entity.ai.goals.RotateTowardsEntityGoal;
import mods.cybercat.gigeresque.common.entity.ai.goals.attack.BreakBlocksGoal;
import mods.cybercat.gigeresque.common.entity.ai.goals.attack.DelayedAttackGoal;
import mods.cybercat.gigeresque.common.entity.ai.goals.attack.LungeAtTargetGoal;
import mods.cybercat.gigeresque.common.entity.ai.goals.movement.DigToTargetGoal;
import mods.cybercat.gigeresque.common.entity.ai.goals.movement.DodgeProjectilesGoal;
import mods.cybercat.gigeresque.common.entity.ai.goals.movement.FindDarknessGoal;
import mods.cybercat.gigeresque.common.entity.ai.goals.movement.FleeExplodingCreeperGoal;
import mods.cybercat.gigeresque.common.entity.ai.goals.movement.FleeFightGoal;
import mods.cybercat.gigeresque.common.entity.ai.goals.movement.FleeFireGoal;
import mods.cybercat.gigeresque.common.entity.ai.goals.movement.StrollAroundInWaterGoal;
import mods.cybercat.gigeresque.common.entity.helper.GigMeleeAttackSelector;
import mods.cybercat.gigeresque.common.tags.GigTags;
import mods.cybercat.gigeresque.common.util.GigEntityUtils;

public class AquaticAlienEntity extends AlienEntity {

    public int killCounter;

    public AquaticAlienEntity(EntityType<? extends AlienEntity> type, Level world) {
        super(
            type,
            world,
            GigMeleeAttackSelector.STANDARD_ANIM_SELECTOR,
            Options.standardAlien(
                3,
                false,
                0.1f,
                false,
                GrowthOptions.adult(CommonMod.config.entityConfigs.aquaticXenoConfigs.aquaticAlienGrowthMultiplier)
            )
        );
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
            .add(
                Attributes.MAX_HEALTH,
                CommonMod.config.entityConfigs.aquaticXenoConfigs.aquaticXenoHealth
            )
            .add(Attributes.ARMOR, CommonMod.config.entityConfigs.aquaticXenoConfigs.aquaticXenoArmor)
            .add(
                Attributes.ARMOR_TOUGHNESS,
                9.0
            )
            .add(Attributes.KNOCKBACK_RESISTANCE, 9.0)
            .add(
                Attributes.FOLLOW_RANGE,
                32
            )
            .add(Attributes.MOVEMENT_SPEED, 0.3300000041723251)
            .add(
                Attributes.ATTACK_DAMAGE,
                CommonMod.config.entityConfigs.aquaticXenoConfigs.aquaticXenoAttackDamage
            )
            .add(Attributes.ATTACK_KNOCKBACK, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FleeExplodingCreeperGoal(this));
        this.goalSelector.addGoal(0, new DodgeProjectilesGoal(this));
        this.goalSelector.addGoal(1, new StrollAroundInWaterGoal(this, 0.6));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(1, new DelayedAttackGoal(this, 1.25F, 5));
        this.goalSelector.addGoal(1, new FleeFightGoal(this));
        this.goalSelector.addGoal(3, new LungeAtTargetGoal(this, 0.05F, 20 * 10, 16).setOnLungeCallback(this::runLungeAnimation));
        this.goalSelector.addGoal(4, new BreakBlocksGoal(this, GigTags.DESTRUCTIBLE_LIGHT, 1.5F));
        this.goalSelector.addGoal(5, new DigToTargetGoal(this, 32));
        this.goalSelector.addGoal(5, new FleeFireGoal(this));
        this.goalSelector.addGoal(7, new FindDarknessGoal(this)); // TODO: Find Darkness Goal
        this.goalSelector.addGoal(9, new RotateTowardsEntityGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new RotateTowardsEntityGoal(this, LivingEntity.class, 15.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, AlienEntity.class).setAlertOthers());
        this.targetSelector.addGoal(
            2,
            new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                false,
                target -> this.getHealth() > (this.getMaxHealth() / 2) && GigEntityUtils.isValidAquaTarget(target)
            )
        );
    }

    @Override
    public boolean killedEntity(@NotNull ServerLevel level, @NotNull LivingEntity entity) {
        if (entity.getType().is(EntityTypeTags.AQUATIC))
            this.killCounter++;
        return super.killedEntity(level, entity);
    }

    @Override
    public void tick() {
        super.tick();
        // if (!this.level().isClientSide())
        // AzureLib.LOGGER.info(this.killCounter);
        if (this.killCounter >= 3) {
            var aquaEgg = GigEntities.AQUA_EGG.get().create(level());
            if (aquaEgg != null) {
                aquaEgg.setPos(this.getX(), this.getY(), this.getZ());
                this.level().addFreshEntity(aquaEgg);
            }
            this.killCounter = 0;
        }
    }
}
