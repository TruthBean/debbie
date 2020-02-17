package com.truthbean.debbie.jdbc.transaction;

public class Print2ResourceHolder implements ResourceHolder {
    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public void prepare() {
        System.out.println("0. prepare2.....");
    }

    @Override
    public void beforeCommit() {
        System.out.println("1. beforeCommit2.....");
    }

    @Override
    public void afterCommit() {
        System.out.println("2. afterCommit2.....");
    }

    @Override
    public void beforeRollback() {
        System.out.println("3. beforeRollback2.....");
    }

    @Override
    public void afterRollback() {
        System.out.println("4. afterRollback2.....");
    }

    @Override
    public void beforeClose() {
        System.out.println("5. beforeClose2.....");
    }

    @Override
    public void afterClose() {
        System.out.println("6. afterClose2.....");
    }
}
