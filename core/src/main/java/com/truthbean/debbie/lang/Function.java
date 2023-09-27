package com.truthbean.debbie.lang;

/**
 * @author TruthBean
 * @since 0.5.5
 * Created on 2022/04/12 15:29.
 */
@FunctionalInterface
public interface Function {

    Object func(Function... funcs);
}
