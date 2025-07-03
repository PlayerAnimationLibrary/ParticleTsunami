package org.redlance.dima_dencep.mods.particletsunami.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.mesdag.particlestorm.particle.MolangParticleLoader;
import org.redlance.dima_dencep.mods.particletsunami.ParticleTsunamiMod;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ParticleTsunamiModFabric extends ParticleTsunamiMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        super.onInitializeClient();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public @NotNull CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, Executor backgroundExecutor, Executor gameExecutor) {
                return LOADER.reload(barrier, manager, backgroundExecutor, gameExecutor);
            }

            @Override
            public ResourceLocation getFabricId() {
                return MolangParticleLoader.RELOADER_ID;
            }

            @Override
            public Collection<ResourceLocation> getFabricDependencies() {
                return Collections.singleton(ResourceReloadListenerKeys.TEXTURES);
            }
        });
        ClientTickEvents.START_WORLD_TICK.register(this::tickPre);
        WorldRenderEvents.AFTER_ENTITIES.register(context -> afterEntities(context.matrixStack(), context.camera()));
    }
}
