package com.truthbean.debbie.graalvm.service;

import com.truthbean.debbie.graalvm.model.Account;

import java.util.Collection;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/25 17:04.
 */
public interface AccountService {

    boolean save(Account account);

    boolean update(Account account);

    boolean delete(Long id);

    Account query(Long id);

    Collection<Account> listAll();
}
