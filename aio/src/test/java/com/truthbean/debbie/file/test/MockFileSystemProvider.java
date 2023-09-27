package com.truthbean.debbie.file.test;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class MockFileSystemProvider extends FileSystemProvider {
    private final ConcurrentMap<String, MockFileSystem> fileSystems;
    public MockFileSystemProvider() {
        fileSystems = new ConcurrentHashMap<>();
    }

    @Override
    public String getScheme() {
        return "mock";
    }

    private void checkUri(URI uri) {
        if (!uri.getScheme().equalsIgnoreCase(getScheme()))
            throw new IllegalArgumentException("URI does not match this provider");
        if (uri.getRawAuthority() != null)
            throw new IllegalArgumentException("Authority component present");
        String path = uri.getPath();
        if (path == null)
            throw new IllegalArgumentException("Path component is undefined");
        if (!path.startsWith("/"))
            throw new IllegalArgumentException("Path component should be '/'");
        if (uri.getRawQuery() != null)
            throw new IllegalArgumentException("Query component present");
        if (uri.getRawFragment() != null)
            throw new IllegalArgumentException("Fragment component present");
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        checkUri(uri);
        String path = uri.getPath();
        if (fileSystems.containsKey(path)) {
            throw new FileSystemAlreadyExistsException();
        }
        MockFileSystem fileSystem = new MockFileSystem(this, uri, env);
        fileSystems.put(path, fileSystem);
        return fileSystem;
    }

    @Override
    public FileSystem getFileSystem(URI uri) {
        if (fileSystems.containsKey(uri.getPath())) {
            new FileSystemNotFoundException();
        }
        return fileSystems.get(uri.getPath());
    }

    @Override
    public Path getPath(URI uri) {
        return getFileSystem(uri).getPath(uri.getPath());
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return new MockSeekableByteChannel();
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return null;
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {

    }

    @Override
    public void delete(Path path) throws IOException {

    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {

    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {

    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        return false;
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        return false;
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        return null;
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {

    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        return null;
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        return null;
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return null;
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {

    }
}
