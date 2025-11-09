package org.redlance.dima_dencep.mods.particletsunami.data.event;

import com.mojang.serialization.Codec;
import org.redlance.dima_dencep.mods.particletsunami.api.IEventNode;
import org.redlance.dima_dencep.mods.particletsunami.api.MolangInstance;
import org.redlance.dima_dencep.mods.particletsunami.ParticleTsunamiMod;

public record EventLog(String log) implements IEventNode {
    public static final Codec<EventLog> CODEC = Codec.STRING.xmap(EventLog::new, EventLog::log);

    @Override
    public void execute(MolangInstance instance) {
        ParticleTsunamiMod.LOGGER.info("{}[{}]: {}", instance.getIdentity(), instance.getPosition(), log);
    }
}
