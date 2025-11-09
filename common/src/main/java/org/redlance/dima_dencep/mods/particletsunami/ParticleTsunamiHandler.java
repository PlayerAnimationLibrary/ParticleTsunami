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
        VariableTable variableTable = IEntity.of(entity).particlestorm$getVariableTable();
        ResourceLocation particle = iData.particlestorm$getParticle();
        MolangExp expression = iData.particlestorm$getExpression(variableTable);

        ParticleEmitter current = ParticleTsunamiMod.LOADER.getEmitter(iData.particlestorm$getCachedId());
        if (current == null || current.isRemoved() || !particle.equals(current.particleId)) {
            ParticleEmitter emitter = new ParticleEmitter(entity.level(), entity.position(), particle, expression);
            ParticleTsunamiMod.LOADER.addEmitter(emitter);
            System.out.println(emitter);
            iData.particlestorm$setCachedId(emitter.id);
            emitter.attachEntity(entity);
            emitter.attachedBlock = null;
            /*double[] offset = getLocatorOffset(locator);
            double[] rotation = getLocatorRotation(locator);
            emitter.offsetPos = new Vec3(offset[0] * 0.0625, offset[1] * 0.0625, -offset[2] * 0.0625);
            emitter.offsetRot = new Vector3f((float) Math.toRadians(rotation[0]), (float) Math.toRadians(rotation[1]), (float) Math.toRadians(rotation[2]));
            emitter.parentPosition = cache.particlestorm$getPosition();
            emitter.parentRotation = cache.particlestorm$getRotation();*/
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
