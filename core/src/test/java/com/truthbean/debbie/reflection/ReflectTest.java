package com.truthbean.debbie.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * @author truthbean/Rogar·Q
 */
public class ReflectTest extends TestA {
  public static void main(String[] args) throws Throwable {
    MethodHandler<ReflectTest, Void> method = MethodHandler.getMethodHandle(ReflectTest.class, void.class, "hello");
    method.invokeExact(new ReflectTest());
    method = MethodHandler.getMethodHandle(ReflectTest.class, void.class, "hello", String.class);
    method.invokeExact(new ReflectTest(), "hello");
    MethodHandler<ReflectTest, Integer> methodHandler = MethodHandler.getMethodHandle(ReflectTest.class, int.class, "hello", int.class);
    int hello = (int) methodHandler.getMethodHandle().invokeExact(new ReflectTest(), 123);
    System.out.println(hello);
    MethodHandler<ReflectTest, String> method2 = MethodHandler.getMethodHandle(ReflectTest.class, String.class, "toString");
    String toStr = (String) method2.invokeExact(new ReflectTest());
    System.out.println(toStr);
  }

  public void hello() {
    System.out.println("hello");
  }

  public void hello(String hello) {
    System.out.println(hello);
  }




}

class TestA {
  public int hello(int hello) {
    return hello;
  }
}
