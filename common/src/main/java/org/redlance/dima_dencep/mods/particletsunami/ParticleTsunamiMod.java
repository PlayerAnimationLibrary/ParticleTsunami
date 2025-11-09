package org.redlance.dima_dencep.mods.particletsunami;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.zigythebird.playeranimcore.animation.keyframe.event.CustomKeyFrameEvents;
import com.zigythebird.playeranimcore.event.MolangEvent;
import com.zigythebird.playeranimcore.molang.MolangLoader;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.mesdag.particlestorm.api.IComponent;
import org.mesdag.particlestorm.api.IEventNode;
import org.mesdag.particlestorm.data.component.*;
import org.mesdag.particlestorm.data.event.*;
import org.mesdag.particlestorm.particle.MolangParticleLoader;
import org.mesdag.particlestorm.particle.MolangParticleOption;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ParticleTsunamiMod {
    public static final String MODID = "particlestorm";
    public static final Logger LOGGER = LoggerFactory.getLogger("ParticleStorm");

    public static final MolangParticleLoader LOADER = new MolangParticleLoader();

    public static final ResourceLocation MOLANG_PARTICLE = ResourceLocation.fromNamespaceAndPath(MODID, "molang");
    public static final ParticleType<MolangParticleOption> MOLANG = Registry.register(BuiltInRegistries.PARTICLE_TYPE, MOLANG_PARTICLE, new ParticleType<MolangParticleOption>(false) {
        @Override
        public @NotNull MapCodec<MolangParticleOption> codec() {
            return MolangParticleOption.codec(this);
        }

        @Override
        public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, MolangParticleOption> streamCodec() {
            return MolangParticleOption.streamCodec(this);
        }
    });

    public void onInitializeClient() {
        MolangEvent.MOLANG_EVENT.register((controller, engine, binding) -> {
            MolangLoader.setDoubleQuery(binding, "total_emitter_count", actor -> LOADER.totalEmitterCount());
            MolangLoader.setDoubleQuery(binding, "total_particle_count", actor -> {
                int sum = 0;
                for (Integer value : Minecraft.getInstance().particleEngine.trackedParticleCounts.values()) {
                    sum += value;
                }
                return sum;
            });
        });

        ParticleTsunamiHandler handler = new ParticleTsunamiHandler();
        CustomKeyFrameEvents.PARTICLE_KEYFRAME_EVENT.register(handler);
        CustomKeyFrameEvents.RESET_KEYFRAMES_EVENT.register(handler);

        registerComponents();
        registerEventNodes();
    }

    protected void tickPre(ClientLevel level) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        if (localPlayer == null) {
            LOADER.removeAll();
        } else if (!minecraft.isPaused() && !level.tickRateManager().isFrozen()) {
            LOADER.tick(localPlayer);
        }
    }

    protected void afterEntities(PoseStack poseStack, Camera camera) {
        if (!ParticleTsunamiPlatform.isDevEnv()) return;

        Minecraft minecraft = Minecraft.getInstance();
        float partialTicks = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        for (ParticleEmitter emitter : LOADER.emitters.values()) {
            double x = Mth.lerp(partialTicks, emitter.posO.x, emitter.pos.x);
            double y = Mth.lerp(partialTicks, emitter.posO.y, emitter.pos.y);
            double z = Mth.lerp(partialTicks, emitter.posO.z, emitter.pos.z);
            DebugRenderer.renderFloatingText(poseStack, bufferSource, emitter.getPreset().option.getId().toString(), x, y + 0.5, z, 0xFFFFFF);
            DebugRenderer.renderFloatingText(poseStack, bufferSource, "id: " + emitter.id, x, y + 0.3, z, 0xFFFFFF);
            int maxNum = minecraft.particleEngine.trackedParticleCounts.getInt(emitter.particleGroup);
            DebugRenderer.renderFloatingText(poseStack, bufferSource, "particles: " + maxNum, x, y + 0.1, z, maxNum == emitter.particleGroup.limit() ? 0xFF0000 : 0xFFFFFF);
            double d0 = camera.getPosition().x;
            double d1 = camera.getPosition().y;
            double d2 = camera.getPosition().z;
            poseStack.pushPose();
            poseStack.translate(x - d0, y - d1, z - d2);
            ShapeRenderer.renderLineBox(poseStack.last(), bufferSource.getBuffer(RenderType.lines()), -0.5, -0.5, -0.5, 0.5, 0.5, 0.5, 0, 1, 0, 1);
            poseStack.popPose();
        }
    }

    private static void registerComponents() {
        IComponent.register("emitter_local_space", EmitterLocalSpace.CODEC);
        IComponent.register("emitter_initialization", EmitterInitialization.CODEC);

        IComponent.register("emitter_rate_instant", EmitterRate.Instant.CODEC);
        IComponent.register("emitter_rate_steady", EmitterRate.Steady.CODEC);
        IComponent.register("emitter_rate_manual", EmitterRate.Manual.CODEC);

        IComponent.register("emitter_lifetime_looping", EmitterLifetime.Looping.CODEC);
        IComponent.register("emitter_lifetime_once", EmitterLifetime.Once.CODEC);
        IComponent.register("emitter_lifetime_expression", EmitterLifetime.Expression.CODEC);
        IComponent.register("emitter_lifetime_events", EmitterLifetimeEvents.CODEC);

        IComponent.register("emitter_shape_point", EmitterShape.Point.CODEC);
        IComponent.register("emitter_shape_sphere", EmitterShape.Sphere.CODEC);
        IComponent.register("emitter_shape_box", EmitterShape.Box.CODEC);
        IComponent.register("emitter_shape_entity_aabb", EmitterShape.EntityAABB.CODEC);
        IComponent.register("emitter_shape_disc", EmitterShape.Disc.CODEC);

        IComponent.register("particle_initial_speed", ParticleInitialSpeed.CODEC);
        IComponent.register("particle_initial_spin", ParticleInitialSpin.CODEC);
        IComponent.register("particle_initialization", ParticleInitialization.CODEC);

        IComponent.register(ParticleMotionDynamic.ID, ParticleMotionDynamic.CODEC);
        IComponent.register("particle_motion_parametric", ParticleMotionParametric.CODEC);
        IComponent.register(ParticleMotionCollision.ID, ParticleMotionCollision.CODEC);

        IComponent.register(ParticleAppearanceBillboard.ID, ParticleAppearanceBillboard.CODEC);
        IComponent.register("particle_appearance_tinting", ParticleAppearanceTinting.CODEC);
        IComponent.register("particle_appearance_lighting", ParticleAppearanceLighting.CODEC);

        IComponent.register("particle_lifetime_expression", ParticleLifetimeExpression.CODEC);
        IComponent.register(ParticleLifeTimeEvents.ID, ParticleLifeTimeEvents.CODEC);
        IComponent.register("particle_kill_plane", ParticleLifetimeKillPlane.CODEC);
        IComponent.register("particle_expire_if_in_blocks", ParticleExpireIfInBlocks.CODEC);
        IComponent.register("particle_expire_if_not_in_blocks", ParticleExpireIfNotInBlocks.CODEC);
    }

    private static void registerEventNodes() {
        IEventNode.register("sequence", EventSequence.CODEC);
        IEventNode.register("weight", EventRandomize.Weight.CODEC);
        IEventNode.register("randomize", EventRandomize.CODEC);
        IEventNode.register("particle_effect", ParticleEffect.CODEC.codec());
        IEventNode.register("sound_effect", SoundEffect.CODEC.codec());
        IEventNode.register("expression", NodeMolangExp.CODEC);
    }
}
