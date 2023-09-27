package com.truthbean.debbie.lang;

import java.util.concurrent.atomic.AtomicReference;

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
        func.func(value::set, err -> this.err = err);
    }

    public static Promise all(Value... funcs) {
        Promise promise = new Promise();
        if (funcs != null && funcs.length > 0) {
            for (Value func : funcs) {
                if (func != null) {
                    promise.then(func);
                }
            }
        }
        return promise;
    }

    public Promise then(Value func) {
        try {
            value.set(func.func(value.get()));
        } catch (Throwable e) {
            err = e;
        }
        return this;
    }

    public Promise then(FinalValue func) {
        try {
            func.func(value.get());
        } catch (Throwable e) {
            err = e;
        }
        return this;
    }

    public Promise then(ValueAndError func) {
        try {
            value.set(func.func(value.get()));
        } catch (Exception e) {
            err = e;
        }
        return this;
    }

    public Promise then(Object resolve) {
        try {
            value.set(resolve);
        } catch (Exception e) {
            err = e;
        }
        return this;
    }

    public void catchError(Error err) {
        err.func(this.err);
    }

    @FunctionalInterface
    public interface ValueAndError {
        Object func(Object val, Throwable err);

        default Object func(Object val) {
            return func(val, null);
        }
    }

    @FunctionalInterface
    public interface Value {
        Object func(Object val);
    }

    @FunctionalInterface
    public interface FinalValue {
        void func(Object val);
    }

    @FunctionalInterface
    public interface Error {
        void func(Throwable err);
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
