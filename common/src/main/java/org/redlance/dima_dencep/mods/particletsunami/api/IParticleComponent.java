package org.redlance.dima_dencep.mods.particletsunami.api;

import net.minecraft.world.level.Level;
import org.redlance.dima_dencep.mods.particletsunami.particle.MolangParticleInstance;

public interface IParticleComponent extends IComponent {
    default void update(MolangParticleInstance instance) {}

    default void apply(MolangParticleInstance instance) {}

    default boolean requireUpdate() {
        return false;
    }

    default void initialize(Level level) {}
}
