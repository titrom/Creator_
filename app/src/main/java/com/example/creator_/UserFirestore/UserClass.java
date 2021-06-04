package com.example.creator_.UserFirestore;

import java.util.ArrayList;

public class UserClass {
    private String nickname;
    private Number XpUser;
    private Number Level;
    private Number XpMax;
    private ArrayList<String> listIdBook;
    private final ArrayList<String> subscribersId;

    public UserClass(String nickname, Number xpUser, Number level, Number xpMax, ArrayList<String> listIdBook, ArrayList<String> subscribersId) {
        this.nickname = nickname;
        XpUser = xpUser;
        Level = level;
        XpMax = xpMax;
        this.listIdBook = listIdBook;
        this.subscribersId = subscribersId;
    }

    public ArrayList<String> getSubscribersId() {
        return subscribersId;
    }

    public ArrayList<String> getListIdBook() {
        return listIdBook;
    }

    public void setListIdBook(ArrayList<String> listIdBook) {
        this.listIdBook = listIdBook;
    }


    public Number getXpMax() {
        return XpMax;
    }

    public void setXpMax(Number xpMax) {
        XpMax = xpMax;
    }

    public Number getXpUser() {
        return XpUser;
    }

    public void setXpUser(Number xpUser) {
        XpUser = xpUser;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Number getLevel() {
        return Level;
    }
    public void setLevel(Number level) {
        Level = level;
    }
}
