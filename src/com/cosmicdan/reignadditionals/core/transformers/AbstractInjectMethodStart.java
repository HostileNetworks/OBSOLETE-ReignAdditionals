package com.cosmicdan.reignadditionals.core.transformers;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import com.cosmicdan.reignadditionals.Main;

import net.minecraft.launchwrapper.IClassTransformer;

/*
 * Helper class to hook the start of a given method
 * author: CosmicDan
 */
public abstract class AbstractInjectMethodStart implements IClassTransformer  {
    String targetClass;
    String targetMethod;
    String targetDesc;
    String hookReason;

    public AbstractInjectMethodStart() {
        targetClass = getTargetClass();
        targetMethod = getTargetMethod();
        targetDesc = getTargetDesc();
        hookReason = getReason();
    }

    public abstract String getTargetClass();
    public abstract String getTargetMethod();
    public abstract String getTargetDesc();
    public abstract String getReason();
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] classBytes) {
        if (transformedName.equals(targetClass))
            return patchClass(classBytes, true);
        return classBytes;
    }
    
    private byte[] patchClass(byte[] classBytes, boolean dev) {
        //String targetMethod = dev ? targetMethodDev : targetMethodObf;

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(classBytes);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        while(methods.hasNext()) {
            MethodNode m = methods.next();
            int fdiv_index = -1;
            if ((m.name.equals(targetMethod) && m.desc.equals(targetDesc))) {
                AbstractInsnNode currentNode = null;
                AbstractInsnNode targetNode = null;
                AbstractInsnNode ain = m.instructions.getFirst();
                InsnList toInject = injectOps(new InsnList());
                m.instructions.insertBefore(ain, toInject);
                Main.LOGGER.info("[i] Patched " + targetClass + "." + targetMethod);
                Main.LOGGER.info("    Reason: " + hookReason);
                break;
            }
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }
    
    public abstract InsnList injectOps(InsnList toInject);
}