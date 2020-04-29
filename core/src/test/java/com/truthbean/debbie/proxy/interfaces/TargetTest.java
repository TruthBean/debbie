package com.truthbean.debbie.proxy.interfaces;

import com.truthbean.debbie.proxy.*;
import com.truthbean.debbie.proxy.asm.AsmProxy;
import com.truthbean.debbie.proxy.bean.TargetImplement;
import com.truthbean.debbie.proxy.bean.TargetInterface;

import com.truthbean.debbie.proxy.bean.TargetObject;
import com.truthbean.debbie.proxy.interfaces.cglib.TargetMethodInterceptor;
import com.truthbean.debbie.proxy.interfaces.jdk.JdkInvocationHandler;
import com.truthbean.debbie.reflection.ByteArrayClassLoader;
import com.truthbean.debbie.reflection.ReflectionHelper;

import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class TargetTest {

    @Test
    public void direct() throws Exception {
        TargetProxyHandler interceptor = new TargetProxyHandler();
        List<MethodProxyHandler> list = List.of(interceptor);
        TargetObject target = new TargetObject("hello", 1, (char) 2, (byte) 3, (short) 4, 5L, 6.7F, 8.90D);

        var proxy = new MethodProxyHandlerHandler(LOGGER);
        proxy.setInterceptors(list);

        MethodCallBack<Void> callBack = new MethodCallBack<Void>(target, "code", "java");

        proxy.proxy(callBack);
    }

    @Test
    public void jdkProxy() {
        TargetInterface target = new TargetImplement();
        JdkInvocationHandler<TargetInterface> handler = new JdkInvocationHandler<>(TargetInterface.class, target);
        TargetInterface proxy = handler.newProxyInstance();
        proxy.coding();
    }

    @Test
    public void cglibProxy() throws Exception {
        Enhancer enhancer = new Enhancer();
        TargetObject target = new TargetObject("hello", 1, (char) 2, (byte) 3, (short) 4, 5L, 6.7F, 8.90D);
        enhancer.setCallback(new TargetMethodInterceptor(target, new TargetProxyHandler(), LOGGER));
        // enhancer.setInterfaces(new Class[]{TargetInterface.class});
        enhancer.setSuperclass(TargetObject.class);
        TargetObject proxy = (TargetObject) enhancer.create();
        proxy.code("c++");
    }

    @Test
    public void javassistProxy() throws Exception {
        TargetProxyHandler interceptor = new TargetProxyHandler();
        MethodProxyHandlerHandler handler = new MethodProxyHandlerHandler(LOGGER);
        handler.setInterceptors(List.of(interceptor));

        TargetObject targetBean = new TargetObject("hello", 1, (char) 2, (byte) 3, (short) 4, 5L, 6.7F, 8.90D);
        JavassistProxy<TargetObject> javassistProxy = new JavassistProxy<>(TargetObject.class, handler, MethodProxy.class);
        TargetObject proxy = javassistProxy.proxy(targetBean);
        proxy.learn();
        proxy.coding();
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");

        proxy.code("java");
    }

    @Test
    public void asmProxy() {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        String className = TargetInterface.class.getName() + "AsmProxy";
        String classPath = className.replace('.', '/');

        // proxy class interface
        String interfacePath = TargetInterface.class.getName().replace('.', '/');
        classWriter.visit(Opcodes.V11, Opcodes.ACC_PUBLIC, classPath, null, "java/lang/Object", new String[]{interfacePath});

        // classWriter.visit(Opcodes.V11, Opcodes.ACC_PUBLIC, classPath, null, "java/lang/Object", new String[]{interceptorPath});

        MethodVisitor initVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        initVisitor.visitCode();
        initVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        initVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        initVisitor.visitInsn(Opcodes.RETURN);
        initVisitor.visitMaxs(0, 0);
        initVisitor.visitEnd();

        // "private " + targetClassName + " target;"
        FieldVisitor targetFieldVisitor = classWriter.visitField(Opcodes.ACC_PRIVATE, "target", "L" + interfacePath + ";", null, null);
        targetFieldVisitor.visitEnd();

        // "private " + interceptorClassName + " interceptor;"
        String interceptorPath = TargetProxyHandler.class.getName().replace('.', '/');
        FieldVisitor interceptorFieldVisitor = classWriter.visitField(Opcodes.ACC_PRIVATE, "interceptor", "L" + interceptorPath + ";", null, null);
        interceptorFieldVisitor.visitEnd();

        // "public void setTarget(" + targetClassName + " target) { this.target = target; }"
        MethodVisitor setTargetMethodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "setTarget", "(L" + interfacePath + ";)V", null, null);
        setTargetMethodVisitor.visitCode();
        setTargetMethodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        setTargetMethodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        setTargetMethodVisitor.visitFieldInsn(Opcodes.PUTFIELD, classPath, "target", "L" + interfacePath + ";");
        setTargetMethodVisitor.visitInsn(Opcodes.RETURN);
        setTargetMethodVisitor.visitMaxs(1, 0);
        setTargetMethodVisitor.visitEnd();

        // "public void setInterceptor(" + interceptorClassName + " interceptor) { this.interceptor = interceptor; }
        MethodVisitor setInterceptorMethodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "setInterceptor", "(L" + interceptorPath + ";)V", null, null);
        setInterceptorMethodVisitor.visitCode();
        setInterceptorMethodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        setInterceptorMethodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        setInterceptorMethodVisitor.visitFieldInsn(Opcodes.PUTFIELD, classPath, "interceptor", "L" + interceptorPath + ";");
        setInterceptorMethodVisitor.visitInsn(Opcodes.RETURN);
        setInterceptorMethodVisitor.visitMaxs(0, 0);
        setInterceptorMethodVisitor.visitEnd();

        // "public void code() { interceptor.before();\n\ntarget.code();\n\ninterceptor.after(); }"
        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "code", "()V", null, null);
        methodVisitor.visitCode();
        // interceptor.before();
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitFieldInsn(Opcodes.GETFIELD, classPath, "interceptor", "L" + interceptorPath + ";");
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, interceptorPath, "before", "()V", false);
        // target.code();
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitFieldInsn(Opcodes.GETFIELD, classPath, "target", "L" + interfacePath + ";");
        methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, interfacePath, "code", "()V", true);
        // interceptor.after();
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitFieldInsn(Opcodes.GETFIELD, classPath, "interceptor", "L" + interceptorPath + ";");
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, interceptorPath, "after", "()V", false);
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();

        classWriter.visitEnd();
        byte[] code = classWriter.toByteArray();

        try {
            byte[] data = classWriter.toByteArray();
            File file = new File("I:\\DevOps\\java\\debbie\\core\\build\\classes\\java\\test\\com\\truthbean\\debbie\\proxy\\TargetInterfaceTest.class");
            FileOutputStream out = new FileOutputStream(file);
            out.write(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Class proxyClass = new ByteArrayClassLoader(TargetInterface.class.getClassLoader()).defineClass(className, code);
        System.out.println(proxyClass);
        TargetInterface proxy = (TargetInterface) ReflectionHelper.newInstance(proxyClass);

        TargetProxyHandler interceptor = new TargetProxyHandler();
        TargetInterface target = new TargetImplement();
        ReflectionHelper.invokeSetMethod(proxy, "interceptor", interceptor, TargetProxyHandler.class);
        ReflectionHelper.invokeSetMethod(proxy, "target", target, TargetInterface.class);

        proxy.code();

    }

    @Test
    void testAsmProxy() throws Exception {
        TargetObject targetBean = new TargetObject("hello", 1, (char) 2, (byte) 3, (short) 4, 5L, 6.7F, 8.90D);
        // TargetInterface targetBean = new TargetImplement();
        TargetProxyHandler interceptor = new TargetProxyHandler();
        MethodProxyHandlerHandler handler = new MethodProxyHandlerHandler(LOGGER);
        handler.setInterceptors(List.of(interceptor));

        AsmProxy<TargetObject> asmProxy = new AsmProxy<>(TargetObject.class, handler, MethodProxy.class);
        TargetObject proxy = asmProxy.proxy(targetBean);

        Set<Method> proxyMethod = asmProxy.getProxyMethods();
        for (Method method : proxyMethod) {
            System.out.println(method.getName());
        }

        proxy.learn();
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");

        proxy.code("rust");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TargetTest.class);
}
