package org.redlance.dima_dencep.mods.particletsunami;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.keyframe.event.CustomKeyFrameEvents;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.KeyFrameData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.ParticleKeyframeData;
import com.zigythebird.playeranimcore.event.EventResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.mesdag.particlestorm.data.event.ParticleEffect;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.data.molang.VariableTable;
import org.mesdag.particlestorm.mixed.IEntity;
import org.mesdag.particlestorm.mixed.IParticleKeyframeData;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.Set;

public class ParticleTsunamiHandler implements CustomKeyFrameEvents.CustomKeyFrameHandler<ParticleKeyframeData>, CustomKeyFrameEvents.ResetKeyFramesHandler {
    @Override
    public EventResult handle(float animationTick, AnimationController controller, ParticleKeyframeData keyframeData, AnimationData animationData) {
        IParticleKeyframeData iData = (IParticleKeyframeData) keyframeData;
        Avatar entity = ((PlayerAnimationController) controller).getAvatar();
        VariableTable variableTable = ((IEntity) entity).particlestorm$getVariableTable();
        ResourceLocation particle = iData.particlestorm$getParticle();
        MolangExp expression = iData.particlestorm$getExpression(variableTable);

        if (ParticleTsunamiMod.LOADER.contains(iData.particlestorm$getCachedId())) return EventResult.PASS;

        ParticleEmitter emitter = new ParticleEmitter(entity.level(), entity.position(), particle, ParticleEffect.Type.EMITTER, expression);
        emitter.subTable = variableTable;
        ParticleTsunamiMod.LOADER.addEmitter(emitter);
        iData.particlestorm$setCachedId(emitter.id);

        /* AdvancedPlayerAnimBone locator = controller.get3DTransform(keyframeData.getLocator());
        if (locator == null) {
            emitter.offsetPos = Vec3.ZERO;
            emitter.offsetRot = new Vector3f();
        } else*/ {
            emitter.attached = entity;
            emitter.offsetPos = Vec3.ZERO;
            emitter.offsetRot = new Vector3f();
            emitter.parentRotation = new Vector3f();
            emitter.parentMode = ParticleEmitter.ParentMode.LOCATOR;
        }

        return EventResult.SUCCESS;
    }

    @Override
    public void handle(AnimationController animationController, Set<KeyFrameData> executedKeyFrames) {
        for (Object executedKeyFrame : executedKeyFrames) {
            if (executedKeyFrame instanceof ParticleKeyframeData particleKeyframeData) {
                int id = ((IParticleKeyframeData) particleKeyframeData).particlestorm$getCachedId();
                if (id != -1) ParticleTsunamiMod.LOADER.removeEmitter(id);
            }
        }
    }
}
