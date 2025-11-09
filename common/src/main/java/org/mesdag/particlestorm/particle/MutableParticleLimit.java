package org.mesdag.particlestorm.particle;

import net.minecraft.core.particles.ParticleLimit;

public class MutableParticleLimit extends ParticleLimit {
    public MutableParticleLimit(int limit) {
        super(limit);
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
