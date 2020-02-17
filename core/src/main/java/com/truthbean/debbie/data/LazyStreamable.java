package com.truthbean.debbie.data;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @since 0.0.2
 */
final class LazyStreamable<T> implements Streamable<T> {
    private final Supplier<? extends Stream<T>> stream;

    @Override
    public Iterator<T> iterator() {
        return this.stream().iterator();
    }

    @Override
    public Stream<T> stream() {
        return this.stream.get();
    }

    private LazyStreamable(Supplier<? extends Stream<T>> stream) {
        this.stream = stream;
    }

    public static <T> LazyStreamable<T> of(Supplier<? extends Stream<T>> stream) {
        return new LazyStreamable<>(stream);
    }

    public Supplier<? extends Stream<T>> getStream() {
        return this.stream;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LazyStreamable<?> that = (LazyStreamable<?>) o;
        return Objects.equals(stream, that.stream);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stream);
    }

    @Override
    public String toString() {
        return "LazyStreamable:{stream:" + this.getStream() + "}";
    }
}