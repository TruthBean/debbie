package com.truthbean.debbie.graalvm.table;

import com.truthbean.debbie.graalvm.model.Account;
import com.truthbean.debbie.jdbc.entity.EntityInfo;

public class AccountTableInfo {

    public Class<?> getEntityClass() {
        return Account.class;
    }

    public void addColumn(EntityInfo<?> entityInfo) {

    }
}
