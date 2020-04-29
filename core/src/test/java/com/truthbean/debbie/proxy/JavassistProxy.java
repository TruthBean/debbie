package com.truthbean.debbie.proxy;

import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.proxy.asm.AbstractProxy;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-21 17:31.
 */
public class JavassistProxy<B> extends AbstractProxy<B> {

    public JavassistProxy(Class<B> beanClass, MethodProxyHandlerHandler handler,
                          Class<? extends Annotation> methodAnnotation) {
        super(beanClass, handler, methodAnnotation);

    }

    @Override
    public B proxy(Supplier<B> bean) {
        B bytecodeProxy = null;
        try {
            ClassPool classPool = new ClassPool(true);
            Class<B> beanClass = getBeanClass();
            String beanClassName = beanClass.getName();
            CtClass proxyClass;
            if (isInterface()) {
                proxyClass = classPool.makeClass(beanClassName + "JavaassistProxy");
                proxyClass.addInterface(classPool.get(beanClassName));
            } else {
                CtClass beanCtClass = classPool.getCtClass(beanClassName);
                proxyClass = classPool.makeClass(beanClassName + "JavaassistProxy", beanCtClass);
            }
            proxyClass.addConstructor(CtNewConstructor.defaultConstructor(proxyClass));

            String methodProxyHandlerHandlerClassName = MethodProxyHandlerHandler.class.getName();
            String methodCallBackClassName = MethodCallBack.class.getName();
            proxyClass.addField(CtField.make("private " + beanClassName + " target;", proxyClass));
            proxyClass.addField(CtField.make("private " + methodProxyHandlerHandlerClassName + " handler;", proxyClass));

            proxyClass.addMethod(CtNewMethod.make("public void setTarget(" + beanClassName + " target) { this.target = target; }", proxyClass));
            proxyClass.addMethod(CtNewMethod.make("public void setHandler(" + methodProxyHandlerHandlerClassName + " handler) { this.handler = handler; }", proxyClass));

            Set<Method> proxyMethod = getMethods();
            for (Method method : proxyMethod) {
                String methodName = method.getName();
                Class<?> returnType = method.getReturnType();
                String returnTypeName = returnType.getName();
                if (isAnnotationMethod(method)) {
                    proxyClass.addMethod(CtNewMethod.make("public " + returnTypeName + " " + methodName + "() {\n" +
                            "        " + methodCallBackClassName + " callBack = new " + methodCallBackClassName + "(target, \"" + methodName + "\");\n" +
                            "        return this.handler.proxy(callBack);\n" +
                            "    }", proxyClass));
                } else if (returnType == void.class) {
                    proxyClass.addMethod(CtNewMethod.make("public " + returnType + " " + methodName + "() {\n" +
                            "        target." + methodName + "();\n" +
                            "    }", proxyClass));
                } else if (method.isDefault()) {
                    // do nothing
                } else {
                    proxyClass.addMethod(CtNewMethod.make("public " + returnType + " " + methodName + "() {\n" +
                            "        return target." + methodName + "();\n" +
                            "    }", proxyClass));
                }
            }
            /*try {
                proxyClass.writeFile("I:\\DevOps\\java\\debbie\\core\\build\\classes\\java\\test\\");
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            Class<?> proxyType = proxyClass.toClass();
            bytecodeProxy = (B) ReflectionHelper.newInstance(proxyType);

            ReflectionHelper.invokeMethod(bytecodeProxy, "setTarget", new Object[]{bean.get()}, new Class<?>[]{beanClass});
            ReflectionHelper.invokeMethod(bytecodeProxy, "setHandler", new Object[]{getHandler()}, new Class<?>[]{MethodProxyHandlerHandler.class});

        } catch (NotFoundException | CannotCompileException e) {
            LOGGER.error("", e);
        }
        return bytecodeProxy;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JavassistProxy.class);
}
