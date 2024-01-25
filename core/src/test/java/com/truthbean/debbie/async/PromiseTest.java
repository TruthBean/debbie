package com.truthbean.debbie.async;

import com.truthbean.Console;
import com.truthbean.debbie.lang.Promise;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.5.5
 * Created on 2022/04/12 15:27.
 */
public class PromiseTest {
    public static void main(String[] args) {
        var promise = new Promise((resolver, rejecter) -> {
            resolver.resolve((Supplier<String>) () -> "123");
            rejecter.reject("reject err");
        });
        promise.then((o) -> {
                    Console.info(((Supplier<String>)o).get());
                })
                .then(v -> {
                    Console.info(v);
                    return "resolve 1";
                })
                .then((v, e) -> {
                    Console.info(v);
                    Console.error(e);
                })
                .then((val, err) -> val + "2")
                .then(then -> {
                    var value = then + "3";
                    Console.info(value);
                    return value;
                })
                .catchError(err -> {
                    if (err != null) {
                        Console.error(err);
                    }
                });

        Promise.all(val -> "1", val -> val + "2", val -> val + "3")
                .then(val -> {
                    Console.info(val.toString());
                })
                .catchError(err -> {
                    if (err != null) {
                        Console.error(err);
                    }
                });
    }
}
