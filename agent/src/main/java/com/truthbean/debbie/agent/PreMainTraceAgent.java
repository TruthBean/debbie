package com.truthbean.debbie.agent;

import com.truthbean.Logger;
import com.truthbean .LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-11-11 23:13
 */
public class PreMainTraceAgent {
    /**
     * 执行java编程语言代码
     * @param agentArgs 通过命令行传给 Java Agent 的参数
     * @param inst Java Class 字节码转换的工具
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        LOGGER.trace("agentArgs : " + agentArgs);
        inst.addTransformer(new DefineTransformer(), true);
    }

    static class DefineTransformer implements ClassFileTransformer {

        @Override
        public byte[] transform(Module module, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            LOGGER.trace("module ... premain load Class:" + className);
            if (className.startsWith("com/truthbean/debbie/agent")) {
                System.out.println("debbie agent module ... ");
            }
            return classfileBuffer;
        }

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
                throws IllegalClassFormatException {
            LOGGER.trace("premain load Class:" + className);
            if (className.startsWith("com/truthbean/debbie/agent")) {
                System.out.println("debbie agent module ... ");
            }
            return classfileBuffer;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PreMainTraceAgent.class);
}