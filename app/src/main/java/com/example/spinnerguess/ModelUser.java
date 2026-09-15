package com.example.spinnerguess;

public class ModelUser {
    private String userName;
    private String uName;
    private String password;
    private int score;
    private long id;

    public ModelUser(String userName, String uName, String password) {
        this.userName = userName;
        this.uName = uName;
        this.password = password;
        this.score = 0;
        this.id = 0;
    }

    public ModelUser(String userName, String uName, String password, int score, long id) {
        this.userName = userName;
        this.uName = uName;
        this.password = password;
        this.score = score;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void updateUser(int score) {
        this.score = score;
    }
}