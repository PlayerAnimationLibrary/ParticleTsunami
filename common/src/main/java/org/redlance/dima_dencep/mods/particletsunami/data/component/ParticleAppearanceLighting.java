package org.redlance.dima_dencep.mods.particletsunami.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import org.redlance.dima_dencep.mods.particletsunami.api.IParticleComponent;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.MolangExp;

import java.util.List;

/**
 * When this component exists, particle will be tinted by local lighting conditions in-game.
 */
public final class ParticleAppearanceLighting implements IParticleComponent {
    public static final ParticleAppearanceLighting INSTANCE = new ParticleAppearanceLighting();
    public static final Codec<ParticleAppearanceLighting> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(ParticleAppearanceLighting.INSTANCE)).codec();

    @Override
    public Codec<ParticleAppearanceLighting> codec() {
        return CODEC;
    }

    @Override
    public List<MolangExp> getAllMolangExp() {
        return List.of();
    }
}
