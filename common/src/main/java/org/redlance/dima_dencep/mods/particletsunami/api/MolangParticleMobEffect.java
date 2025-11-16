package org.redlance.dima_dencep.mods.particletsunami.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.redlance.dima_dencep.mods.particletsunami.particle.MolangParticleOption;
import org.redlance.dima_dencep.mods.particletsunami.particle.ParticlePreset;

public class MolangParticleMobEffect extends MobEffect {
    public MolangParticleMobEffect(MobEffectCategory category, int color, ResourceLocation particleId, ParticlePreset preset) {
        super(category, color, new MolangParticleOption(particleId, preset));
    }
}
