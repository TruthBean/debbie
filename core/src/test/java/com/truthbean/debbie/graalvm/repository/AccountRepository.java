package com.truthbean.debbie.graalvm.repository;

import com.truthbean.debbie.graalvm.model.Account;
import com.truthbean.debbie.jdbc.column.PrimaryKeyType;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.entity.ResultMap;
import com.truthbean.debbie.jdbc.repository.DebbieRepository;

public class AccountRepository extends DebbieRepository<Account, Long> {

    public AccountRepository(EntityResolver entityResolver) {
        super(Long.class, Account.class, entityResolver);

        ResultMap<Account> entityInfo = new ResultMap<>(Account::new);
        entityInfo.setTable("t_account")
                .setJavaType(Account.class)
                .setPrimaryKey("id", Long.class, PrimaryKeyType.AUTO_INCREMENT, Account::getId, Account::setId)
                .addColumn("name", Account::getName, Account::setName)
                .addColumn("password", Account::getPassword, Account::setPassword);
        entityResolver.addEntityInfo(entityInfo);
        entityResolver.addResultMap(entityInfo);
    }
}
