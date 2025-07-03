package org.mesdag.particlestorm.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.registries.BuiltInRegistries;
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

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {
    @Shadow
    @Final
    private Map<ResourceLocation, ParticleEngine.MutableSpriteSet> spriteSets;
    @Shadow
    @Final
    private Int2ObjectMap<ParticleProvider<?>> providers;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void particleTsunami$init(ClientLevel level, TextureManager textureManager, CallbackInfo ci) {
        ExtendMutableSpriteSet extendMutableSpriteSet = new ExtendMutableSpriteSet();
        spriteSets.put(ParticleTsunamiMod.MOLANG_PARTICLE, extendMutableSpriteSet);
        providers.put(BuiltInRegistries.PARTICLE_TYPE.getIdOrThrow(ParticleTsunamiMod.MOLANG),
                new MolangParticleInstance.Provider(extendMutableSpriteSet)
        );
    }
}
