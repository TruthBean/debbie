package com.truthbean.debbie.bean;

import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/04 12:19.
 */
public interface BeanInfo<Bean> {

    /**
     * 不用Class&lt;Bean&gt;或者Class&lt;? extends Bean&gt;的原因是，考虑到泛型问题
     *
     * @return Class&lt;? extends Bean&gt;
     */
    Class<?> getBeanClass();

    default BeanProxyType getBeanProxyType() {
        return BeanProxyType.JDK;
    }

    default boolean needProxy() {
        return getBeanProxyType() != BeanProxyType.NO;
    }

    default Set<BeanCondition> getConditions() {
        Set<BeanCondition> conditions = new HashSet<>();
        conditions.add(DefaultBeanCondition.INSTANCE);
        return conditions;
    }

    default boolean isLazyCreate() {
        return true;
    }

    default BeanType getBeanType() {
        return BeanType.SINGLETON;
    }

    default boolean isSingleton() {
        var beanType = getBeanType();
        return beanType == BeanType.SINGLETON;
    }

    default String getName() {
        Set<String> beanNames = getAllName();
        String name = beanNames == null || beanNames.isEmpty() ? null : beanNames.iterator().next();
        if (name == null || name.isBlank()) {
            name = getBeanClass().getSimpleName();
            name = StringUtils.toFirstCharLowerCase(name);
        }
        return name;
    }

    Set<String> getAllName();

    default boolean containName(String name) {
        Set<String> beanNames = getAllName();
        if (beanNames.isEmpty()) {
            setDefaultName();
        }
        return beanNames.contains(name);
    }

    default String containOneName(Collection<String> names) {
        Set<String> beanNames = getAllName();
        if (beanNames.isEmpty()) {
            setDefaultName();
        }
        for (String beanName : beanNames) {
            for (String name : names) {
                if (beanName.equals(name)) {
                    return name;
                }
            }
        }
        return null;
    }

    default boolean containAllName(Collection<String> names) {
        Set<String> beanNames = getAllName();
        if (beanNames.isEmpty()) {
            setDefaultName();
        }
        boolean result = false;
        for (String name : names) {
            result = false;
            for (String beanName : beanNames) {
                if (name.equals(beanName)) {
                    result = true;
                    break;
                }
            }
            if (!result) {
                return false;
            }
        }
        return result;
    }

    default void setDefaultName() {
        Set<String> beanNames = getAllName();
        if (beanNames.isEmpty()) {
            String name = getBeanClass().getSimpleName();
            name = StringUtils.toFirstCharLowerCase(name);
            beanNames.add(name);
            beanNames.add(getBeanClass().getName());
        }
    }

    default String profile() {
        return "default";
    }

    BeanInfo<Bean> copy();

    default boolean isEquals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BeanInfo<?> beanInfo)) {
            return false;
        }
        // if (!super.equals(o)) return false;
        Set<String> beanNames = getAllName();
        Set<String> oBeanNames = beanInfo.getAllName();
        boolean beanNameEmpty = beanNames == null || beanNames.isEmpty() || oBeanNames == null || oBeanNames.isEmpty();
        if (beanNameEmpty) {
            return true;
        }
        if (beanNames.size() == oBeanNames.size()) {
            boolean[] equals = new boolean[beanNames.size()];
            int i = 0;
            for (String s1 : beanNames) {
                for (String s2 : oBeanNames) {
                    if (s1.equals(s2)) {
                        equals[i] = true;
                        break;
                    }
                }
                i++;
            }
            for (boolean equal : equals) {
                if (!equal) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    default int getHashCode(int superHashCode) {
        Set<String> beanNames = getAllName();
        if (beanNames.isEmpty()) {
            return Objects.hash(superHashCode, beanNames);
        }
        // 重新计算hashcode
        int h = 0;
        for (String obj : beanNames) {
            if (obj != null) {
                h += obj.hashCode();
            }
        }
        return h;
    }
}
