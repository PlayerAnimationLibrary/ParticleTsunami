package org.redlance.dima_dencep.mods.particletsunami.data.event;

import com.mojang.serialization.Codec;
import org.redlance.dima_dencep.mods.particletsunami.api.IEventNode;
import org.redlance.dima_dencep.mods.particletsunami.api.MolangInstance;

import java.util.List;
import java.util.Map;

public record EventSequence(List<Map<String, IEventNode>> nodes) implements IEventNode {
    public static final Codec<EventSequence> CODEC = IEventNode.CODEC.listOf().xmap(EventSequence::new, EventSequence::nodes);

    @Override
    public void execute(MolangInstance instance) {
        for (Map<String, IEventNode> nodeMap : nodes) {
            for (IEventNode node : nodeMap.values()) {
                node.execute(instance);
            }
        }
    }
}
