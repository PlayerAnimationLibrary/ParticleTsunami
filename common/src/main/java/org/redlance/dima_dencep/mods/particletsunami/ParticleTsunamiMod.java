package org.redlance.dima_dencep.mods.particletsunami;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.zigythebird.playeranimcore.animation.keyframe.event.CustomKeyFrameEvents;
import com.zigythebird.playeranimcore.event.MolangEvent;
import com.zigythebird.playeranimcore.molang.MolangLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.redlance.dima_dencep.mods.particletsunami.api.IComponent;
import org.redlance.dima_dencep.mods.particletsunami.api.IEventNode;
import org.redlance.dima_dencep.mods.particletsunami.data.component.*;
import org.redlance.dima_dencep.mods.particletsunami.data.event.*;
import org.redlance.dima_dencep.mods.particletsunami.particle.MolangParticleLoader;
import org.redlance.dima_dencep.mods.particletsunami.particle.MolangParticleOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public abstract class ParticleTsunamiMod {
    public static final String MODID = "particle_tsunami";
    public static final Logger LOGGER = LoggerFactory.getLogger("ParticleTsunami");

    public static final Codec<List<String>> STRING_LIST_CODEC = Codec.either(Codec.STRING, Codec.list(Codec.STRING)).xmap(
            either -> either.map(Collections::singletonList, Function.identity()),
            l -> l.size() == 1 ? Either.left(l.getFirst()) : Either.right(l)
    );

    public static final MolangParticleLoader LOADER = new MolangParticleLoader();

    public static final ResourceLocation MOLANG_PARTICLE = ResourceLocation.fromNamespaceAndPath(MODID, "molang");
    public static final ParticleType<MolangParticleOption> MOLANG = new ParticleType<>(false) {
        @Override
        public @NotNull MapCodec<MolangParticleOption> codec() {
            return MolangParticleOption.codec(this);
        }

        @Override
        public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, MolangParticleOption> streamCodec() {
            return MolangParticleOption.streamCodec(this);
        }
    };

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
