package com.cosmicdan.reignadditionals.core.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.cosmicdan.reignadditionals.asmhelper.ASMHelper;

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
        return "updateTick";
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
        toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/cosmicdan/reignadditionals/events/LiquidMixingEvents", "hotAndColdLiquidContact", "()V", false));
        toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/cosmicdan/reignadditionals/events/LiquidMixingEvents", "smoothstoneMix", "()Z", false));
        toInject.add(new JumpInsnNode(Opcodes.IFEQ, targetNode.label));
        return toInject; 
    }
}
