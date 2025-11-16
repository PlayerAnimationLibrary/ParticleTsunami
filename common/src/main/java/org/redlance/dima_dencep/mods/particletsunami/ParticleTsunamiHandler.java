package org.redlance.dima_dencep.mods.particletsunami;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.keyframe.event.CustomKeyFrameEvents;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.KeyFrameData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.ParticleKeyframeData;
import com.zigythebird.playeranimcore.event.EventResult;
import com.zigythebird.playeranimcore.math.Vec3f;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.phys.Vec3;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.MolangExp;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.VariableTable;
import org.redlance.dima_dencep.mods.particletsunami.mixed.IEntity;
import org.redlance.dima_dencep.mods.particletsunami.mixed.IParticleKeyframeData;
import org.redlance.dima_dencep.mods.particletsunami.particle.ParticleEmitter;

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
        if (current == null || current.isRemoved() || !particle.equals(current.getIdentity())) {
            ParticleEmitter emitter = new ParticleEmitter(entity.level(), entity.position(), particle, expression);
            ParticleTsunamiMod.LOADER.addEmitter(emitter);
            System.out.println(emitter);
            iData.particlestorm$setCachedId(emitter.id);
            emitter.attachEntity(entity);
            emitter.attachedBlock = null;
            Vec3f position = controller.getBonePosition(keyframeData.getLocator());
            emitter.offsetPos = new Vec3(position.x() * 0.0625, position.y() * 0.0625, position.z() * 0.0625);
            //emitter.offsetRot =
            emitter.parentPosition = null;
            emitter.parentRotation = null;
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
