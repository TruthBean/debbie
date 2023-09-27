package com.truthbean.debbie.bean;

import com.truthbean.core.util.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public interface MultiNameBeanInfo<Bean> extends BeanInfo<Bean> {

    default String getName() {
        Set<String> beanNames = getBeanNames();
        String name = beanNames.isEmpty() ? null : beanNames.iterator().next();
        if (name == null || name.isBlank()) {
            name = getBeanClass().getSimpleName();
            name = StringUtils.toFirstCharLowerCase(name);
            beanNames.add(name);
            beanNames.add(getBeanClass().getName());
        }
        return name;
    }

    default boolean containName(String name) {
        Set<String> beanNames = getBeanNames();
        if (beanNames.isEmpty()) {
            setDefaultName();
        }
        return beanNames.contains(name);
    }

    default String containOneName(Collection<String> names) {
        Set<String> beanNames = getBeanNames();
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
        Set<String> beanNames = getBeanNames();
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
        Set<String> beanNames = getBeanNames();
        if (beanNames.isEmpty()) {
            String name = getBeanClass().getSimpleName();
            name = StringUtils.toFirstCharLowerCase(name);
            beanNames.add(name);
            beanNames.add(getBeanClass().getName());
        }
    }

    default Set<String> getBeanNames() {
        return new HashSet<>();
    }

    BeanInfo<Bean> copy();

    default boolean isEquals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultiNameBeanInfo<?> beanInfo)) {
            return false;
        }
        // if (!super.equals(o)) return false;
        Set<String> beanNames = getBeanNames();
        Set<String> oBeanNames = beanInfo.getBeanNames();
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
        Set<String> beanNames = getBeanNames();
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
