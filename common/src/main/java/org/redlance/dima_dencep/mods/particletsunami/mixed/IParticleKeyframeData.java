package org.redlance.dima_dencep.mods.particletsunami.mixed;

import net.minecraft.resources.ResourceLocation;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.MolangExp;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.VariableTable;

public interface IParticleKeyframeData {
    ResourceLocation particlestorm$getParticle();

    MolangExp particlestorm$getExpression(VariableTable variableTable);

    void particlestorm$setCachedId(int id);
    int particlestorm$getCachedId();
}
