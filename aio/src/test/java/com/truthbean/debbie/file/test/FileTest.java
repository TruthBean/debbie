package com.truthbean.debbie.file.test;

import java.net.URI;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class FileTest {
    public static void main(String[] args) {
        String rootPath = "mock:///";
        FileSystem fileSystem = FileSystems.getFileSystem(URI.create(rootPath));
        Iterable<FileStore> fileStores = fileSystem.getFileStores();
        for (FileStore fileStore : fileStores) {
            System.out.println(fileStore);
        }
        Path path = fileSystem.getPath("/");
        System.out.println(path);
    }
}
