package com.cosmicdan.reignadditionals.core.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class TransformBlockLiquid2 extends AbstractInjectMethodStart {

    @Override
    public String getTargetClass() {
        return "net.minecraft.block.BlockLiquid";
    }

    @Override
    public String getTargetMethod() {
        return "func_149799_m";
    }

    @Override
    public String getTargetDesc() {
        return "(Lnet/minecraft/world/World;III)V";
    }

    @Override
    public String getReason() {
        return "Steam particles spawning with every 'fizz' from lava contact/spread";
    }

    @Override
    public InsnList injectOps(InsnList toInject) {
        toInject = new InsnList();
        toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
        toInject.add(new VarInsnNode(Opcodes.ILOAD, 2));
        toInject.add(new VarInsnNode(Opcodes.ILOAD, 3));
        toInject.add(new VarInsnNode(Opcodes.ILOAD, 4));
        toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/cosmicdan/reignadditionals/events/LiquidMixingEvents", "hotAndColdLiquidContact", "(Lnet/minecraft/world/World;III)V", false));
        return toInject; 
    }
}
