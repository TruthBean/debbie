package com.truthbean.debbie.io;

import com.truthbean.debbie.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 15:03.
 */
public final class StreamHelper {
    private StreamHelper() {
    }

    public static BufferedReader toBufferedReader(byte[] bytes) {
        InputStream inputStream = new ByteArrayInputStream(bytes);
        return toBufferedReader(inputStream);
    }

    public static BufferedReader toBufferedReader(InputStream inputStream) {
        var inputStreamReader = new InputStreamReader(inputStream);
        return new BufferedReader(inputStreamReader);
    }

    public static List<String> readFile(File file) {
        List<String> result = new ArrayList<>();
        try {
            if (file.exists() && file.isFile() && file.canRead()) {
                FileReader reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (!line.isBlank()) {
                        result.add(line.trim());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> readFileInJar(URL url) {
        List<String> result = new ArrayList<>();
        try {
            InputStream inputStream = url.openConnection().getInputStream();
            BufferedReader bufferedReader = toBufferedReader(inputStream);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.isBlank()) {
                    result.add(line.trim());
                }
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return result;
    }

    private static List<JarEntry> getFilesInJar(URL url) {
        List<JarEntry> filesInJar = new ArrayList<>();
        try {
            // 获取jar
            JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
            // 从此jar包 得到一个枚举类
            var entries = jar.entries();
            // 同样的进行循环迭代
            while (entries.hasMoreElements()) {
                // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                var entry = entries.nextElement();
                filesInJar.add(entry);
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return filesInJar;
    }

    private static void readFileInJar(String innerPath, String filePartPath, ClassLoader classLoader,
                                      List<String> result) throws IOException {
        if (innerPath.endsWith(filePartPath)) {
            InputStream inputStream = classLoader.getResourceAsStream(innerPath);
            assert inputStream != null;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.isBlank()) {
                    result.add(line.trim());
                }
            }
        }
    }

    public static List<Class<?>> getClassFromJarByPackageName(String packageName, URL url, String packageDirName,
                                                              ClassLoader classLoader) {
        List<Class<?>> result = new ArrayList<>();
        List<JarEntry> filesInJar = getFilesInJar(url);
        for (JarEntry entry : filesInJar) {
            getClassesUnderPackageInJar(packageName, entry, packageDirName, classLoader, result);
        }
        return result;
    }

    private static void getClassesUnderPackageInJar(String packageName, JarEntry entry, String packageDirName,
                                                              ClassLoader classLoader, List<Class<?>> classes) {
        var name = entry.getName();
        // 如果是以/开头的
        if (name.charAt(0) == '/') {
            // 获取后面的字符串
            name = name.substring(1);
        }
        // 如果前半部分和定义的包名相同
        if (name.startsWith(packageDirName)) {
            int idx = name.lastIndexOf('/');
            // 如果以"/"结尾 是一个包
            if (idx != -1) {
                // 获取包名 把"/"替换成"."
                packageName = name.substring(0, idx).replace('/', '.');
            }
            // 如果可以迭代下去 并且是一个包
            // 如果是一个.class文件 而且不是目录
            if (name.endsWith(".class") && !entry.isDirectory()) {
                // 去掉后面的".class" 获取真正的类名
                var className = name.substring(packageName.length() + 1, name.length() - 6);
                try {
                    // 添加到classes
                    classes.add(classLoader.loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

    /**
     * The default buffer size ({@value}) to use for
     * {@link #copyLarge(InputStream, OutputStream)}
     * and
     * {@link #copyLarge(Reader, Writer)}
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * Get the contents of an <code>InputStream</code> as a <code>byte[]</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input  the <code>InputStream</code> to read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     */
    public static byte[] toByteArray(InputStream input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            copy(input, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toByteArray();
    }

    // copy from InputStream
    //-----------------------------------------------------------------------

    /**
     * Copy bytes from an <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p>
     * Large streams (over 2GB) will return a bytes copied value of
     * <code>-1</code> after the copy has completed since the correct
     * number of bytes cannot be returned as an int. For large streams
     * use the <code>copyLarge(InputStream, OutputStream)</code> method.
     *
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @return the number of bytes copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 1.1
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 1.3
     */
    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p>
     *
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @param buffer the buffer to use for the copy
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException {
        long count = 0;
        int n = 0;
        while (Constants.EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Copy chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
     * <p>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p>
     *
     * @param input  the <code>Reader</code> to read from
     * @param output  the <code>Writer</code> to write to
     * @param buffer the buffer to be used for the copy
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(Reader input, Writer output, char[] buffer) throws IOException {
        long count = 0;
        int n = 0;
        while (Constants.EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Copy chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @param input  the <code>Reader</code> to read from
     * @param output  the <code>Writer</code> to write to
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 1.3
     */
    public static long copyLarge(Reader input, Writer output) throws IOException {
        return copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
    }

    public static String getAndClose(InputStream stream) throws IOException {
        String result;
        try {
            result = streamToString(stream);
        } finally {
            if (stream != null) {
                close(stream);
            }
        }

        return result;
    }

    public static String streamToString(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        } else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder content = new StringBuilder();

            String newLine;
            do {
                newLine = reader.readLine();
                if (newLine != null) {
                    content.append(newLine).append('\n');
                }
            } while (newLine != null);

            if (content.length() > 0) {
                content.setLength(content.length() - 1);
            }

            return content.toString();
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                LOGGER.error("IOException closing stream", e);
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamHelper.class);
}
