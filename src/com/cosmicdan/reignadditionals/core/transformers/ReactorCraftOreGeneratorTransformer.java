package com.cosmicdan.reignadditionals.core.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.cosmicdan.reignadditionals.core.CorePlugin;

public class ReactorCraftOreGeneratorTransformer extends AbstractInjectMethodStart {
    @Override
    public String getTargetClass() {
        return "Reika.ReactorCraft.World.ReactorOreGenerator";
    }
    
    @Override
    public String getTargetMethod() {
        return "generate";
    }

    @Override
    public String getTargetDesc() {
        return "(Ljava/util/Random;IILnet/minecraft/world/World;Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;)V";
    }

    @Override
    public String getReason() {
        return "Prevents ReactorCraft ores from generating in Overworld (Dimension 0)";
    }

    @Override
    public InsnList injectOps(InsnList toInject) {
        LabelNode allowGeneration = new LabelNode();
        toInject.add(new VarInsnNode(Opcodes.ALOAD, 4)); // World world
        toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/cosmicdan/reignadditionals/Main", "isOverworld", "(Lnet/minecraft/world/World;)Z", false));
        toInject.add(new JumpInsnNode(Opcodes.IFEQ, allowGeneration));
        toInject.add(new InsnNode(Opcodes.RETURN));
        toInject.add(allowGeneration);
        return toInject; 
    }
}