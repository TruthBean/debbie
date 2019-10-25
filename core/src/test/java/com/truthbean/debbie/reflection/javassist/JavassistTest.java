package com.truthbean.debbie.reflection.javassist;

import com.truthbean.debbie.reflection.ReflectionHelper;
import javassist.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class JavassistTest {

    @Test
    public void test() {
        try {
            ClassPool pool = ClassPool.getDefault();
            // 创建Programmer类
            CtClass cc = pool.makeClass("com.truthbean.debbie.reflection.javassist.Programmer");
            // 定义code方法
            CtMethod method = CtNewMethod.make("public void code(){}", cc);
            // 插入方法代码
            method.insertBefore("System.out.println(\"I'm a Programmer,Just Coding.....\");");
            cc.addMethod(method);

            Loader.Simple simple = new Loader.Simple();
            Class<?> aClass = simple.invokeDefineClass(cc);
            Object o = ReflectionHelper.newInstance(aClass);
            System.out.println(o.getClass());
            ReflectionHelper.invokeMethod(o, "code");
        } catch (CannotCompileException | IOException e) {
            e.printStackTrace();
        }
    }
}
