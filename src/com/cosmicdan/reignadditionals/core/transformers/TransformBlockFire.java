package com.cosmicdan.reignadditionals.core.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.cosmicdan.reignadditionals.core.CorePlugin;

public class TransformBlockFire extends AbstractInjectMethodStart {
    @Override
    public String getTargetClass() {
        return "net.minecraft.block.BlockFire";
    }
    
    @Override
    public String getTargetMethod() {
        return ( CorePlugin.isDevEnv() ? "updateTick" : "func_149674_a"); 
    }

    @Override
    public String getTargetDesc() {
        return "(Lnet/minecraft/world/World;IIILjava/util/Random;)V";
    }

    @Override
    public String getReason() {
        return "For flammability config, prevents fire ticking when on a non-flammable block (and sets to air)";
    }

    @Override
    public InsnList injectOps(InsnList toInject) {
        LabelNode allowTick = new LabelNode();
        toInject.add(new VarInsnNode(Opcodes.ALOAD, 1)); // World world
        toInject.add(new VarInsnNode(Opcodes.ILOAD, 2)); // int posX
        toInject.add(new VarInsnNode(Opcodes.ILOAD, 3)); // int posY
        toInject.add(new VarInsnNode(Opcodes.ILOAD, 4)); // int posZ
        toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/cosmicdan/reignadditionals/events/BlockEvents", "shouldStopFire", "(Lnet/minecraft/world/World;III)Z", false));
        toInject.add(new JumpInsnNode(Opcodes.IFEQ, allowTick));
        toInject.add(new InsnNode(Opcodes.RETURN));
        toInject.add(allowTick);
        return toInject; 
    }
}
