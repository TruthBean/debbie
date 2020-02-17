package com.truthbean.debbie.jdbc.transaction;

public class PrintResourceHolder implements ResourceHolder {
    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public void prepare() {
        System.out.println("0. prepare");
    }

    @Override
    public void beforeCommit() {
        System.out.println("1. beforeCommit");
    }

    @Override
    public void afterCommit() {
        System.out.println("2. afterCommit");
    }

    @Override
    public void beforeRollback() {
        System.out.println("3. beforeRollback");
    }

    @Override
    public void afterRollback() {
        System.out.println("4. afterRollback");
    }

    @Override
    public void beforeClose() {
        System.out.println("5. beforeClose");
    }

    @Override
    public void afterClose() {
        System.out.println("6. afterClose");
    }
}
