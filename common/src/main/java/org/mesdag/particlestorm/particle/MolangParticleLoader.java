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
    public final Map<ResourceLocation, DefinedParticleEffect> ID_2_EFFECT = new Hashtable<>();
    public final Map<ResourceLocation, ParticleDetail> ID_2_PARTICLE = new Hashtable<>();
    public final Map<ResourceLocation, EmitterDetail> ID_2_EMITTER = new Hashtable<>();
    public final Int2ObjectMap<ParticleEmitter> emitters = new Int2ObjectOpenHashMap<>();
    private final Object2ObjectMap<Entity, EvictingQueue<ParticleEmitter>> tracker = new Object2ObjectOpenHashMap<>();
    private final IntAllocator allocator = new IntAllocator();

    private boolean initialized = false;

    public void tick(LocalPlayer localPlayer) {
        if (initialized) {
            if (!emitters.isEmpty()) {
                int renderDistSqr = Mth.square(Minecraft.getInstance().options.renderDistance().get() * 16);
                ObjectIterator<Int2ObjectMap.Entry<ParticleEmitter>> iterator = emitters.int2ObjectEntrySet().iterator();
                while (iterator.hasNext()) {
                    ParticleEmitter emitter = iterator.next().getValue();
                    if (emitter.isRemoved()) {
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
                    var entry = iterator1.next();
                    if (entry.getKey().isRemoved() || entry.getValue().isEmpty()) {
                        iterator1.remove();
                    } else {
                        entry.getValue().removeIf(ParticleEmitter::isRemoved);
                    }
                }
            }
        } else {
            for (ParticleDetail detail : ID_2_PARTICLE.values()) {
                for (IParticleComponent component : detail.effect.orderedParticleComponents) {
                    component.initialize(localPlayer.level());
                }
            }
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
        emitters.clear();
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
            map.forEach((file, resource) -> {
                ResourceLocation id = PARTICLE_LISTER.fileToId(file);
                list.add(CompletableFuture.supplyAsync(() -> {
                    try (Reader reader = resource.openAsReader()) {
                        return DefinedParticleEffect.CODEC.parse(JsonOps.INSTANCE, GsonHelper.parse(reader).get("particle_effect")).getOrThrow(JsonParseException::new);
                    } catch (IOException exception) {
                        throw new IllegalStateException("Failed to load definition for particle " + id, exception);
                    }
                }, backgroundExecutor));
            });
            return Util.sequence(list);
        });
        CompletableFuture<SpriteLoader.Preparations> particleFuture = sharedState.get(AtlasManager.PENDING_STITCH).get(AtlasIds.PARTICLES);
        return CompletableFuture.allOf(prepare, particleFuture).thenCompose(preparationBarrier::wait).thenAcceptAsync(effects -> {
            ID_2_EFFECT.clear();
            ID_2_PARTICLE.clear();
            ID_2_EMITTER.clear();
            prepare.join().forEach(effect -> {
                ResourceLocation id = effect.description.identifier();
                ID_2_EFFECT.put(id, effect);
                ID_2_PARTICLE.put(id, new ParticleDetail(effect));
                System.out.println(id);
                ID_2_EMITTER.put(id, new EmitterDetail(
                        new MolangParticleOption(effect.description.identifier()),
                        effect.orderedEmitterComponents,
                        effect.events
                ));
            });
            System.out.println(ID_2_EMITTER);

            ParticleResources resources = Minecraft.getInstance().particleEngine.resourceManager;
            if (resources.spriteSets.get(ParticleTsunamiMod.MOLANG_PARTICLE) instanceof ExtendMutableSpriteSet spriteSet) {
                spriteSet.clear();
                int i = 0;
                SpriteLoader.Preparations preparations = particleFuture.join();
                for (Map.Entry<ResourceLocation, DefinedParticleEffect> entry : ParticleTsunamiMod.LOADER.ID_2_EFFECT.entrySet()) {
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
