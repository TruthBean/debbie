/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.asm;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.reflection.ByteArrayClassLoader;
import com.truthbean.common.mini.util.OsUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-26 17:03
 */
public class AsmClassCreator {

    public String randomClassName() {
        StringBuilder className = new StringBuilder();
        SecureRandom random = new SecureRandom();
        // 首字母
        int i = random.nextInt(23);
        char first = (char)('A' + i);
        className.append(first);

        // 长度
        int length = random.nextInt(8);

        for (int j = 0; j < length; j++) {
            // 大小写字母，数字，下划线，美元符
            int next = random.nextInt(23 + 23 + 10 + 1 + 1);
            if (next < 23) {
                className.append((char) ('A' + next));
            } else if (next < 45) {
                className.append((char) ('a' + next - 23));
            } else if (next < 55) {
                className.append((char) ('0' + next - 45));
            } else if (next == 56) {
                className.append('_');
            } else if (next == 57) {
                className.append('$');
            }
        }
        return className.toString();
    }

    public Class<?> createClass(String name, String packageName, ClassLoader classLoader, String originClassName, boolean save) {
        String className = packageName + "." + name;
        try {
            return Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
        }
        String classPath = className.replace('.', '/');
        String signature = "L" + classPath + ";";
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classWriter.visit(Opcodes.V11, Opcodes.ACC_PUBLIC, classPath, signature, "java/lang/Object", null);
        classWriter.visitAnnotation(Type.getDescriptor(BeanComponent.class), true);

        MethodVisitor initVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        // Code:
        initVisitor.visitCode();
        // 0: aload_0
        initVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        // 1: invokespecial #1                  // Method java/lang/Object."<init>":()V
        initVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        // 4: return
        initVisitor.visitInsn(Opcodes.RETURN);
        // Maxs computed by ClassWriter.COMPUTE_MAXS, these arguments ignored
        initVisitor.visitMaxs(-1, -1);
        initVisitor.visitEnd();

        classWriter.visitEnd();
        byte[] code = classWriter.toByteArray();
        Class<?> proxyClass = (Class<?>) new ByteArrayClassLoader(classLoader).defineClass(className, code);

        if (save)
            try {
                byte[] data = classWriter.toByteArray();
                String originPath = originClassName.replace(".", "/");
                URL resource = classLoader.getResource(originPath + ".class");
                if (resource != null) {
                    String path = resource.getFile();
                    if (OsUtils.isWinOs()) {
                        path = path.substring(1);
                    }
                    int i = path.lastIndexOf(originClassName.replace(".", "/"));
                    path = path.substring(0, i);

                    File file = new File(path, classPath + ".class");
                    if (!file.exists())
                        try (FileOutputStream out = new FileOutputStream(file)) {
                            out.write(data);
                        }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        return proxyClass;
    }
}
