package com.cosmicdan.reignadditionals.core.transformers;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.cosmicdan.reignadditionals.Main;
import net.minecraft.launchwrapper.IClassTransformer;

/*
 * Helper class to add an additional check to an existing jump.
 * author: CosmicDan
 */
public abstract class AbstractInjectJumpTransformer implements IClassTransformer  {
    String targetClass;
    String targetMethod;
    String targetDesc;
    String hookReason;
    int targetOp;
    JumpInsnNode targetNode;

    public AbstractInjectJumpTransformer(int targetOp) {
        targetClass = getTargetClass();
        targetMethod = getTargetMethod();
        targetDesc = getTargetDesc();
        hookReason = getReason();
        this.targetOp = targetOp;
    }

    public abstract String getTargetClass();
    public abstract String getTargetMethod();
    public abstract String getTargetDesc();
    public abstract String getReason();
    /**
     * Do your search logic for the desired JumpInsnNode you want to inject here.
     * @param m is the matched MethodNode you can search through 
     * @return the found JumpInsnNode you want to inject on.
     */
    public abstract JumpInsnNode getTargetNode(MethodNode m);
    /**
     * Perform the opcode injections here.
     * @param toInject is the list of opcodes to inject.
     * @return the modified toInject.
     */
    public abstract InsnList injectOps(InsnList toInject);
    
    
    
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] classBytes) {
        if (transformedName.equals(targetClass))
            return patchClass(classBytes, true);
        return classBytes;
    }
    
    private byte[] patchClass(byte[] classBytes, boolean dev) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(classBytes);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        while(methods.hasNext()) {
            MethodNode m = methods.next();
            int fdiv_index = -1;
            if ((m.name.equals(targetMethod) && m.desc.equals(targetDesc))) {
                targetNode = getTargetNode(m);
                InsnList toInject = injectOps(new InsnList());
                m.instructions.insert(targetNode, toInject);
                Main.LOGGER.info("[i] Patched " + targetClass + "." + targetMethod);
                Main.LOGGER.info("    Reason: " + hookReason);
                break;
            }
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
