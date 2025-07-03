package org.redlance.dima_dencep.mods.particletsunami.fabric;

import net.fabricmc.loader.api.FabricLoader;

@SuppressWarnings("unused")
public class ParticleTsunamiPlatformImpl {
    public static boolean isDevEnv() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static boolean hasSodium() {
        return FabricLoader.getInstance().isModLoaded("sodium");
    }
}
