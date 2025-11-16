package org.redlance.dima_dencep.mods.particletsunami.data.molang;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.redlance.dima_dencep.mods.particletsunami.api.MolangInstance;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.compiler.MathValue;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.compiler.MolangParser;
import org.redlance.dima_dencep.mods.particletsunami.data.molang.compiler.value.Constant;

import java.util.Map;

public class MolangExp {
    public static final MolangExp EMPTY = Util.make(new MolangExp(""), exp -> exp.variable = new Constant(0.0));
    public static final Codec<MolangExp> CODEC = Codec.STRING.xmap(MolangExp::new, MolangExp::getExpStr);
    public static final StreamCodec<ByteBuf, MolangExp> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(MolangExp::new, MolangExp::getExpStr);
    protected final String expStr;
    protected MathValue variable;

    public MolangExp(String expStr) {
        this.expStr = expStr;
    }

    public MolangExp(String key, double value) {
        if (!key.startsWith("variable.")) key = "variable." + key;
        this.expStr = key + "=" + value + ";";
    }

    public MolangExp(Map<String, String> exps) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : exps.entrySet()) {
            builder.append(entry.getKey()).append('=').append(entry.getValue()).append(';');
        }
        this.expStr = builder.toString();
    }

    public String getExpStr() {
        return expStr;
    }

    public void compile(MolangParser parser) {
        if (variable == null && !expStr.isEmpty() && !expStr.isBlank()) {
            this.variable = parser.compileMolang(expStr);
        }
    }

    public float calculate(MolangInstance instance) {
        if (!initialized()) return 0.0F;
        return (float) variable.get(instance);
    }

    public MathValue getVariable() {
        return variable;
    }

    public boolean initialized() {
        return variable != null;
    }

    @Override
    public String toString() {
        return "MolangExp{" + expStr + "}";
    }
}
