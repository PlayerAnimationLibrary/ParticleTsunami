package org.redlance.dima_dencep.mods.particletsunami.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.redlance.dima_dencep.mods.particletsunami.particle.MolangParticleOption;

public class MolangParticleMobEffect extends MobEffect {
    public MolangParticleMobEffect(MobEffectCategory category, int color, ResourceLocation particleId) {
        super(category, color, new MolangParticleOption(particleId));
    }
}
