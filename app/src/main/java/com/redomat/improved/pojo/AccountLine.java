package com.redomat.improved.pojo;

import java.io.Serializable;

public class AccountLine implements Serializable {
    private String accountId;
    private String status;


    //CONSTRUCTORS
    public AccountLine() {}

    public AccountLine(String accountId) {
        this.accountId = accountId;
        this.status = "active";
    }

    public AccountLine(String accountId, String status) {
        this.accountId = accountId;
        this.status = status;
    }

    //GETTERS
    public String getAccountId() {
        return accountId;
    }

    public String getStatus() {
        return status;
    }

    //SETTERS
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
