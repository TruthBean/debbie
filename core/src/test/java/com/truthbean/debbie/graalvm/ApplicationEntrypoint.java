package com.truthbean.debbie.graalvm;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.graalvm.model.Account;
import com.truthbean.debbie.graalvm.service.AccountService;
import com.truthbean.debbie.graalvm.service.LogService;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.UUID;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/25 17:52.
 */
public class ApplicationEntrypoint implements ApplicationContextAware {

    private AccountService accountService;
    private LogService logService;
    private DataSourceFactory factory;

    public ApplicationEntrypoint() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.accountService = applicationContext.factory(AccountService.class);
        this.logService = applicationContext.factory(LogService.class);
        this.factory = applicationContext.factory(DataSourceFactory.class);
    }

    public void printId() {
        TransactionInfo transaction = this.factory.getTransaction();
        TransactionManager.offer(transaction);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 10; i++) {
            Account account = new Account();
            account.setName("user_" + i + "_" + random.nextInt(999));
            account.setPassword(UUID.randomUUID().toString());
            accountService.save(account);
            logService.log(account.toString());
            Account query = accountService.query(account.getId());
            if (query != null) {
                query.setName(UUID.randomUUID().toString());
                accountService.update(query);
                logService.log(query.toString());
            }
        }

        Collection<Account> accounts = accountService.listAll();
        if (accounts != null) {
            for (Account a : accounts) {
                logService.log(a.toString());
            }
        }
        transaction.commit();
    }
}
