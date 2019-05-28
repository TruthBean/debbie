package com.truthbean.debbie.core.reflection;

import com.truthbean.debbie.core.spi.SpiLoader;

import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class ExecutableArgumentResolverFactory {

    public ExecutableArgumentResolver factory(ExecutableArgument parameter) {
        Set<ExecutableArgumentResolver> resolvers = SpiLoader.loadProviders(ExecutableArgumentResolver.class);
        for (ExecutableArgumentResolver resolver : resolvers) {
            if (resolver.supportsParameter(parameter)) {
                return resolver;
            }
        }
        return null;
    }
}
