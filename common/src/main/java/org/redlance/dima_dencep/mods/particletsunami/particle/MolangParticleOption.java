package org.redlance.dima_dencep.mods.particletsunami.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.redlance.dima_dencep.mods.particletsunami.ParticleTsunamiMod;
import org.redlance.dima_dencep.mods.particletsunami.data.DefinedParticleEffect;

public class MolangParticleOption implements ParticleOptions {
    public static final MapCodec<MolangParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(MolangParticleOption::getId),
            DefinedParticleEffect.CODEC.fieldOf("preset").forGetter(MolangParticleOption::getEffect)
    ).apply(instance, MolangParticleOption::new));
    public static final StreamCodec<ByteBuf, MolangParticleOption> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, MolangParticleOption::getId,
            ByteBufCodecs.fromCodec(DefinedParticleEffect.CODEC), MolangParticleOption::getEffect,
            MolangParticleOption::new
    );
    private final ParticleType<MolangParticleOption> type;
    private final ResourceLocation id;
    private final ParticlePreset preset;

    private MolangParticleOption(ParticleType<MolangParticleOption> type, ResourceLocation id, ParticlePreset preset) {
        this.type = type;
        this.id = id;
        this.preset = preset;
    }

    public MolangParticleOption(ResourceLocation id, ParticlePreset preset) {
        this(ParticleTsunamiMod.MOLANG, id, preset);
    }

    protected MolangParticleOption(ResourceLocation id, DefinedParticleEffect effect) {
        this(id, new ParticlePreset(effect));
    }

    public ResourceLocation getId() {
        return id;
    }

    public ParticlePreset getPreset() {
        return preset;
    }

    protected DefinedParticleEffect getEffect() {
        return preset.effect;
    }

    @Override
    public @NotNull ParticleType<MolangParticleOption> getType() {
        return this.type;
    }
}
