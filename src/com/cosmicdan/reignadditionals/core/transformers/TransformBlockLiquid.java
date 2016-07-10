package com.cosmicdan.reignadditionals.core.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import com.cosmicdan.reignadditionals.asmhelper.ASMHelper;

public class TransformBlockLiquid extends AbstractInjectJumpTransformer {

	public TransformBlockLiquid() {
		// hook at that last "else if (l <= 4)" in BlockLiquid
		super(Opcodes.IF_ICMPGT);
	}

	@Override
	public String getTargetClass() {
		return "net.minecraft.block.BlockLiquid";
	}

	@Override
	public String getTargetMethod() {
		return "func_149805_n";
	}

	@Override
	public String getTargetDesc() {
		return "(Lnet/minecraft/world/World;III)V";
	}

	@Override
	public String getReason() {
		return "Option to prevent cobble generating from lava + water mixing";
	}
	
	@Override
	public JumpInsnNode getTargetNode(MethodNode m) {
		return (JumpInsnNode) ASMHelper.findLastInstructionWithOpcode(m, targetOp);
	}

	@Override
	public InsnList injectOps(InsnList toInject) {
		// call CobbleMixEvent#cobbleMix, if it returns true then goto to label that the original jump requested
		toInject = new InsnList();
        //toInject.add(new VarInsnNode(Opcodes.ALOAD, 1)); // push first parameter (World p_149805_1_) onto stack
        //toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/cosmicdan/reignadditionals/events/BlockLiquidEvent", "cobbleMix", "(Lnet/minecraft/world/World;)Z", false));
		toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/cosmicdan/reignadditionals/events/BlockLiquidEvent", "cobbleMix", "()Z", false));
        toInject.add(new JumpInsnNode(Opcodes.IFEQ, targetNode.label));
        return toInject; 
	}
}
