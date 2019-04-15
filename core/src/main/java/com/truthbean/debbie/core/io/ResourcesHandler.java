package com.truthbean.debbie.core.io;

import com.truthbean.debbie.core.util.Constants;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-18 19:52
 */
public final class ResourcesHandler {
    private ResourcesHandler() {
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private static InputStream handle(String resource) {

        String tempResource = resource;
        if (resource.contains(Constants.CLASSPATH)) {
            tempResource = resource.substring(10);
        }
        if (resource.contains(Constants.CLASSPATHS)) {
            tempResource = resource.substring(11);
        }
        InputStream in;
        in = Thread.currentThread().getContextClassLoader().getResourceAsStream(tempResource);
        if (in == null) {
            in = ResourcesHandler.class.getResourceAsStream(tempResource);
        }

        return in;
    }

    public static String handleStaticResource(String resource) {
        try {
            InputStream in = handle(resource);

            InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            StringWriter writer = new StringWriter();

            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int n;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }

            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] handleStaticBytesResource(String resource) {
        InputStream inputStream = handle(resource);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            copy(inputStream, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toByteArray();
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        final int eof = -1;

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        long count = 0;
        int n;
        while (eof != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

}
