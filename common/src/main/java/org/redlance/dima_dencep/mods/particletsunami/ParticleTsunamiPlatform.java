package org.redlance.dima_dencep.mods.particletsunami;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.Contract;

public class ParticleTsunamiPlatform {
    @Contract
    @ExpectPlatform
    public static boolean isDevEnv() {
        throw new AssertionError();
    }

    @Contract
    @ExpectPlatform
    public static boolean hasSodium() {
        throw new AssertionError();
    }
}
