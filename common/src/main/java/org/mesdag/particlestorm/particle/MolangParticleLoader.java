package org.mesdag.particlestorm.particle;

import com.google.common.collect.EvictingQueue;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.api.IParticleComponent;
import org.mesdag.particlestorm.api.IntAllocator;
import org.mesdag.particlestorm.data.DefinedParticleEffect;
import org.redlance.dima_dencep.mods.particletsunami.ParticleTsunamiMod;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MolangParticleLoader implements PreparableReloadListener {
    public static final ResourceLocation RELOADER_ID = ResourceLocation.fromNamespaceAndPath(ParticleTsunamiMod.MODID, "reloader");
    private static final FileToIdConverter PARTICLE_LISTER = FileToIdConverter.json("particle_definitions");
    private Map<ResourceLocation, DefinedParticleEffect> id2Effect = new Hashtable<>();
    private Map<ResourceLocation, ParticlePreset> id2Particle = new Hashtable<>();
    private Map<ResourceLocation, EmitterPreset> id2Emitter = new Hashtable<>();
    public final Int2ObjectMap<ParticleEmitter> emitters = new Int2ObjectOpenHashMap<>();
    private final Object2ObjectMap<Entity, EvictingQueue<ParticleEmitter>> tracker = new Object2ObjectOpenHashMap<>();
    private final IntAllocator allocator = new IntAllocator();

    private boolean initialized = false;

    public Map<ResourceLocation, DefinedParticleEffect> id2Effect() {
        return id2Effect;
    }

    public Map<ResourceLocation, ParticlePreset> id2Particle() {
        return id2Particle;
    }

    public Map<ResourceLocation, EmitterPreset> id2Emitter() {
        return id2Emitter;
    }

    public void tick(LocalPlayer localPlayer) {
        if (initialized) {
            if (!emitters.isEmpty()) {
                int renderDistSqr = Mth.square(Minecraft.getInstance().options.renderDistance().get() * 16);
                ObjectIterator<Int2ObjectMap.Entry<ParticleEmitter>> iterator = emitters.int2ObjectEntrySet().iterator();
                while (iterator.hasNext()) {
                    ParticleEmitter emitter = iterator.next().getValue();
                    if (emitter.isRemoved() || emitter.level.dimension() != localPlayer.level().dimension()) {
                        emitter.onRemove();
                        allocator.release(emitter.id);
                        iterator.remove();
                    } else if (Mth.square(emitter.pos.x - localPlayer.getX()) + Mth.square(emitter.pos.z - localPlayer.getZ()) < renderDistSqr) {
                        emitter.tick();
                    }
                }
            }
            if (!tracker.isEmpty()) {
                ObjectIterator<Map.Entry<Entity, EvictingQueue<ParticleEmitter>>> iterator1 = tracker.entrySet().iterator();
                while (iterator1.hasNext()) {
                    Map.Entry<Entity, EvictingQueue<ParticleEmitter>> entry = iterator1.next();
                    if (entry.getKey().isRemoved()) {
                        iterator1.remove();
                    } else if (entry.getValue().removeIf(ParticleEmitter::isRemoved) && entry.getValue().isEmpty()) {
                        iterator1.remove();
                    }
                }
            }
        } else {
            for (ParticlePreset detail : id2Particle.values()) {
                for (IParticleComponent component : detail.effect.orderedParticleComponents) {
                    component.initialize(localPlayer.level());
                }
            }
            removeAll();
            this.initialized = true;
        }
    }

    public int totalEmitterCount() {
        return emitters.size();
    }

    /*public void loadEmitter(Level level, int id, CompoundTag tag) {
        ParticleEmitter emitter = new ParticleEmitter(level, tag);
        emitter.id = id;
        emitters.put(id, emitter);
        if (allocator.forceAllocate(id)) {
            ParticleTsunamiMod.LOGGER.warn("There was an emitter exist before, now replaced");
        }
    }*/

    public void addEmitter(ParticleEmitter emitter) {
        emitter.id = allocator.allocate();
        emitters.put(emitter.id, emitter);
    }

    public void addTrackedEmitter(Entity entity, ParticleEmitter emitter) {
        addEmitter(emitter);
        tracker.computeIfAbsent(entity, e -> EvictingQueue.create(16)).add(emitter);
    }

    public void removeEmitter(ParticleEmitter emitter) {
        removeEmitter(emitter.id);
    }

    public ParticleEmitter removeEmitter(int id) {
        ParticleEmitter removed = emitters.remove(id);
        if (removed != null) {
            removed.onRemove();
        }
        allocator.release(id, removed != null);
        return removed;
    }

    public void removeAll() {
        if (!emitters.isEmpty()) {
            ObjectIterator<Int2ObjectMap.Entry<ParticleEmitter>> iterator = emitters.int2ObjectEntrySet().iterator();
            while (iterator.hasNext()) {
                iterator.next().getValue().remove();
                iterator.remove();
            }
        }
        tracker.clear();
        allocator.clear();
    }

    public boolean contains(int id) {
        return allocator.isAllocated(id);
    }

    public @Nullable ParticleEmitter getEmitter(int id) {
        return emitters.get(id);
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(SharedState sharedState, Executor backgroundExecutor, PreparationBarrier preparationBarrier, Executor gameExecutor) {
        ResourceManager resourceManager = sharedState.resourceManager();
        CompletableFuture<List<DefinedParticleEffect>> prepare = CompletableFuture.supplyAsync(() -> PARTICLE_LISTER.listMatchingResources(resourceManager), backgroundExecutor).thenCompose(map -> {
            List<CompletableFuture<DefinedParticleEffect>> list = new ArrayList<>(map.size());
            for (Map.Entry<ResourceLocation, Resource> entry : map.entrySet()) {
                ResourceLocation id = PARTICLE_LISTER.fileToId(entry.getKey());
                list.add(CompletableFuture.supplyAsync(() -> {
                    try (Reader reader = entry.getValue().openAsReader()) {
                        return DefinedParticleEffect.CODEC.parse(JsonOps.INSTANCE, GsonHelper.parse(reader).get("particle_effect")).getOrThrow(JsonParseException::new);
                    } catch (IOException exception) {
                        throw new IllegalStateException("Failed to load definition for particle " + id, exception);
                    }
                }, backgroundExecutor));
            }
            return Util.sequence(list);
        });
        CompletableFuture<SpriteLoader.Preparations> particleFuture = sharedState.get(AtlasManager.PENDING_STITCH).get(AtlasIds.PARTICLES);
        return CompletableFuture.allOf(prepare, particleFuture).thenCompose(preparationBarrier::wait).thenAcceptAsync(effects -> {
            Map<ResourceLocation, DefinedParticleEffect> id2Effect = new Hashtable<>();
            Map<ResourceLocation, ParticlePreset> id2Particle = new Hashtable<>();
            Map<ResourceLocation, EmitterPreset> id2Emitter = new Hashtable<>();
            for (DefinedParticleEffect effect : prepare.join()) {
                ResourceLocation id = effect.description.identifier();
                id2Effect.put(id, effect);
                id2Particle.put(id, new ParticlePreset(effect));
                id2Emitter.put(id, new EmitterPreset(
                        new MolangParticleOption(effect.description.identifier()),
                        effect.orderedEmitterComponents,
                        effect.events
                ));
            }
            this.id2Effect = id2Effect;
            this.id2Particle = id2Particle;
            this.id2Emitter = id2Emitter;
            this.initialized = false;

            ParticleResources resources = Minecraft.getInstance().particleEngine.resourceManager;
            if (resources.spriteSets.get(ParticleTsunamiMod.MOLANG_PARTICLE) instanceof ExtendMutableSpriteSet spriteSet) {
                spriteSet.clear();
                int i = 0;
                SpriteLoader.Preparations preparations = particleFuture.join();
                for (Map.Entry<ResourceLocation, DefinedParticleEffect> entry : id2Effect.entrySet()) {
                    TextureAtlasSprite missing = preparations.missing();
                    spriteSet.bindMissing(missing);
                    ResourceLocation texture = entry.getValue().description.parameters().bindTexture(i);
                    TextureAtlasSprite sprite = preparations.regions().get(texture);
                    spriteSet.addSprite(sprite == null ? missing : sprite);
                    i++;
                }
            }
        }, gameExecutor);
    }
}
