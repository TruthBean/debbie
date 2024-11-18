package com.truthbean.debbie.environment;

import com.truthbean.debbie.bean.Aware;

/**
 * @author TruthBean
 * @since 0.5.5
 * Created on 2022/04/02 11:45.
 */
public interface EnvironmentHolderAware extends Aware {

    void setEnvironmentHolder(EnvironmentDepositoryHolder environmentDepositoryHolder);
}
