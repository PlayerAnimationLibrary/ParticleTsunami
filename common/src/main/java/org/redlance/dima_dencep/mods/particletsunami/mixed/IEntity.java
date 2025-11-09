package org.redlance.dima_dencep.mods.particletsunami.mixed;

import net.minecraft.world.entity.Entity;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.VariableTable;

public interface IEntity {
    VariableTable particlestorm$getVariableTable();

    static IEntity of(Entity entity) {
        return (IEntity) entity;
    }
}
