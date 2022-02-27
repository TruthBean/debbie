package com.truthbean.debbie.asm.reflect;

import com.truthbean.debbie.reflection.TypeHelper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/01/03 13:21.
 */
public class AsmTypeHelper extends TypeHelper {
    public static int getAccessByModifiers(int modifier) {
        if (Modifier.isPublic(modifier)) {
            return Opcodes.ACC_PUBLIC;
        } else if (Modifier.isProtected(modifier)) {
            return  Opcodes.ACC_PROTECTED;
        } else if (Modifier.isPrivate(modifier)) {
            return  Opcodes.ACC_PRIVATE;
        } else if (Modifier.isStatic(modifier)) {
            return  Opcodes.ACC_STATIC;
        } else if (Modifier.isFinal(modifier)) {
            return  Opcodes.ACC_FINAL;
        } else if (Modifier.isSynchronized(modifier)) {
            return Opcodes.ACC_SYNCHRONIZED;
        }
        return 0;
    }

    public static Type[] getTypes(@SuppressWarnings("rawtypes") Class[] classes) {
        if (classes == null) {
            return new Type[0];
        }
        Type[] types = new Type[classes.length];
        for (int i = 0; i < classes.length; i++) {
            types[i] = Type.getType(classes[i]);
        }
        return types;
    }
}
