package mods.cybercat.gigeresque.common.entity.impl.hellmorphs;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import mods.cybercat.gigeresque.CommonMod;
import mods.cybercat.gigeresque.common.entity.helper.GigMeleeAttackSelector;
import mods.cybercat.gigeresque.common.entity.impl.runner.RunnerbursterEntity;

public class HellbursterEntity extends RunnerbursterEntity {

    public HellbursterEntity(EntityType<? extends HellbursterEntity> type, Level level) {
        super(type, level);
        animationSelector = GigMeleeAttackSelector.RBUSTER_ANIM_SELECTOR;
        this.type = Type.HELLBURSTER;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
            .add(
                Attributes.MAX_HEALTH,
                CommonMod.config.entityConfigs.hellbusterConfigs.hellbusterHealth
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
                CommonMod.config.entityConfigs.hellbusterConfigs.hellbusterAttackDamage
            )
            .add(Attributes.ATTACK_KNOCKBACK, 0.3);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }
}
