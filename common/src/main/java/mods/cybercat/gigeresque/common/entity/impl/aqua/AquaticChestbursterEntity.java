package mods.cybercat.gigeresque.common.entity.impl.aqua;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import mods.cybercat.gigeresque.common.entity.GigEntities;
import mods.cybercat.gigeresque.common.entity.impl.classic.ChestbursterEntity;

public class AquaticChestbursterEntity extends ChestbursterEntity {

    public AquaticChestbursterEntity(EntityType<? extends AquaticChestbursterEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public LivingEntity growInto() {
        var entity = GigEntities.AQUATIC_ALIEN.get().create(level());

        if (hasCustomName() && entity != null)
            entity.setCustomName(this.getCustomName());

        return entity;
    }

}
