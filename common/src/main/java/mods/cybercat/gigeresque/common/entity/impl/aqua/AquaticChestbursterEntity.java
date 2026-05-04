package mods.cybercat.gigeresque.common.entity.impl.aqua;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import mods.cybercat.gigeresque.common.entity.helper.GigMeleeAttackSelector;
import mods.cybercat.gigeresque.common.entity.impl.classic.ChestbursterEntity;

public class AquaticChestbursterEntity extends ChestbursterEntity {

    public AquaticChestbursterEntity(EntityType<? extends AquaticChestbursterEntity> type, Level world) {
        super(type, world);
        animationSelector = GigMeleeAttackSelector.RBUSTER_ANIM_SELECTOR;
        this.type = Type.AQUATIC_CHESTBURSTER;
    }

}
