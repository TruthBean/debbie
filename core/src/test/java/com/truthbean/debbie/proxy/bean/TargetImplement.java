package com.truthbean.debbie.proxy.bean;

public class TargetImplement implements TargetInterface {
    @Override
    public void code() {
        System.out.println("逗逼程序猿在编程...");
    }

    @Override
    public void learn() {
        System.out.println("逗逼程序猿在疯狂学习中...");
    }
}
