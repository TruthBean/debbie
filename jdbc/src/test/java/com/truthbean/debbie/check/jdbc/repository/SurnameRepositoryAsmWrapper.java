package com.truthbean.debbie.check.jdbc.repository;

import com.truthbean.debbie.check.jdbc.entity.Surname;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.proxy.MethodCallBack;
import com.truthbean.debbie.proxy.MethodProxyHandlerHandler;
import com.truthbean.debbie.proxy.asm.AsmGenerated;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

@AsmGenerated
public class SurnameRepositoryAsmWrapper extends SurnameRepository {
    private SurnameRepository target;
    private MethodProxyHandlerHandler handler;

    public SurnameRepositoryAsmWrapper() {
        super();
    }

    public void setTarget(SurnameRepository target) {
        this.target = target;
    }

    public void setHandler(MethodProxyHandlerHandler var1) {
        this.handler = var1;
    }

    public List<Surname> saveAndDelete(Surname var1, Long var2) {
        return this.target.saveAndDelete(var1, var2);
    }

    public boolean save(Surname var1) {
        return this.target.save(var1);
    }

    public boolean delete(Long var1) {
        return this.target.delete(var1);
    }

    public boolean update(Surname var1) {
        return this.target.update(var1);
    }

    public Boolean exists(Long var1) {
        return this.target.exists(var1);
    }

    public Page<Surname> findPaged(PageRequest var1) {
        return this.target.findPaged(var1);
    }

    public Future<List<Surname>> findAll() {
        return this.target.findAll();
    }

    public Long count() {
        MethodCallBack<Long> var1 = new MethodCallBack<>(this.target, "count");
        return this.handler.proxy(var1);
    }

    public Optional<Surname> findById(Long var1) {
        return this.target.findById(var1);
    }
}