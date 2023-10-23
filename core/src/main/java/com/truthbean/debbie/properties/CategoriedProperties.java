package com.truthbean.debbie.properties;

import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;

import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public abstract class CategoriedProperties<C extends DebbieConfiguration> extends DebbieEnvironmentDepositoryHolder
        implements DebbieProperties<C> {

    public CategoriedProperties() {
        setDefaultProfile(DEFAULT_PROFILE);
    }

    public abstract String getKeyPrefix();

    protected Set<String> getRawCategories() {
        Set<String> set = new HashSet<>();
        String[] arr = getStringArray(getValue(getKeyPrefix() + CATEGORIES_KEY_NAME), ",");
        if (arr == null || arr.length == 0) {
            arr = getStringArray(getValue(getKeyPrefix() + CATEGORIES_KEY_NAME), ";");
        }
        if (arr != null) {
            set.addAll(Set.of(arr));
        }
        return set;
    }
}
