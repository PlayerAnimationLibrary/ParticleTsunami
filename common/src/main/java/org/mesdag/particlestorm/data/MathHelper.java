package org.mesdag.particlestorm.data;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.mesdag.particlestorm.data.molang.VariableTable;
import org.mesdag.particlestorm.data.molang.compiler.MathValue;
import org.mesdag.particlestorm.data.molang.compiler.value.CompoundValue;
import org.mesdag.particlestorm.data.molang.compiler.value.Variable;
import org.mesdag.particlestorm.data.molang.compiler.value.VariableAssignment;

import java.util.List;
import java.util.Map;

public class MathHelper {
    public static Quaternionf setFromUnitVectors(Vector3f e, Vector3f t, Quaternionf dest) {
        float n = e.dot(t) + 1.0F;
        if (n < Mth.EPSILON) {
            if (Math.abs(e.x) > Math.abs(e.z)) {
                dest.x = -e.y;
                dest.y = e.x;
                dest.z = 0.0F;
            } else {
                dest.x = 0.0F;
                dest.y = -e.z;
                dest.z = e.y;
            }
            dest.w = 0.0F;
        } else {
            dest.x = e.y * t.z - e.z * t.y;
            dest.y = e.z * t.x - e.x * t.z;
            dest.z = e.x * t.y - e.y * t.x;
            dest.w = n;
        }
        return dest.normalize();
    }

    public static void applyQuaternion(Quaternionf e, Vector3f dest) {
        float t = dest.x, n = dest.y, r = dest.z;
        float i = e.x, a = e.y, o = e.z, s = e.w;
        float l = s * t + a * r - o * n;
        float c = s * n + o * t - i * r;
        float u = s * r + i * n - a * t;
        float h = -i * t - a * n - o * r;
        dest.x = l * s + h * -i + c * -o - u * -a;
        dest.y = c * s + h * -a + u * -i - l * -o;
        dest.z = u * s + h * -o + l * -a - c * -i;
    }

    public static Vector3f getRandomEuler(RandomSource random) {
        float x = random.nextFloat() * Mth.TWO_PI - Mth.PI;
        float y = random.nextFloat() * Mth.TWO_PI - Mth.PI;
        float z = random.nextFloat() * Mth.TWO_PI - Mth.PI;
        return new Vector3f(x, y, z);
    }

    public static void applyEuler(Vector3f euler, Vector3f dest) {
        applyEuler(euler.x, euler.y, euler.z, dest);
    }

    public static void applyEuler(float ex, float ey, float ez, Vector3f dest) {
        Quaternionf rotation = new Quaternionf();
        rotation.rotationYXZ(ey, ex, ez);
        rotation.transform(dest);
    }

    public static boolean forAssignment(Map<String, Variable> table, List<VariableAssignment> toInit, MathValue value) {
        if (value instanceof VariableAssignment assignment) {
            Variable variable = assignment.variable();
            table.put(variable.name(), variable);
            toInit.add(assignment);
            return true;
        }
        return false;
    }

    public static void forCompound(Map<String, Variable> table, List<VariableAssignment> toInit, MathValue variable) {
        if (variable instanceof CompoundValue(MathValue[] subValues)) {
            for (MathValue value : subValues) {
                forAssignment(table, toInit, value);
            }
        }
    }

    public static void redirect(List<VariableAssignment> toInit, VariableTable vars) {
        for (VariableAssignment assignment : toInit) {
            // 重定向，防止因找不到变量而爆栈
            vars.setValue(assignment.variable().name(), assignment.value());
        }
    }
}
