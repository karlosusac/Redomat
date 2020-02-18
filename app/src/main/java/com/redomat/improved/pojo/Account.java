package com.redomat.improved.pojo;

public class Account {
    private String firstName;
    private String LastName;
    private String email;
    private String password;

    //CONSTRUCTORS
    public Account() {
    }

    public Account(String first_name, String last_name, String email, String password) {
        this.firstName = firstName;
        this.LastName = LastName;
        this.email = email;
        this.password = password;
    }

    //GETTERS
    public String getfirstName() {
        return firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    //SETTERS
    public void setfirstName(String first_name) {
        this.firstName = firstName;
    }

    public void setLastName(String last_name) {
        this.LastName = LastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
