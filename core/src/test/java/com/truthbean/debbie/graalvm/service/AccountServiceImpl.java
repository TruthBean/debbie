package com.truthbean.debbie.graalvm.service;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.graalvm.model.Account;
import com.truthbean.debbie.graalvm.repository.AccountRepository;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.proxy.MethodProxy;

import java.util.Collection;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/25 17:04.
 */
public class AccountServiceImpl implements AccountService, ApplicationContextAware, AutoCloseable {

    private AccountRepository accountRepository;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        accountRepository = applicationContext.getGlobalBeanFactory().factory(AccountRepository.class);
    }

    @Override
    @JdbcTransactional
    public boolean save(Account account) {
        Account name = accountRepository.findByColumn("name", account.getName());
        if (name != null) {
            accountRepository.update(account, true);
        } else {
            accountRepository.save(account);
        }
        return true;
    }

    @Override
    @JdbcTransactional
    public boolean update(Account account) {
        accountRepository.update(account, false);
        return true;
    }

    @Override
    @JdbcTransactional
    public boolean delete(Long id) {
        accountRepository.deleteById(id);
        return false;
    }

    @Override
    @JdbcTransactional
    public Account query(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    @MethodProxy
    @JdbcTransactional
    public Collection<Account> listAll() {
        return accountRepository.findAll();
    }

    @Override
    public void close() throws Exception {
    }
}
