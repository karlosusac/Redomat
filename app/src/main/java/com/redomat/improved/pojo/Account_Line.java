package com.redomat.improved.pojo;

public class Account_Line {
    String account_id;
    String line_id;
    String status;

    //CONSTRUCTORS
    public Account_Line() {
    }

    public Account_Line(String account_id, String line_id, String status) {
        this.account_id = account_id;
        this.line_id = line_id;
        this.status = status;
    }

    //GETTERS
    public String getAccount_id() {
        return account_id;
    }

    public String getLine_id() {
        return line_id;
    }

    public String getStatus() {
        return status;
    }

    //SETTERS
    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public void setLine_id(String line_id) {
        this.line_id = line_id;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
