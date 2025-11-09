package org.redlance.dima_dencep.mods.particletsunami.particle;

import org.redlance.dima_dencep.mods.particletsunami.api.MolangInstance;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.VariableTable;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.compiler.value.Variable;

public class ParticleVariableTable extends VariableTable {
    private final VariableTable emitter;

    public ParticleVariableTable(VariableTable preset, VariableTable emitter) {
        super(preset);
        this.emitter = emitter;
    }

    @Override
    public double getValue(String name, MolangInstance instance) {
        Variable variable = table.get(name);
        if (variable == null) {
            variable = parent.table.get(name); // 预设表没有父级
            if (variable == null) {
                return emitter.getValue(name, instance);
            }
            return variable.get(instance);
        }
        return variable.get(instance);
    }
}
