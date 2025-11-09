package org.redlance.dima_dencep.mods.particletsunami.mixin.integration.geckolib;

import com.zigythebird.playeranimcore.animation.keyframe.event.data.ParticleKeyframeData;
import net.minecraft.resources.ResourceLocation;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.MolangExp;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.VariableTable;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.compiler.MolangParser;
import org.redlance.dima_dencep.mods.particletsunami.mixed.IParticleKeyframeData;
import org.spongepowered.asm.mixin.*;

@Mixin(value = ParticleKeyframeData.class, remap = false)
public abstract class ParticleKeyframeDataMixin implements IParticleKeyframeData {
    @Shadow
    @Final
    private String effect;
    @Shadow
    @Final
    private String script;

    @Unique
    private ResourceLocation particlestorm$particle;
    @Unique
    private MolangExp particlestorm$expression;
    @Unique
    private int particlestorm$cachedId = -1;

    @Override
    public ResourceLocation particlestorm$getParticle() {
        if (particlestorm$particle == null) {
            this.particlestorm$particle = ResourceLocation.parse(effect);
        }
        return particlestorm$particle;
    }

    @Override
    public MolangExp particlestorm$getExpression(VariableTable variableTable) {
        if (particlestorm$expression == null) {
            this.particlestorm$expression = new MolangExp(script);
            particlestorm$expression.compile(new MolangParser(variableTable));
        }
        return particlestorm$expression;
    }

    @Override
    public void particlestorm$setCachedId(int id) {
        this.particlestorm$cachedId = id;
    }

    @Override
    public int particlestorm$getCachedId() {
        return this.particlestorm$cachedId;
    }
}
