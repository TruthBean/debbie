/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.proxy.asm;

import com.truthbean.debbie.proxy.MethodCallBack;
import com.truthbean.debbie.proxy.MethodProxyHandlerHandler;
import com.truthbean.debbie.reflection.ByteArrayClassLoader;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.reflection.asm.AsmClassInfo;
import com.truthbean.debbie.reflection.asm.AsmConstructorInfo;
import com.truthbean.debbie.reflection.asm.AsmMethodInfo;
import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
public class AsmProxy<B> extends AbstractProxy<B> {

    private static final Map<Class<?>, Class<?>> beanAndProxy = new ConcurrentHashMap<>();

    public AsmProxy(Class<B> beanClass, MethodProxyHandlerHandler handler,
                    Class<? extends Annotation> methodAnnotation) {
        super(beanClass, handler, methodAnnotation);
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized B proxy(Supplier<B> bean) {
        Class<B> beanClass = getBeanClass();
        if (beanAndProxy.containsKey(beanClass)) {
            return doProxy((Class<? extends B>) beanAndProxy.get(beanClass), beanClass, bean);
        }
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        AsmClassInfo asmClassInfo = getAsmClassInfo();
        String className = beanClass.getName() + "AsmWrapper";
        String classPath = className.replace('.', '/');

        // proxy class super class
        String superClassPath = beanClass.getName().replace('.', '/');
        if (beanClass.isInterface()) {
            classWriter.visit(Opcodes.V11, Opcodes.ACC_PUBLIC, classPath, asmClassInfo.getSignature(), "java/lang/Object", new String[]{superClassPath});
        } else {
            classWriter.visit(Opcodes.V11, Opcodes.ACC_PUBLIC, classPath, asmClassInfo.getSignature(), superClassPath, null);
        }

        classWriter.visitAnnotation(Type.getDescriptor(AsmGenerated.class), true);

        buildConstructor(classWriter, superClassPath);

        buildTargetField(classWriter, superClassPath);

        String handlerPath = buildHandlerField(classWriter);

        setTarget(classWriter, superClassPath, classPath);

        setHandler(classWriter, handlerPath, classPath);

        Set<AsmMethodInfo> methodInfoList = getMethodInfoList();
        for (AsmMethodInfo method : methodInfoList) {
            if (isAnnotationMethod(method.getMethod())) {
                proxyMethod(classWriter, superClassPath, classPath, handlerPath, method);
            } else {
                subMethod(classWriter, superClassPath, classPath, method);
            }
        }

        classWriter.visitEnd();
        byte[] code = classWriter.toByteArray();

        /*try {
            byte[] data = classWriter.toByteArray();
            File file = new File("V:\\person\\debbie\\core\\build\\classes\\java\\test\\" + classPath + ".class");
            System.out.println(file.getAbsolutePath());
            FileOutputStream out = new FileOutputStream(file);
            out.write(data);
            out.close();
        } catch (IOException e) {
            LOGGER.error("", e);
        }*/

        ClassLoader classLoader = ClassLoaderUtils.getClassLoader(getBeanClass());
        Class<? extends B> proxyClass = (Class<? extends B>) new ByteArrayClassLoader(classLoader).defineClass(className, code);
        LOGGER.trace("asm create Class: " + proxyClass);
        beanAndProxy.put(beanClass, proxyClass);
        return doProxy(proxyClass, beanClass, bean);
    }

    private B doProxy(Class<? extends B> proxyClass, Class<B> beanClass, Supplier<B> bean) {
        B proxy = (B) ReflectionHelper.newInstance(proxyClass);

        ReflectionHelper.invokeSetMethod(proxy, "handler", getHandler(), getHandlerClass());
        ReflectionHelper.invokeSetMethod(proxy, "target", bean.get(), beanClass);

        return proxy;
    }

    private void buildConstructor(ClassWriter classWriter, String superClassPath) {
        MethodVisitor initVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, NAME_CTOR, "()V", null, null);
        // Code:
        initVisitor.visitCode();
        // 0: aload_0
        initVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        Class<B> beanClass = getBeanClass();
        boolean isInterface = beanClass.isInterface();
        if (isInterface) {
            // 1: invokespecial #1                  // Method java/lang/Object."<init>":()V
            initVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, JAVA_LANG_OBJECT, NAME_CTOR, "()V", false);
        } else {
            List<AsmConstructorInfo> constructorInfoList = getConstructorInfoList();
            if (constructorInfoList.isEmpty()) {
                // 1: invokespecial #1                  // Method java/lang/Object."<init>":()V
                initVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassPath, NAME_CTOR, "()V", false);
            } else {
                AsmConstructorInfo constructorInfo = constructorInfoList.get(0);
                Class<?>[] parameterTypes = constructorInfo.getParameterTypes();
                for (Class<?> parameterType : parameterTypes) {
                    if (parameterType == int.class || parameterType == char.class || parameterType == byte.class
                            || parameterType == short.class) {
                        initVisitor.visitInsn(Opcodes.ICONST_0);
                    } else if (parameterType == long.class) {
                        initVisitor.visitInsn(Opcodes.LCONST_0);
                    } else if (parameterType == float.class) {
                        initVisitor.visitInsn(Opcodes.FCONST_0);
                    } else if (parameterType == double.class) {
                        initVisitor.visitInsn(Opcodes.DCONST_0);
                    } else {
                        // 1: aconst_null
                        initVisitor.visitInsn(Opcodes.ACONST_NULL);
                    }
                }
                // 2: invokespecial #1
                initVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassPath, NAME_CTOR, constructorInfo.getDescriptor(), false);
            }
        }
        // 4: return
        initVisitor.visitInsn(Opcodes.RETURN);
        // Maxs computed by ClassWriter.COMPUTE_MAXS, these arguments ignored
        initVisitor.visitMaxs(-1, -1);
        initVisitor.visitEnd();
    }

    private void buildTargetField(ClassWriter classWriter, String superClassPath) {
        // "private " + targetClassName + " target;"
        FieldVisitor targetFieldVisitor = classWriter.visitField(Opcodes.ACC_PRIVATE, "target", "L" + superClassPath + ";", null, null);
        targetFieldVisitor.visitEnd();
    }

    private String buildHandlerField(ClassWriter classWriter) {
        // "private " + handlerClassName + " handler;"
        Class<MethodProxyHandlerHandler> handlerClass = getHandlerClass();
        String handlerPath = handlerClass.getName().replace('.', '/');
        FieldVisitor handlerFieldVisitor = classWriter.visitField(Opcodes.ACC_PRIVATE, "handler", "L" + handlerPath + ";", null, null);
        handlerFieldVisitor.visitEnd();
        return handlerPath;
    }

    private void setTarget(ClassWriter classWriter, String superClassPath, String classPath) {
        // "public void setTarget(" + targetClassName + " target) { this.target = target; }"
        MethodVisitor setTargetMethodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "setTarget", "(L" + superClassPath + ";)V", null, null);
        // Code:
        setTargetMethodVisitor.visitCode();
        // 0: aload_0
        setTargetMethodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        // 1: aload_1
        setTargetMethodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        // 2: putfield      #21                 // Field target
        setTargetMethodVisitor.visitFieldInsn(Opcodes.PUTFIELD, classPath, "target", "L" + superClassPath + ";");
        // 5: return
        setTargetMethodVisitor.visitInsn(Opcodes.RETURN);
        setTargetMethodVisitor.visitMaxs(1, 0);
        setTargetMethodVisitor.visitEnd();
    }

    private void setHandler(ClassWriter classWriter, String handlerPath, String classPath) {
        // "public void setHandler(" + handlerClassName + " handler) { this.handler = handler; }
        MethodVisitor setHandlerMethodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "setHandler", "(L" + handlerPath + ";)V", null, null);
        // Code:
        setHandlerMethodVisitor.visitCode();
        // 0: aload_0
        setHandlerMethodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        // 1: aload_1
        setHandlerMethodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        // 2: putfield      #25                 // Field handler
        setHandlerMethodVisitor.visitFieldInsn(Opcodes.PUTFIELD, classPath, "handler", "L" + handlerPath + ";");
        // 5: return
        setHandlerMethodVisitor.visitInsn(Opcodes.RETURN);
        setHandlerMethodVisitor.visitMaxs(0, 0);
        setHandlerMethodVisitor.visitEnd();
    }

    private void proxyMethod(ClassWriter classWriter, String superClassPath, String classPath, String handlerPath,
                             AsmMethodInfo methodInfo) {
        Method method = methodInfo.getMethod();
        String name = methodInfo.getName();
        Class<?>[] paramTypes = methodInfo.getParamTypes();
        MethodVisitor methodVisitor = classWriter.visitMethod(methodInfo.getAccess(), name,
                methodInfo.getDescriptor(), methodInfo.getSignature(), methodInfo.getExceptions());
        // Code:
        methodVisitor.visitCode();

        // 0: new           #28                 // class com/truthbean/debbie/proxy/MethodCallBack
        String methodCallBackClass = Type.getInternalName(MethodCallBack.class);
        methodVisitor.visitTypeInsn(Opcodes.NEW, methodCallBackClass);
        // 3: dup
        methodVisitor.visitInsn(Opcodes.DUP);
        // 4: aload_0
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        // 5: getfield      #21                 // Field target:
        methodVisitor.visitFieldInsn(Opcodes.GETFIELD, classPath, "target", "L" + superClassPath + ";");
        // 8: ldc           #29
        methodVisitor.visitLdcInsn(name);
        boolean hasParams = methodInfo.hasParams();
        if (hasParams) {
            int length = paramTypes.length;
            // 10: iconst_1
            methodVisitor.visitInsn(Opcodes.ICONST_0 + length);
            // 11: anewarray     #6                  // class java/lang/Class
            methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, Type.getInternalName(Class.class));
            // 14: dup
            methodVisitor.visitInsn(Opcodes.DUP);
            // 15: iconst_0
            methodVisitor.visitInsn(Opcodes.ICONST_0);
            // ??
            // 16: aload_1
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            // 19: aastore
            methodVisitor.visitInsn(Opcodes.AASTORE);

            // 20: iconst_1
            methodVisitor.visitInsn(Opcodes.ICONST_1);
            // 21: anewarray     #6                  // class java/lang/Class
            methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, Type.getInternalName(Object.class));
            // 24: dup
            methodVisitor.visitInsn(Opcodes.DUP);
            // 25: iconst_0
            methodVisitor.visitInsn(Opcodes.ICONST_0);
            for (int i = 0; i < 1; i++) {
                Class<?> paramType = paramTypes[i];
                if (paramType == double.class) {
                    // 26: dload_1
                    methodVisitor.visitVarInsn(Opcodes.DLOAD, 1);
                    // 27: invokestatic  #11                 // Method java/lang/Double.valueOf:(D)Ljava/lang/Double;
                    methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                } else if (paramType == float.class) {
                    // 26: fload_1
                    methodVisitor.visitVarInsn(Opcodes.FLOAD, 1);
                    // 27: invokestatic  #11                 // Method java/lang/Float.valueOf:(F)Ljava/lang/Float;
                    methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                } else if (paramType == long.class) {
                    // 26: lload_1
                    methodVisitor.visitVarInsn(Opcodes.LLOAD, 1);
                    // 27: invokestatic  #11                 // Method java/lang/Long.valueOf:(L)Ljava/lang/Long;
                    methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(L)Ljava/lang/Long;", false);
                } else if (paramType == int.class) {
                    // 26: iload_1
                    methodVisitor.visitVarInsn(Opcodes.ILOAD, 1);
                    // 27: invokestatic  #11                 // Method java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
                    methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                } else if (paramType == char.class) {
                    // 26: iload_1
                    methodVisitor.visitVarInsn(Opcodes.ILOAD, 1);
                    // 27: invokestatic  #11                 // Method java/lang/Character.valueOf:(C)Ljava/lang/Character;
                    methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                } else if (paramType == byte.class) {
                    // 26: iload_1
                    methodVisitor.visitVarInsn(Opcodes.ILOAD, 1);
                    // 27: invokestatic  #11                 // Method java/lang/Byte.valueOf:(B)Ljava/lang/Byte;
                    methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
                } else if (paramType == short.class) {
                    // 26: iload_1
                    methodVisitor.visitVarInsn(Opcodes.ILOAD, 1);
                    // 27: invokestatic  #11                 // Method java/lang/Short.valueOf:(S)Ljava/lang/Short;
                    methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                } else if (paramType == boolean.class) {
                    // 26: iload_1
                    methodVisitor.visitVarInsn(Opcodes.ILOAD, 1);
                    // 27: invokestatic  #11                 // Method java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
                    methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                } else {
                    // 26: aload_1
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                }
            }
            // 30: aastore
            methodVisitor.visitInsn(Opcodes.AASTORE);
            // 31: invokespecial #32                 // Method com/truthbean/debbie/proxy/MethodCallBack."<init>":(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Class;[Ljava/lang/Object;)V
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, methodCallBackClass, "<init>", "(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Class;[Ljava/lang/Object;)V", false);
            // 34: astore_2
            methodVisitor.visitVarInsn(Opcodes.ASTORE, 2);
            // 35: aload_0
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            // 36: getfield      #25                 // Field handler:Lcom/truthbean/debbie/proxy/MethodProxyHandlerHandler;
            methodVisitor.visitFieldInsn(Opcodes.GETFIELD, classPath, "handler", "L" + handlerPath + ";");
            // 39: aload_2
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);
            // 40: invokevirtual #38                 // Method com/truthbean/debbie/proxy/MethodProxyHandlerHandler.proxy:(Lcom/truthbean/debbie/proxy/MethodCallBack;)Ljava/lang/Object;
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, handlerPath, "proxy", "(Lcom/truthbean/debbie/proxy/MethodCallBack;)Ljava/lang/Object;", false);
            // 43: pop
            methodVisitor.visitInsn(Opcodes.POP);
        } else {
            // 10: invokespecial #9                 // Method com/truthbean/debbie/proxy/MethodCallBack."<init>":(Ljava/lang/Object;Ljava/lang/String;)V
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, methodCallBackClass, "<init>", "(Ljava/lang/Object;Ljava/lang/String;)V", false);
            // 13: astore_1
            methodVisitor.visitVarInsn(Opcodes.ASTORE, 1);
            // 14: aload_0
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            // 15: getfield      #4                 // Field handler:Lcom/truthbean/debbie/proxy/MethodProxyHandlerHandler;
            methodVisitor.visitFieldInsn(Opcodes.GETFIELD, classPath, "handler", "L" + handlerPath + ";");
            // 18: aload_1
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            // 19: invokevirtual #10                 // Method com/truthbean/debbie/proxy/MethodProxyHandlerHandler.proxy:(Lcom/truthbean/debbie/proxy/MethodCallBack;)Ljava/lang/Object;
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, handlerPath, "proxy", "(Lcom/truthbean/debbie/proxy/MethodCallBack;)Ljava/lang/Object;", false);
            // checkcast
            methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(methodInfo.getReturnWrapperType()));
        }
        // return
        Class<?> returnType = methodInfo.getReturnType();
        if (returnType == void.class) {
            methodVisitor.visitInsn(Opcodes.RETURN);
        } else if (returnType == double.class) {
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Double.class), "doubleValue", "()D", false);
            methodVisitor.visitInsn(Opcodes.DRETURN);
        } else if (returnType == float.class) {
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Float.class), "floatValue", "()F", false);
            methodVisitor.visitInsn(Opcodes.FRETURN);
        } else if (returnType == long.class) {
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Long.class), "longValue", "()J", false);
            methodVisitor.visitInsn(Opcodes.LRETURN);
        } else if (returnType == int.class) {
            // invokevirtual #10                 // Method java/lang/Integer.intValue:()I
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Integer.class), "intValue", "()I", false);
            methodVisitor.visitInsn(Opcodes.IRETURN);
        } else if (returnType == char.class) {
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Character.class), "charValue", "()C", false);
            methodVisitor.visitInsn(Opcodes.IRETURN);
        } else if (returnType == byte.class) {
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Byte.class), "byteValue", "()B", false);
            methodVisitor.visitInsn(Opcodes.IRETURN);
        } else if (returnType == short.class) {
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Short.class), "shortValue", "()S", false);
            methodVisitor.visitInsn(Opcodes.IRETURN);
        } else if (returnType == boolean.class) {
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Boolean.class), "booleanValue", "()Z", false);
            methodVisitor.visitInsn(Opcodes.IRETURN);
        } else {
            methodVisitor.visitInsn(Opcodes.ARETURN);
        }
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }

    private void subMethod(ClassWriter classWriter, String superClassPath, String classPath, AsmMethodInfo methodInfo) {
        String methodName = methodInfo.getName();
        String descriptor = methodInfo.getDescriptor();
        MethodVisitor subMethodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, methodName, descriptor,
                methodInfo.getSignature(), methodInfo.getExceptions());
        // Code:
        subMethodVisitor.visitCode();
        // 0: aload_0
        subMethodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        // 1: getfield
        subMethodVisitor.visitFieldInsn(Opcodes.GETFIELD, classPath, "target", "L" + superClassPath + ";");
        if (isInterface())
            // 4: invokeinterface #44,  1
            subMethodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, superClassPath, methodName, descriptor, true);
        else
            // 4: invokevirtual #6                  //
            subMethodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, superClassPath, methodName, descriptor, false);
        Class<?> returnType = methodInfo.getReturnType();
        if (returnType == void.class) {
            // 7: return
            subMethodVisitor.visitInsn(Opcodes.RETURN);
        } else if (returnType == double.class) {
            subMethodVisitor.visitInsn(Opcodes.DRETURN);
        } else if (returnType == float.class) {
            subMethodVisitor.visitInsn(Opcodes.FRETURN);
        } else if (returnType == long.class) {
            subMethodVisitor.visitInsn(Opcodes.LRETURN);
        } else if (returnType == int.class || returnType == char.class || returnType == byte.class
                || returnType == short.class) {
            subMethodVisitor.visitInsn(Opcodes.IRETURN);
        } else {
            subMethodVisitor.visitInsn(Opcodes.ARETURN);
        }
        subMethodVisitor.visitMaxs(0, 0);
        subMethodVisitor.visitEnd();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AsmProxy.class);
}
