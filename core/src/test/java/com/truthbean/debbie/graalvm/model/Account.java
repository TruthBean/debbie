package com.truthbean.debbie.graalvm.model;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/25 17:03.
 */
public class Account {
    private Long id;

    private String name;

    private String password;

    public Account() {
    }

    public Account(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public Account(Long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public Account(Account account) {
        if (account != null) {
            this.id = account.id;
            this.name = account.name;
            this.password = account.password;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "\"Account\":{" + "\"id\":" + id + "," + "\"name\":\"" + name + '\"' + "," + "\"password\":\"" + password + '\"' + '}';
    }
}
