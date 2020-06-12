package com.truthbean.debbie.proxy;

import com.truthbean.debbie.proxy.asm.AsmGenerated;
import com.truthbean.debbie.proxy.javaassist.JavaassistProxyBean;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.proxy.asm.AbstractProxy;
import com.truthbean.debbie.reflection.asm.AsmConstructorInfo;
import com.truthbean.debbie.reflection.asm.AsmMethodInfo;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
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
            String proxyClassName = beanClassName + "JavaassistProxy";
            CtClass proxyClass;
            if (isInterface()) {
                proxyClass = classPool.makeClass(proxyClassName);
                proxyClass.addInterface(classPool.get(beanClassName));
            } else {
                CtClass beanCtClass = classPool.getCtClass(beanClassName);
                proxyClass = classPool.makeClass(proxyClassName, beanCtClass);
            }
            proxyClass.addInterface(classPool.get(JavaassistProxyBean.class.getName()));
            List<AsmConstructorInfo> constructorInfoList = getConstructorInfoList();
            constructorInfoList.sort(AsmConstructorInfo::compareTo);

            AsmConstructorInfo asmConstructorInfo = constructorInfoList.get(0);
            String constructor = makeConstructor(asmConstructorInfo, proxyClassName.substring(proxyClassName.lastIndexOf(".") + 1));
            CtConstructor newConstructor = CtNewConstructor.make(constructor, proxyClass);
            proxyClass.addConstructor(newConstructor);

            /*CtClass[] parameters = convert(classPool, asmConstructorInfo.getParameterTypes());
            CtClass[] exceptions = convert(classPool, asmConstructorInfo.getExceptions());
            CtConstructor newConstructor = CtNewConstructor.make(parameters, exceptions, null, proxyClass);
            proxyClass.addConstructor(newConstructor);*/

            String methodProxyHandlerHandlerClassName = MethodProxyHandlerHandler.class.getName();

            proxyClass.addField(CtField.make("private " + beanClassName + " target;", proxyClass));
            proxyClass.addField(CtField.make("private " + methodProxyHandlerHandlerClassName + " handler;", proxyClass));

            proxyClass.addMethod(CtNewMethod.make("public void setTarget(" + beanClassName + " target) { this.target = target; }", proxyClass));
            proxyClass.addMethod(CtNewMethod.make("public void setHandler(" + methodProxyHandlerHandlerClassName + " handler) { this.handler = handler; }", proxyClass));

            Set<AsmMethodInfo> proxyMethod = getMethodInfoList();
            for (AsmMethodInfo method : proxyMethod) {
                String methodContent = makeMethod(method);
                proxyClass.addMethod(CtNewMethod.make(methodContent, proxyClass));
            }
            try {
                proxyClass.writeFile("V:\\person\\debbie\\core\\build\\classes\\java\\test\\");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Class<?> proxyType = proxyClass.toClass();

            Object[] objects = asmConstructorInfo.makeConstructorDefaultValue();
            bytecodeProxy = (B) ReflectionHelper.newInstance(proxyType, asmConstructorInfo.getParameterTypes(), objects);

            ReflectionHelper.invokeMethod(bytecodeProxy, "setTarget", new Object[]{bean.get()}, new Class<?>[]{beanClass});
            ReflectionHelper.invokeMethod(bytecodeProxy, "setHandler", new Object[]{getHandler()}, new Class<?>[]{MethodProxyHandlerHandler.class});

        } catch (NotFoundException | CannotCompileException e) {
            LOGGER.error("", e);
        }
        return bytecodeProxy;
    }

    private CtClass[] convert(ClassPool classPool, Type[] classes) {
        if (classes != null && classes.length > 0 && classes[0] != null) {
            CtClass[] ctClasses = new CtClass[classes.length];
            for (int i = 0; i < classes.length; i++) {
                try {
                    ctClasses[i] = classPool.get(classes[i].getTypeName());
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
            return ctClasses;
        }
        return null;
    }

    private CtClass[] convert(ClassPool classPool, String[] classes) {
        if (classes != null && classes.length > 0 && classes[0] != null) {
            CtClass[] ctClasses = new CtClass[classes.length];
            for (int i = 0; i < classes.length; i++) {
                try {
                    ctClasses[i] = classPool.get(classes[i]);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
            return ctClasses;
        }
        return null;
    }

    private String makeConstructor(AsmConstructorInfo constructor, String proxyClassName) {
        StringBuilder builder = new StringBuilder("    public ").append(proxyClassName).append("(");
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        StringBuilder params = new StringBuilder();
        if (parameterTypes.length > 0 && parameterTypes[0] != null) {
            int n = parameterTypes.length - 1;
            for (int i = 0; i < n; i++) {
                Class<?> parameterType = parameterTypes[i];
                builder.append(parameterType.getName()).append(" arg").append(i).append(", ");
                params.append("arg").append(i).append(", ");
            }
            Class<?> parameterType = parameterTypes[n];
            builder.append(parameterType.getName()).append(" arg").append(n);
            params.append("arg").append(n);
        }
        builder.append(")").append(" {\n")
                .append("        super(").append(params).append(");\n")
                .append("    }");
        return builder.toString();
    }

    private String makeMethod(AsmMethodInfo method) {
        String methodName = method.getName();

        StringBuilder methodContext = new StringBuilder();
        if (method.isPublic()) {
            methodContext.append("public ");
        } else if (method.isProtected()) {
            methodContext.append("protected ");
        } else if (!method.isDefault()) {
            return null;
        }

        Class<?> returnType = method.getReturnType();
        String returnTypeName = returnType.getName();
        if (returnType != void.class) {
            methodContext.append(returnTypeName).append(" ");
        } else {
            methodContext.append("void ");
        }
        methodContext.append(methodName).append("(");

        StringBuilder params = new StringBuilder();

        Class<?>[] parameterTypes = method.getParamTypes();
        StringBuilder paramTypes = new StringBuilder("new Class[] {");
        if (parameterTypes.length > 0 && parameterTypes[0] != null) {
            int n = parameterTypes.length - 1;
            for (int i = 0; i < n; i++) {
                Class<?> parameterType = parameterTypes[i];
                methodContext.append(parameterType.getName()).append(" arg").append(i).append(", ");
                params.append("arg").append(i).append(", ");
                paramTypes.append(parameterType.getName()).append(".class, ");
            }
            Class<?> parameterType = parameterTypes[n];
            methodContext.append(parameterType.getName()).append(" arg").append(n);
            params.append("arg").append(n);
            paramTypes.append(parameterType.getName()).append(".class");
        }
        paramTypes.append("}");
        methodContext.append(") ");

        Class<?>[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes.length > 0 && exceptionTypes[0] != null) {
            methodContext.append("throws ");
            int n = exceptionTypes.length - 1;
            for (int i = 0; i < n; i++) {
                Class<?> type = exceptionTypes[i];
                methodContext.append(type.getName()).append(", ");
            }
            Class<?> type = exceptionTypes[n];
            methodContext.append(type.getName());
        }
        methodContext.append(" {\n");
        if (isAnnotationMethod(method)) {
            String methodCallBackClassName = MethodCallBack.class.getName();
            methodContext.append("        ")
                    .append(methodCallBackClassName)
                    .append(" callBack = new ")
                    .append(methodCallBackClassName)
                    .append("(this.target, \"")
                    .append(methodName).append("\", ")
                    .append(paramTypes).append(", ")
                    .append("new Object[] {").append(params).append("}")
                    .append(");\n");
            if (returnType == void.class) {
                methodContext.append("        this.handler.proxy(callBack);\n");
            } else {
                methodContext.append("        return this.handler.proxy(callBack);\n");
            }
        } else {
            if (returnType == void.class) {
                methodContext.append("        return target.")
                        .append(methodName)
                        .append("(")
                        .append(params.toString())
                        .append(");\n");
            } else {
                methodContext.append("        target.")
                        .append(methodName)
                        .append("(")
                        .append(params.toString())
                        .append(");\n");
            }
        }
        methodContext.append("    }");
        return methodContext.toString();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JavassistProxy.class);
}
