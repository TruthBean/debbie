package com.truthbean.debbie.proxy.interfaces;

import com.truthbean.debbie.proxy.*;
import com.truthbean.debbie.proxy.bean.TargetImplement;
import com.truthbean.debbie.proxy.bean.TargetInterface;
import com.truthbean.debbie.proxy.interfaces.cglib.TargetMethodInterceptor;
import com.truthbean.debbie.proxy.interfaces.jdk.TargetInvocationHandler;
import com.truthbean.debbie.reflection.ByteArrayClassLoader;
import com.truthbean.debbie.reflection.ReflectionHelper;
import javassist.*;
import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class TargetTest {

    @Test
    public void direct() throws Throwable {
        TargetProxyHandler interceptor = new TargetProxyHandler();
        List<MethodProxyHandler> list = List.of(interceptor);
        TargetInterface target = new TargetImplement();

        var proxy = new BeanMethodProxy<>(target, list);
        proxy.proxy("code", () -> {
            target.code();
            return null;
        });
    }

    @Test
    public void jdkProxy() {
        TargetInterface target = new TargetImplement();
        TargetInvocationHandler handler = new TargetInvocationHandler(target);
        TargetInterface proxy = (TargetInterface) Proxy.newProxyInstance(target.getClass().getClassLoader(), new Class[]{TargetInterface.class}, handler);
        proxy.code();
    }

    @Test
    public void cglibProxy() {
        Enhancer enhancer = new Enhancer();
        TargetInterface target = new TargetImplement();
        enhancer.setCallback(new TargetMethodInterceptor(target));
        enhancer.setInterfaces(new Class[]{TargetInterface.class});
        TargetInterface proxy = (TargetInterface) enhancer.create();
        proxy.code();
    }

    @Test
    public void javassistProxy() {
        TargetProxyHandler interceptor = new TargetProxyHandler();
        TargetInterface target = new TargetImplement();

        try {
            ClassPool classPool = new ClassPool(true);
            CtClass proxyClass = classPool.makeClass(TargetInterface.class.getName() + "JavaassistProxy");
            proxyClass.addInterface(classPool.get(TargetInterface.class.getName()));
            proxyClass.addConstructor(CtNewConstructor.defaultConstructor(proxyClass));

            String targetClassName = TargetInterface.class.getName();
            String interceptorClassName = TargetProxyHandler.class.getName();
            proxyClass.addField(CtField.make("private " + targetClassName + " target;", proxyClass));
            proxyClass.addField(CtField.make("private " + interceptorClassName + " interceptor;", proxyClass));

            proxyClass.addMethod(CtNewMethod.make("public void code() { interceptor.before();\n\ntarget.code();\n\ninterceptor.after(); }", proxyClass));

            proxyClass.addMethod(CtNewMethod.make("public void setTarget(" + targetClassName + " target) { this.target = target; }", proxyClass));
            proxyClass.addMethod(CtNewMethod.make("public void setInterceptor(" + interceptorClassName + " interceptor) { this.interceptor = interceptor; }", proxyClass));

            Class<?> proxy = proxyClass.toClass();
            TargetInterface bytecodeProxy = (TargetInterface) ReflectionHelper.newInstance(proxy);
            ReflectionHelper.invokeMethod(bytecodeProxy, "setTarget", new Object[]{target}, new Class<?>[]{TargetInterface.class});
            ReflectionHelper.invokeMethod(bytecodeProxy, "setInterceptor", interceptor);

            bytecodeProxy.code();
        } catch (NotFoundException | CannotCompileException e) {
            LOGGER.error("", e);
        }
    }

    @Test
    public void asmProxy() {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        String className = TargetInterface.class.getName() +  "AsmProxy";
        String classPath = className.replace('.', '/');

        // proxy class interface
        String interfacePath = TargetInterface.class.getName().replace('.', '/');
        classWriter.visit(Opcodes.V11, Opcodes.ACC_PUBLIC, classPath, null, "java/lang/Object", new String[] {interfacePath});

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

        /*try {
            byte[] data = classWriter.toByteArray();
            File file = new File("V:\\person\\debbie\\core\\out\\test\\classes\\com\\truthbean\\debbie\\proxy\\TargetInterfaceTest.class");
            FileOutputStream out = new FileOutputStream(file);
            out.write(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        Class proxyClass = new ByteArrayClassLoader().getClass(className, code);
        System.out.println(proxyClass);
        TargetInterface proxy = (TargetInterface) ReflectionHelper.newInstance (proxyClass);

        TargetProxyHandler interceptor = new TargetProxyHandler();
        TargetInterface target = new TargetImplement();
        ReflectionHelper.invokeSetMethod(proxy, "interceptor", interceptor, TargetProxyHandler.class);
        ReflectionHelper.invokeSetMethod(proxy, "target", target, TargetInterface.class);

        proxy.code();

    }

    @Test
    void testAsmProxy() {
        TargetInterface targetBean = new TargetImplement();
        TargetProxyHandler interceptor = new TargetProxyHandler();
        MethodProxyHandlerHandler handler = new MethodProxyHandlerHandler();
        handler.setLogger(LOGGER);
        handler.setInterceptors(List.of(interceptor));

        AsmProxy<TargetInterface> asmProxy = new AsmProxy<>(TargetInterface.class, handler, MethodProxy.class);
        TargetInterface proxy = asmProxy.proxy(targetBean);

        List<Method> proxyMethod = asmProxy.getProxyMethod();
        for (Method method : proxyMethod) {
            System.out.println(method.getName());
        }

        proxy.learn();
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");

        proxy.code();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TargetTest.class);
}
