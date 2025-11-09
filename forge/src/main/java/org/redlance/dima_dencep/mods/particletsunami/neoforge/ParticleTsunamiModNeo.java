package org.redlance.dima_dencep.mods.particletsunami.neoforge;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.mesdag.particlestorm.particle.MolangParticleLoader;
import org.redlance.dima_dencep.mods.particletsunami.ParticleTsunamiMod;

@Mod(value = ParticleTsunamiMod.MODID, dist = Dist.CLIENT)
public class ParticleTsunamiModNeo extends ParticleTsunamiMod {
    public ParticleTsunamiModNeo(IEventBus bus) {
        super.onInitializeClient();

        NeoForge.EVENT_BUS.addListener(this::onClientTickPre);
        bus.addListener(this::onAddClientReloadListeners);
        bus.addListener(this::onRegister);
    }

    private void onClientTickPre(ClientTickEvent.Pre event) {
        super.tickPre(Minecraft.getInstance().level);
    }

    private void onAddClientReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(MolangParticleLoader.RELOADER_ID, ParticleTsunamiMod.LOADER);
        event.addDependency(event.getNameLookup().apply(Minecraft.getInstance().particleEngine.resourceManager), MolangParticleLoader.RELOADER_ID);
    }

    public void onRegister(RegisterEvent event) {
        if (event.getRegistry() != BuiltInRegistries.PARTICLE_TYPE) return;
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, MOLANG_PARTICLE, MOLANG);
    }
}
