package com.truthbean.code.debbie.core.data.transformer;

import com.truthbean.code.debbie.core.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ServiceLoader;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataTransformerFactory {

    public <O, T> T transform(O origin, Class<T> target) {
        Type originType = origin.getClass();

        if (originType == target) {
            return target.cast(origin);
        }

        ServiceLoader<DataTransformer> serviceLoader = ServiceLoader.load(DataTransformer.class);
        for (var transformer: serviceLoader) {
            LOGGER.debug(transformer.getClass().getName());
            Type[] argsType = ReflectionHelper.getActualTypes(transformer.getClass());
            if (argsType != null && argsType.length == 2) {
                if (argsType[0] == originType && argsType[1] == target) {
                    return target.cast(transformer.transform(origin));
                }
                if (argsType[1] == originType && argsType[0] == target) {
                    return target.cast(transformer.reverse(origin));
                }
            }
        }
        var error = originType.getTypeName() + " with " + target.getName() + " has no transformer";
        LOGGER.warn("", new NoDataTransformerMatchedException(error));
        return target.cast(origin);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTransformerFactory.class);
}
