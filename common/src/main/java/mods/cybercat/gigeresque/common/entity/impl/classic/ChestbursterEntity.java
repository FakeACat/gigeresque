package mods.cybercat.gigeresque.common.entity.impl.classic;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import mods.cybercat.gigeresque.CommonMod;
import mods.cybercat.gigeresque.common.entity.AlienEntity;
import mods.cybercat.gigeresque.common.entity.ai.goals.attack.*;
import mods.cybercat.gigeresque.common.entity.ai.goals.movement.*;
import mods.cybercat.gigeresque.common.entity.helper.GigCommonMethods;
import mods.cybercat.gigeresque.common.entity.helper.GigMeleeAttackSelector;
import mods.cybercat.gigeresque.common.sound.GigSounds;
import mods.cybercat.gigeresque.common.tags.GigTags;

public class ChestbursterEntity extends AlienEntity {

    public ChestbursterEntity(
        EntityType<? extends ChestbursterEntity> type,
        Level world
    ) {
        super(type, world, GigMeleeAttackSelector.RBUSTER_ANIM_SELECTOR, Type.CHESTBURSTER);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
            .add(
                Attributes.MAX_HEALTH,
                CommonMod.config.entityConfigs.bursterConfigs.chestbursterHealth
            )
            .add(Attributes.ARMOR, 0.0f)
            .add(
                Attributes.ARMOR_TOUGHNESS,
                0.0f
            )
            .add(Attributes.KNOCKBACK_RESISTANCE, 8.0)
            .add(Attributes.FOLLOW_RANGE, 32.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3300000041723251)
            .add(
                Attributes.ATTACK_DAMAGE,
                0.0f
            )
            .add(Attributes.ATTACK_KNOCKBACK, 1.0);
    }

    @Override
    public @NotNull SoundEvent getHurtSound(@NotNull DamageSource source) {
        return GigSounds.HUGGER_HURT.get();
    }

    @Override
    public @NotNull SoundEvent getDeathSound() {
        return GigSounds.HUGGER_DEATH.get();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isDeadOrDying()) {
            GigCommonMethods.setAnimation(animationDispatcher::sendDeath);
        }
        if (this.getVehicle() instanceof LivingEntity livingEntity && livingEntity.isAlive()) {
            GigCommonMethods.setAnimation(animationDispatcher::sendImpregate);
        }

        if (!this.level().isClientSide) {
            var radius = this.getBoundingBox().inflate(1.25D);
            this.level()
                .getEntitiesOfClass(ItemEntity.class, radius)
                .stream()
                .filter(itemEntity -> itemEntity.getItem().is(GigTags.BURSTER_FOODS))
                .findFirst()
                .ifPresent(this::checkAndPerformEating);
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FleeExplodingCreeperGoal(this));
        this.goalSelector.addGoal(1, new StrollAroundInWaterGoal(this, 0.6));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(1, new FleeFightGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this));
        this.goalSelector.addGoal(3, new EatFoodItemGoal(this, 0.9F, 5));
        this.goalSelector.addGoal(4, new BreakBlocksGoal(this, GigTags.BURSTER_BLOCKS, 1.5F));
        this.goalSelector.addGoal(5, new FleeFireGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, LivingEntity.class, 15.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, AlienEntity.class).setAlertOthers());
    }

}
