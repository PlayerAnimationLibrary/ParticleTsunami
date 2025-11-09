package org.redlance.dima_dencep.mods.particletsunami.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.redlance.dima_dencep.mods.particletsunami.particle.MolangParticleOption;
import org.redlance.dima_dencep.mods.particletsunami.particle.ParticleEmitter;
import org.redlance.dima_dencep.mods.particletsunami.ParticleTsunamiMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @WrapWithCondition(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private boolean modify(Level instance, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        if (particleData instanceof MolangParticleOption molang) {
            LivingEntity self = (LivingEntity) (Object) this;
            ParticleEmitter emitter = new ParticleEmitter(instance, self.position(), molang.getId());
            emitter.attachEntity(self);
            ParticleTsunamiMod.LOADER.addTrackedEmitter(self, emitter);
            return false;
        }
        return true;
    }
}
