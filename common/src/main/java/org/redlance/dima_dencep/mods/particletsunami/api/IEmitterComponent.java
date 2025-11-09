package org.redlance.dima_dencep.mods.particletsunami.api;

import org.redlance.dima_dencep.mods.particletsunami.particle.ParticleEmitter;

public interface IEmitterComponent extends IComponent {
    default void update(ParticleEmitter entity) {}

    default void apply(ParticleEmitter entity) {}

    default boolean requireUpdate() {
        return false;
    }
}
