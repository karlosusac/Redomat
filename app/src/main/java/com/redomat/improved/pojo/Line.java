package com.redomat.improved.pojo;

public class Line {
    private String name;
    private String owner_id;

    //CONSTRUCTORS
    public Line() {
    }

    public Line(String name, String owner_id) {
        this.name = name;
        this.owner_id = owner_id;
    }

    //GETTERS
    public String getName() {
        return name;
    }

    public String getOwner_id() {
        return owner_id;
    }

    //SETTERS
    public void setName(String name) {
        this.name = name;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }
}
