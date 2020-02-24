package com.redomat.improved.pojo;

public class Account {
    private String firstName;
    private String LastName;
    private String email;
    private String password;

    private int numOfParticipatedRedomats;
    private int numOfMadeRedomats;
    private int numOfLeftRedomats;

    //CONSTRUCTORS
    public Account() {
    }

    public Account(String firstName, String LastName, String email, String password) {
        this.firstName = firstName;
        this.LastName = LastName;
        this.email = email;
        this.password = password;

        this.numOfParticipatedRedomats = 0;
        this.numOfMadeRedomats = 0;
        this.numOfLeftRedomats = 0;
    }

    public Account AccountWithoutPassword(Account account){
        Account accountWithoutPassword = new Account();
        accountWithoutPassword.firstName = account.getFirstName();
        accountWithoutPassword.LastName = account.getLastName();
        accountWithoutPassword.email = account.getEmail();

        accountWithoutPassword.numOfParticipatedRedomats = 0;
        accountWithoutPassword.numOfMadeRedomats = 0;
        accountWithoutPassword.numOfLeftRedomats = 0;

        return accountWithoutPassword;
    }

    //GETTERS
    public String getFirstName() {
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

    public int getNumOfParticipatedRedomats() {
        return numOfParticipatedRedomats;
    }

    public int getNumOfMadeRedomats() {
        return numOfMadeRedomats;
    }

    public int getNumOfLeftRedomats() {
        return numOfLeftRedomats;
    }

    //SETTERS
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNumOfParticipatedRedomats(int numOfParticipatedRedomats) {
        this.numOfParticipatedRedomats = numOfParticipatedRedomats;
    }

    public void setNumOfMadeRedomats(int numOfMadeRedomats) {
        this.numOfMadeRedomats = numOfMadeRedomats;
    }

    public void setNumOfLeftRedomats(int numOfLeftRedomats) {
        this.numOfLeftRedomats = numOfLeftRedomats;
    }
}
