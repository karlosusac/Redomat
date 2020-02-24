package com.redomat.improved.pojo;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;

public class Line implements Serializable {
    private String name;
    private String status;
    private ArrayList<AccountLine> redomatLine;
    private Integer currentPosition;
    private Integer redomatLength;
    private Integer nextPersonTime;

    //CONSTRUCTORS

    public Line() {}

    public Line(String name, String status, Integer currentPosition, Integer redomatLength, Integer nextPersonTime) {
        this.name = name;
        this.status = status;
        this.redomatLine = new ArrayList<>(); this.redomatLine.add(new AccountLine(null, null));
        this.currentPosition = currentPosition;
        this.redomatLength = redomatLength;
        this.nextPersonTime = nextPersonTime;
    }

    public Line(String name, String status) {
        this.name = name;
        this.status = status;
        this.redomatLine = new ArrayList<>(); this.redomatLine.add(new AccountLine(null, null));
        this.currentPosition = 0;
        this.redomatLength = 0;
        this.nextPersonTime = 0;
    }


    //GETTERS
    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public ArrayList<AccountLine> getRedomatLine() {
        return redomatLine;
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public Integer getRedomatLength() {
        return redomatLength;
    }

    public Integer getNextPersonTime() {
        return nextPersonTime;
    }

    //SETTERS
    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRedomatLine(ArrayList<AccountLine> redomatLine) {
        this.redomatLine = redomatLine;
    }

    public void setCurrentPosition(Integer currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setRedomatLength(Integer redomatLength) {
        redomatLength = redomatLength;
    }

    public void setNextPersonTime(Integer nextPersonTIme) {
        this.nextPersonTime = nextPersonTIme;
    }


    public static String generatePin(){
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(100000);
        String pin = String.format("%05d", num);
        return(pin);
    }

    public void pushUser(String id){
        this.redomatLine.add(new AccountLine(id, "active"));

    }

    public void pushUser(String id, String status){
        this.redomatLine.add(new AccountLine(id, status));

    }

    public void inactiveUser(Integer index){
        this.redomatLine.get(index).setStatus("inactive");
    }

    public void incrementRedomatLength(){
        this.redomatLength += 1;
    }

    public void incrementRedomatCurrentPosition(){
        this.currentPosition += 1;
    }
}
