package org.mesdag.particlestorm.mixin;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import org.mesdag.particlestorm.particle.ExtendMutableSpriteSet;
import org.mesdag.particlestorm.particle.MolangParticleInstance;
import org.redlance.dima_dencep.mods.particletsunami.ParticleTsunamiMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ParticleResources.class)
public abstract class ParticleResourcesMixin {
    @Shadow
    @Final
    private Map<ResourceLocation, ParticleResources.MutableSpriteSet> spriteSets;
    @Shadow
    protected abstract <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void particleTsunami$init(CallbackInfo ci) {
        ExtendMutableSpriteSet extendMutableSpriteSet = new ExtendMutableSpriteSet();
        this.spriteSets.put(ParticleTsunamiMod.MOLANG_PARTICLE, extendMutableSpriteSet);
        register(ParticleTsunamiMod.MOLANG, new MolangParticleInstance.Provider(extendMutableSpriteSet));
    }
}
