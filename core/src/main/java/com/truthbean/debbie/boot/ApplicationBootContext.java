package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.task.TaskInfo;

import java.util.List;

/**
 * Extended {@link ApplicationContext} exposed to application boot
 * callbacks ({@code then}, {@code afterStarted}, etc.), allowing
 * registration of beans, lifecycles, and tasks as well as bean
 * retrieval and type transformation.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public interface ApplicationBootContext extends ApplicationContext {

    /**
     * Registers a bean via its {@link BeanInfo}.
     *
     * @param beanFactory the bean info describing the bean
     * @param <T>         the bean type
     * @param <F>         the bean info subtype
     */
    <T, F extends BeanInfo<T>> void registerBean(F beanFactory);

    /**
     * Registers a singleton bean instance under the given interface
     * class and optional names.
     *
     * @param beanClass the interface or super type of the bean
     * @param bean      the singleton instance
     * @param names     optional additional bean names
     * @param <T>       the concrete bean type
     * @param <I>       the interface type
     */
    <T extends I, I> void registerSingleBean(Class<I> beanClass, T bean, String... names);

    /** Registers a bean lifecycle listener. */
    void registerBeanLifecycle(BeanLifecycle beanLifecycle);

    /**
     * Registers a task for scheduling.
     *
     * @param taskInfo the task descriptor
     */
    void registerTask(TaskInfo taskInfo);

    /**
     * Transforms the given object to the target type.
     *
     * @param origin the source object
     * @param target the target type
     * @param <O>    the source type
     * @param <T>    the target type
     * @return the transformed value
     */
    <O, T> T transform(final O origin, final Class<T> target);

    /**
     * Retrieves a bean instance by name.
     *
     * @param beanName the bean name
     * @param <T>      the bean type
     * @return the bean instance
     */
    <T> T factory(String beanName);

    /**
     * Retrieves a bean instance by type.
     *
     * @param beanType the bean type
     * @param <T>      the bean type
     * @return the bean instance
     */
    <T> T factory(Class<T> beanType);

    /**
     * Retrieves all bean instances of the given type.
     *
     * @param beanType the bean type
     * @param <T>      the bean type
     * @return a list of matching bean instances
     */
    <T> List<T> factories(Class<T> beanType);

    /**
     * Retrieves a configuration bean for the given profile and category.
     *
     * @param profile  the configuration profile
     * @param category the configuration category
     * @param beanType the bean type
     * @param <T>      the bean type
     * @return the configuration bean instance
     */
    <T> T factoryConfiguration(String profile, String category, Class<T> beanType);

    /**
     * Resolves a bean via the given injection descriptor.
     *
     * @param injection the injection descriptor
     * @param <T>       the bean type
     * @return the resolved bean instance
     */
    <T> T factory(BeanInjection<T> injection);
}
