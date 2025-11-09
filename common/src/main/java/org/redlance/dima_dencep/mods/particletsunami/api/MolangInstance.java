package org.redlance.dima_dencep.mods.particletsunami.api;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.VariableTable;
import org.redlance.dima_dencep.mods.particletsunami.particle.ParticleEmitter;

public interface MolangInstance {
    VariableTable getVars();

    Level getLevel();

    float tickAge();

    float tickLifetime();

    double getRandom1();

    double getRandom2();

    double getRandom3();

    double getRandom4();

    ResourceLocation getIdentity();

    Vec3 getPosition();

    @Nullable Entity getAttachedEntity();

    float getInvTickRate();

    ParticleEmitter getEmitter();
}
