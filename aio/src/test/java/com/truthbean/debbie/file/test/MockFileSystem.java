package com.truthbean.debbie.file.test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class MockFileSystem extends FileSystem {
    private final FileSystemProvider fileSystemProvider;
    private final URI uri;
    private final Map<String, ?> env;
    MockFileSystem(FileSystemProvider fileSystemProvider, URI uri, Map<String, ?> env) {
        this.fileSystemProvider = fileSystemProvider;
        this.uri = uri;
        this.env = env;
    }

    @Override
    public FileSystemProvider provider() {
        return fileSystemProvider;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public String getSeparator() {
        return "/";
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return new ArrayList<>();
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        return new ArrayList<>();
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return new HashSet<>();
    }

    @Override
    public Path getPath(String first, String... more) {
        Objects.requireNonNull(first);
        String path;
        if (more.length == 0) {
            path = first;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(first);
            for (String segment: more) {
                if (!segment.isEmpty()) {
                    if (sb.length() > 0)
                        sb.append(getSeparator());
                    sb.append(segment);
                }
            }
            path = sb.toString();
        }
        return new MockPath(this, path);
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        return path -> Pattern.compile(syntaxAndPattern).matcher(path.toString()).matches();
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        return new MockUserPrincipalLookupService();
    }

    @Override
    public WatchService newWatchService() throws IOException {
        return new MockWatchService();
    }
}
