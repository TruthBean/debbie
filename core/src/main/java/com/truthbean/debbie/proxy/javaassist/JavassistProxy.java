///**
// * Copyright (c) 2021 TruthBean(Rogar·Q)
// * Debbie is licensed under Mulan PSL v2.
// * You can use this software according to the terms and conditions of the Mulan PSL v2.
// * You may obtain a copy of Mulan PSL v2 at:
// * http://license.coscl.org.cn/MulanPSL2
// * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
// * See the Mulan PSL v2 for more details.
// */
//package com.truthbean.debbie.proxy.javaassist;
//
//import com.truthbean.Logger;
//import com.truthbean.debbie.proxy.MethodCallBack;
//import com.truthbean.debbie.proxy.MethodProxyHandlerHandler;
//import com.truthbean.debbie.proxy.asm.AbstractProxy;
//import com.truthbean.debbie.reflection.ClassInfo;
//import com.truthbean.debbie.reflection.ReflectionHelper;
//import com.truthbean.debbie.reflection.TypeHelper;
//import com.truthbean.debbie.reflection.asm.AsmConstructorInfo;
//import com.truthbean.debbie.reflection.asm.AsmMethodInfo;
//import com.truthbean.common.mini.util.OsUtils;
//import com.truthbean.LoggerFactory;
//import javassist.*;
//
//import java.io.IOException;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Type;
//import java.net.URL;
//import java.util.List;
//import java.util.Set;
//import java.util.function.Supplier;
//
///**
// * @author truthbean/Rogar·Q
// * @since 0.0.2
// * Created on 2020-04-21 17:31.
// */
//public class JavassistProxy<B> extends AbstractProxy<B> {
//
//    public JavassistProxy(Class<B> beanClass, ClassLoader classLoader, MethodProxyHandlerHandler handler,
//                          Class<? extends Annotation> methodAnnotation) {
//        super(beanClass, classLoader, handler, methodAnnotation);
//    }
//
//    public JavassistProxy(ClassInfo<B> beanClassInfo, ClassLoader classLoader, MethodProxyHandlerHandler handler,
//                          Class<? extends Annotation> methodAnnotation) {
//        super(beanClassInfo, classLoader, handler, methodAnnotation);
//    }
//
//    @Override
//    public B proxy(Supplier<B> bean) {
//        try {
//            ClassPool classPool = new ClassPool(true);
//            Class<B> beanClass = getBeanClass();
//            String beanClassName = beanClass.getName();
//            String proxyClassName = beanClassName + "JavaassistProxy";
//            CtClass proxyClass;
//            if (isInterface()) {
//                proxyClass = classPool.makeClass(proxyClassName);
//                proxyClass.addInterface(classPool.get(beanClassName));
//            } else {
//                CtClass beanCtClass = classPool.getCtClass(beanClassName);
//                proxyClass = classPool.makeClass(proxyClassName, beanCtClass);
//            }
//            proxyClass.addInterface(classPool.get(JavaassistProxyBean.class.getName()));
//            List<AsmConstructorInfo> constructorInfoList = getConstructorInfoList();
//            constructorInfoList.sort(AsmConstructorInfo::compareTo);
//
//            AsmConstructorInfo asmConstructorInfo = constructorInfoList.get(0);
//            String constructor = makeConstructor(asmConstructorInfo, proxyClassName.substring(proxyClassName.lastIndexOf(".") + 1));
//            CtConstructor newConstructor = CtNewConstructor.make(constructor, proxyClass);
//            proxyClass.addConstructor(newConstructor);
//
//            String methodProxyHandlerHandlerClassName = MethodProxyHandlerHandler.class.getName();
//
//            proxyClass.addField(CtField.make("private " + beanClassName + " target;", proxyClass));
//            proxyClass.addField(CtField.make("private " + methodProxyHandlerHandlerClassName + " handler;", proxyClass));
//
//            proxyClass.addMethod(CtNewMethod.make("public void setTarget(" + beanClassName + " target) { this.target = target; }", proxyClass));
//            proxyClass.addMethod(CtNewMethod.make("public void setHandler(" + methodProxyHandlerHandlerClassName + " handler) { this.handler = handler; }", proxyClass));
//
//            Set<AsmMethodInfo> proxyMethod = getMethodInfoList();
//            for (AsmMethodInfo method : proxyMethod) {
//                String methodContent = makeMethod(method);
//                LOGGER.trace(() -> methodContent);
//                proxyClass.addMethod(CtNewMethod.make(methodContent, proxyClass));
//            }
//            try {
//                String originPath = beanClassName.replace(".", "/");
//                URL resource = getClassLoader().getResource( originPath + ".class");
//                if (resource != null) {
//                    String path = resource.getFile();
//                    if (OsUtils.isWinOs()) {
//                        path = path.substring(1);
//                    }
//                    int i = path.lastIndexOf(originPath);
//                    path = path.substring(0, i);
//
//                    proxyClass.writeFile(path);
//                }
//            } catch (IOException e) {
//                LOGGER.error("", e);
//            }
//            Class<?> proxyType = proxyClass.toClass();
//
//            Object[] objects = asmConstructorInfo.makeConstructorDefaultValue();
//            @SuppressWarnings("unchecked")
//            B bytecodeProxy = (B) ReflectionHelper.newInstance(proxyType, asmConstructorInfo.getParameterTypes(), objects);
//
//            if (bytecodeProxy != null) {
//                ReflectionHelper.invokeMethod(bytecodeProxy, "setTarget", new Object[]{bean.get()}, new Class<?>[]{beanClass});
//                ReflectionHelper.invokeMethod(bytecodeProxy, "setHandler", new Object[]{getHandler()}, new Class<?>[]{MethodProxyHandlerHandler.class});
//            }
//            return bytecodeProxy;
//        } catch (NotFoundException | CannotCompileException e) {
//            LOGGER.error("", e);
//        }
//        return null;
//    }
//
//    private CtClass[] convert(ClassPool classPool, Type[] classes) {
//        if (classes != null && classes.length > 0 && classes[0] != null) {
//            CtClass[] ctClasses = new CtClass[classes.length];
//            for (int i = 0; i < classes.length; i++) {
//                try {
//                    ctClasses[i] = classPool.get(classes[i].getTypeName());
//                } catch (NotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//            return ctClasses;
//        }
//        return null;
//    }
//
//    private CtClass[] convert(ClassPool classPool, String[] classes) {
//        if (classes != null && classes.length > 0 && classes[0] != null) {
//            CtClass[] ctClasses = new CtClass[classes.length];
//            for (int i = 0; i < classes.length; i++) {
//                try {
//                    ctClasses[i] = classPool.get(classes[i]);
//                } catch (NotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//            return ctClasses;
//        }
//        return null;
//    }
//
//    private String makeConstructor(AsmConstructorInfo constructor, String proxyClassName) {
//        StringBuilder builder = new StringBuilder("    public ").append(proxyClassName).append("(");
//        Class<?>[] parameterTypes = constructor.getParameterTypes();
//        StringBuilder params = new StringBuilder();
//        if (parameterTypes.length > 0 && parameterTypes[0] != null) {
//            int n = parameterTypes.length - 1;
//            for (int i = 0; i < n; i++) {
//                Class<?> parameterType = parameterTypes[i];
//                builder.append(parameterType.getName()).append(" arg").append(i).append(", ");
//                params.append("arg").append(i).append(", ");
//            }
//            Class<?> parameterType = parameterTypes[n];
//            builder.append(parameterType.getName()).append(" arg").append(n);
//            params.append("arg").append(n);
//        }
//        builder.append(")").append(" {\n")
//                .append("        super(").append(params).append(");\n")
//                .append("    }");
//        return builder.toString();
//    }
//
//    private String makeMethod(AsmMethodInfo method) {
//        String methodName = method.getName();
//
//        StringBuilder methodContext = new StringBuilder();
//        if (method.isPublic()) {
//            methodContext.append("    public ");
//        } else if (method.isProtected()) {
//            methodContext.append("    protected ");
//        } else if (!method.isDefault()) {
//            return null;
//        }
//
//        Class<?> returnType = method.getReturnType();
//        String returnTypeName = returnType.getName();
//        if (returnType != void.class) {
//            methodContext.append(returnTypeName).append(" ");
//        } else {
//            methodContext.append("void ");
//        }
//        methodContext.append(methodName).append("(");
//
//        StringBuilder params = new StringBuilder();
//
//        Class<?>[] parameterTypes = method.getParamTypes();
//        StringBuilder paramTypes = new StringBuilder("new Class[] {");
//        if (parameterTypes.length > 0 && parameterTypes[0] != null) {
//            int n = parameterTypes.length - 1;
//            for (int i = 0; i < n; i++) {
//                Class<?> parameterType = parameterTypes[i];
//                methodContext.append(parameterType.getName()).append(" arg").append(i).append(", ");
//                if (TypeHelper.isRawBaseType(parameterType)) {
//                    params.append(setBaseWrapperClassValue(parameterType)).append("arg").append(i).append("), ");
//                } else {
//                    params.append("arg").append(i).append(", ");
//                }
//                paramTypes.append(parameterType.getName()).append(".class, ");
//            }
//            Class<?> parameterType = parameterTypes[n];
//            methodContext.append(parameterType.getName()).append(" arg").append(n);
//            if (TypeHelper.isRawBaseType(parameterType)) {
//                params.append(setBaseWrapperClassValue(parameterType)).append("arg").append(n).append(")");
//            } else {
//                params.append("arg").append(n);
//            }
//            paramTypes.append(parameterType.getName()).append(".class");
//        }
//        paramTypes.append("}");
//        methodContext.append(") ");
//
//        Class<?>[] exceptionTypes = method.getExceptionTypes();
//        if (exceptionTypes.length > 0 && exceptionTypes[0] != null) {
//            methodContext.append("throws ");
//            int n = exceptionTypes.length - 1;
//            for (int i = 0; i < n; i++) {
//                Class<?> type = exceptionTypes[i];
//                methodContext.append(type.getName()).append(", ");
//            }
//            Class<?> type = exceptionTypes[n];
//            methodContext.append(type.getName());
//        }
//        methodContext.append(" {\n");
//        if (isAnnotatedMethod(method)) {
//            String methodCallBackClassName = MethodCallBack.class.getName();
//            Class<?> wrapperClass = returnType;
//            if (TypeHelper.isRawBaseType(returnType)) {
//                wrapperClass = TypeHelper.getWrapperClass(returnType);
//            }
//            if (returnType == void.class) {
//                wrapperClass = Void.class;
//            }
//            String wrapperClassName = wrapperClass.getName();
//
//            methodContext.append("        ")
//                    .append(methodCallBackClassName)
//                    .append(" callBack = new ")
//                    .append(methodCallBackClassName)
//                    .append("(this.target, \"")
//                    .append(methodName).append("\", ")
//                    .append(paramTypes).append(", ")
//                    .append("new Object[] {").append(params).append("}")
//                    .append(");\n");
//            if (returnType == void.class) {
//                methodContext.append("        this.handler.proxy(callBack);\n");
//            } else if (TypeHelper.isRawBaseType(returnType)) {
//                methodContext.append("        return ((")
//                        .append(wrapperClassName)
//                        .append(")this.handler.proxy(callBack))")
//                        .append(getBaseWrapperClassValue(returnType))
//                        .append(";\n");
//            } else {
//                methodContext.append("        return this.handler.proxy(callBack);\n");
//            }
//        } else {
//            if (returnType == void.class) {
//                methodContext.append("        target.")
//                        .append(methodName)
//                        .append("(")
//                        .append(params.toString())
//                        .append(");\n");
//            } else {
//                methodContext.append("        return target.")
//                        .append(methodName)
//                        .append("(")
//                        .append(params.toString())
//                        .append(");\n");
//            }
//        }
//        methodContext.append("    }");
//        return methodContext.toString();
//    }
//
//    private String getBaseWrapperClassValue(Class<?> wrapperClass) {
//        if (wrapperClass == boolean.class) {
//            return ".booleanValue()";
//        }
//        if (wrapperClass == byte.class) {
//            return ".byteValue()";
//        }
//        if (wrapperClass == char.class) {
//            return ".charValue()";
//        }
//        if (wrapperClass == short.class) {
//            return ".shortValue()";
//        }
//        if (wrapperClass == int.class) {
//            return ".intValue()";
//        }
//        if (wrapperClass == long.class) {
//            return ".longValue()";
//        }
//        if (wrapperClass == float.class) {
//            return ".floatValue()";
//        }
//        if (wrapperClass == double.class) {
//            return ".doubleValue()";
//        }
//        return "";
//    }
//
//    private String setBaseWrapperClassValue(Class<?> wrapperClass) {
//        if (wrapperClass == boolean.class) {
//            return "Boolean.valueOf(";
//        }
//        if (wrapperClass == byte.class) {
//            return "Byte.valueOf(";
//        }
//        if (wrapperClass == char.class) {
//            return "Character.valueOf(";
//        }
//        if (wrapperClass == short.class) {
//            return "Short.valueOf(";
//        }
//        if (wrapperClass == int.class) {
//            return "Integer.valueOf(";
//        }
//        if (wrapperClass == long.class) {
//            return "Long.valueOf(";
//        }
//        if (wrapperClass == float.class) {
//            return "Float.valueOf(";
//        }
//        if (wrapperClass == double.class) {
//            return "Double.valueOf(";
//        }
//        return "";
//    }
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(JavassistProxy.class);
//}
