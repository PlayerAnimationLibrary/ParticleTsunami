package org.redlance.dima_dencep.mods.particletsunami.data.event;

import com.mojang.serialization.Codec;
import org.redlance.dima_dencep.mods.particletsunami.api.IEventNode;
import org.redlance.dima_dencep.mods.particletsunami.api.MolangInstance;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.MolangExp;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.compiler.MolangParser;

public final class NodeMolangExp extends MolangExp implements IEventNode {
    public static final Codec<NodeMolangExp> CODEC = Codec.STRING.xmap(NodeMolangExp::new, NodeMolangExp::getExpStr);

    public NodeMolangExp(String expStr) {
        super(expStr);
    }

    @Override
    public void execute(MolangInstance instance) {
        if (variable == null && !expStr.isEmpty() && !expStr.isBlank()) {
            MolangParser parser = new MolangParser(instance.getVars());
            this.variable = parser.compileMolang(expStr);
        }
        if (variable != null) {
            variable.get(instance);
        }
    }
}
