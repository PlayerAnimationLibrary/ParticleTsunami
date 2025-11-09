package org.redlance.dima_dencep.mods.particletsunami.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.PackType;
import org.mesdag.particlestorm.particle.MolangParticleLoader;
import org.redlance.dima_dencep.mods.particletsunami.ParticleTsunamiMod;

public class ParticleTsunamiModFabric extends ParticleTsunamiMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, MOLANG_PARTICLE, MOLANG);

        super.onInitializeClient();
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(MolangParticleLoader.RELOADER_ID, ParticleTsunamiMod.LOADER);
        ResourceLoader.get(PackType.CLIENT_RESOURCES).addReloaderOrdering(ResourceReloaderKeys.Client.PARTICLES, MolangParticleLoader.RELOADER_ID);

        ClientTickEvents.START_WORLD_TICK.register(this::tickPre);
    }
}
