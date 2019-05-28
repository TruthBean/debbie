package com.truthbean.debbie.core.reflection;

import com.truthbean.debbie.core.data.validate.DataValidateFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public interface ExecutableArgumentResolver {

    /**
     * Whether the given {@linkplain ExecutableArgument method parameter} is
     * supported by this resolver.
     * @param parameter the method parameter to check
     * @return {@code true} if this resolver supports the supplied parameter;
     * {@code false} otherwise
     */
    boolean supportsParameter(ExecutableArgument parameter);

    boolean resolveArgument(ExecutableArgument parameter, Object originValues, DataValidateFactory validateFactory);
}
