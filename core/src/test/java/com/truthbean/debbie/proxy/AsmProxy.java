package com.truthbean.debbie.proxy;

import com.truthbean.debbie.reflection.ByteArrayClassLoader;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class AsmProxy<B> {

    private final MethodProxyHandlerHandler handler;

    private final Class<B> beanClass;
    private final Class<MethodProxyHandlerHandler> handlerClass;
    private final Class<? extends Annotation> methodAnnotation;

    public AsmProxy(Class<B> beanClass, MethodProxyHandlerHandler handler,
                    Class<? extends Annotation> methodAnnotation) {

        this.handler = handler;

        this.beanClass = beanClass;
        this.handlerClass = (Class<MethodProxyHandlerHandler>) handler.getClass();

        this.methodAnnotation = methodAnnotation;
    }

    public List<Method> getProxyMethod() {
        List<Method> declaredMethods = ReflectionHelper.getDeclaredMethods(beanClass);
        return declaredMethods.stream()
                .filter((method) -> methodAnnotation.isInstance(method.getAnnotation(methodAnnotation)))
                .collect(Collectors.toList());
    }

    public List<MethodInfo> getProxyMethodInfo() {
        List<Method> declaredMethods = ReflectionHelper.getDeclaredMethods(beanClass);
        return declaredMethods.stream()
                .filter((method) -> methodAnnotation.isInstance(method.getAnnotation(methodAnnotation)))
                .map(MethodInfo::new)
                .collect(Collectors.toList());
    }

    public B proxy(B bean) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        String className = beanClass.getName() + "AsmProxy";
        String classPath = className.replace('.', '/');

        // proxy class super class
        String superClassPath = beanClass.getName().replace('.', '/');
        if (beanClass.isInterface()) {
            classWriter.visit(Opcodes.V11, Opcodes.ACC_PUBLIC, classPath, null, null, new String[]{superClassPath});
        } else {
            classWriter.visit(Opcodes.V11, Opcodes.ACC_PUBLIC, classPath, null, superClassPath, null);
        }


        buildConstructor(classWriter, superClassPath);

        buildTargetField(classWriter, superClassPath);

        String interceptorHandlerPath = buildInterceptorHandlerField(classWriter);

        setTarget(classWriter, superClassPath, classPath);

        setInterceptorHandler(classWriter, interceptorHandlerPath, classPath);

        List<MethodInfo> methodInfoList = getProxyMethodInfo();
        methodInfoList.forEach((methodInfo -> proxyMethod(classWriter, superClassPath, classPath, interceptorHandlerPath, methodInfo)));

        classWriter.visitEnd();
        byte[] code = classWriter.toByteArray();

        try {
            byte[] data = classWriter.toByteArray();
            File file = new File("I:\\DevOps\\java\\debbie\\core\\out\\test\\classes\\com\\truthbean\\debbie\\proxy\\TargetInterfaceAsmProxy.class");
            FileOutputStream out = new FileOutputStream(file);
            out.write(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Class<?> proxyClass = new ByteArrayClassLoader().getClass(className, code);
        System.out.println(proxyClass);
        B proxy = (B) ReflectionHelper.newInstance(proxyClass);

        ReflectionHelper.invokeSetMethod(proxy, "handler", handler, handlerClass);
        ReflectionHelper.invokeSetMethod(proxy, "target", bean, beanClass);

        return proxy;
    }

    private void buildConstructor(ClassWriter classWriter, String superClassPath) {
        MethodVisitor initVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        initVisitor.visitCode();
        initVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        boolean isInterface = beanClass.isInterface();
        initVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassPath, "<init>", "()V", isInterface);
        initVisitor.visitInsn(Opcodes.RETURN);
        initVisitor.visitMaxs(0, 0);
        initVisitor.visitEnd();
    }

    private void buildTargetField(ClassWriter classWriter, String superClassPath) {
        // "private " + targetClassName + " target;"
        FieldVisitor targetFieldVisitor = classWriter.visitField(Opcodes.ACC_PRIVATE, "target", "L" + superClassPath + ";", null, null);
        targetFieldVisitor.visitEnd();
    }

    private String buildInterceptorHandlerField(ClassWriter classWriter) {
        // "private " + handlerClassName + " handler;"
        String interceptorHandlerPath = handlerClass.getName().replace('.', '/');
        FieldVisitor interceptorFieldVisitor = classWriter.visitField(Opcodes.ACC_PRIVATE, "handler", "L" + interceptorHandlerPath + ";", null, null);
        interceptorFieldVisitor.visitEnd();
        return interceptorHandlerPath;
    }

    private void setTarget(ClassWriter classWriter, String superClassPath, String classPath) {
        // "public void setTarget(" + targetClassName + " target) { this.target = target; }"
        MethodVisitor setTargetMethodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "setTarget", "(L" + superClassPath + ";)V", null, null);
        setTargetMethodVisitor.visitCode();
        setTargetMethodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        setTargetMethodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        setTargetMethodVisitor.visitFieldInsn(Opcodes.PUTFIELD, classPath, "target", "L" + superClassPath + ";");
        setTargetMethodVisitor.visitInsn(Opcodes.RETURN);
        setTargetMethodVisitor.visitMaxs(1, 0);
        setTargetMethodVisitor.visitEnd();
    }

    private void setInterceptorHandler(ClassWriter classWriter, String interceptorHandlerPath, String classPath) {
        // "public void setHandler(" + handlerClassName + " handler) { this.handler = handler; }
        MethodVisitor setInterceptorMethodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "setHandler", "(L" + interceptorHandlerPath + ";)V", null, null);
        setInterceptorMethodVisitor.visitCode();
        setInterceptorMethodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        setInterceptorMethodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        setInterceptorMethodVisitor.visitFieldInsn(Opcodes.PUTFIELD, classPath, "handler", "L" + interceptorHandlerPath + ";");
        setInterceptorMethodVisitor.visitInsn(Opcodes.RETURN);
        setInterceptorMethodVisitor.visitMaxs(0, 0);
        setInterceptorMethodVisitor.visitEnd();
    }

    private void proxyMethod(ClassWriter classWriter, String superClassPath, String classPath, String interceptorHandlerPath,
                             MethodInfo methodInfo) {
        // "public void code() { interceptor.before();\n\ntarget.code();\n\ninterceptor.after(); }"
        // MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "code", "()V", null, null);
        MethodVisitor methodVisitor = classWriter.visitMethod(methodInfo.getAccess(), methodInfo.getName(),
                methodInfo.getDescriptor(), methodInfo.getSignature(), methodInfo.getExceptions());
        methodVisitor.visitCode();

        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitFieldInsn(Opcodes.GETFIELD, classPath, "handler", "L" + interceptorHandlerPath + ";");
        methodVisitor.visitLdcInsn(methodInfo.getName());

        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        // Handle handle = new Handle(Opcodes.INVOKEVIRTUAL, superClassPath, methodInfo.getName(), methodInfo.getDescriptor(), beanClass.isInterface());
        // methodVisitor.visitInvokeDynamicInsn("get", "(L"+classPath+";)Ljava/util/function/Supplier;", handle);

        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, interceptorHandlerPath, "proxy",
                "(Ljava/lang/String;Ljava/util/function/Supplier;)Ljava/lang/Object;", false);
        methodVisitor.visitInsn(Opcodes.POP);
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }
}
