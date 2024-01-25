package com.truthbean.debbie.lang;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author TruthBean
 * @since 0.5.5
 * Created on 2022/04/12 15:22.
 */
public class Promise {

    private final AtomicReference<Object> value = new AtomicReference<>();
    private volatile Throwable err;

    public Promise() {
    }

    public static Promise fall() {
        return new Promise();
    }

    public Promise(ResolveAndReject func) {
        try {
            func.func(value::lazySet, err -> this.err = err);
        } catch (Throwable e) {
            this.err = e;
        }
    }

    @SafeVarargs
    public static Promise all(Function<Object, Object>... funcs) {
        Promise promise = new Promise();
        try {
            if (funcs != null) {
                for (Function<Object, Object> func : funcs) {
                    if (func != null) {
                        promise.then(func);
                    }
                }
            }
        } catch (Exception e) {
            promise.err = e;
        }
        return promise;
    }

    public Promise then(Function<Object, Object> func) {
        try {
            value.setPlain(func.apply(value.getPlain()));
        } catch (Throwable e) {
            err = e;
        }
        return this;
    }

    public Promise then(Consumer<Object> func) {
        try {
            func.accept(value.getAcquire());
            value.setRelease(null);
        } catch (Throwable e) {
            err = e;
        }
        return this;
    }

    public Promise then(ValueAndError func) {
        try {
            value.setOpaque(func.func(value.getOpaque()));
        } catch (Exception e) {
            err = e;
        }
        return this;
    }

    public Promise then(ValueAndErrorConsumer func) {
        try {
            func.func(value.get(), err);
            value.set(null);
            err = null;
        } catch (Exception e) {
            err = e;
        }
        return this;
    }

    public void catchError(Consumer<Throwable> err) {
        err.accept(this.err);
        this.err = null;
    }

    @FunctionalInterface
    public interface ValueAndError {
        Object func(Object val, Throwable err);

        default Object func(Object val) {
            return func(val, null);
        }
    }

    @FunctionalInterface
    public interface ValueAndErrorConsumer {
        void func(Object val, Throwable err);
    }

    @FunctionalInterface
    public interface ResolveAndReject {
        void func(Resolver resolve, Rejecter reject);
    }

    @FunctionalInterface
    public interface Resolver {
        void resolve(Object param);
    }

    @FunctionalInterface
    public interface Rejecter {
        void reject(Throwable err);

        default void reject(String msg) {
            reject(new RuntimeException(msg));
        }
    }
}
