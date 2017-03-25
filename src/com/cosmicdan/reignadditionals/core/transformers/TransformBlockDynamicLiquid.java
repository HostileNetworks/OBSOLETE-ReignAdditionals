package com.cosmicdan.reignadditionals.core.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.cosmicdan.reignadditionals.asmhelper.ASMHelper;
import com.cosmicdan.reignadditionals.core.CorePlugin;

public class TransformBlockDynamicLiquid extends AbstractInjectJumpTransformer {

    public TransformBlockDynamicLiquid() {
        // hook at the material lava/water condition check surrounding the code that calls setBlock to Blocks.stone 
        super(Opcodes.IF_ACMPNE);
    }

    @Override
    public String getTargetClass() {
        return "net.minecraft.block.BlockDynamicLiquid";
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
        return "Option to prevent smooth stone generating when lava spread makes contact with water";
    }
    
    @Override
    public JumpInsnNode getTargetNode(MethodNode m) {
        return (JumpInsnNode) ASMHelper.findLastInstructionWithOpcode(m, targetOp);
    }

    @Override
    public InsnList injectOps(InsnList toInject) {
        toInject = new InsnList();
        toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
        toInject.add(new VarInsnNode(Opcodes.ILOAD, 2));
        toInject.add(new VarInsnNode(Opcodes.ILOAD, 3));
        toInject.add(new VarInsnNode(Opcodes.ILOAD, 4));
        toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/cosmicdan/reignadditionals/events/LiquidMixingEvents", "hotAndColdLiquidContact", "(Lnet/minecraft/world/World;III)V", false));
        toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/cosmicdan/reignadditionals/events/LiquidMixingEvents", "smoothstoneMix", "()Z", false));
        toInject.add(new JumpInsnNode(Opcodes.IFEQ, targetNode.label));
        return toInject; 
    }
}
