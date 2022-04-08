package com.example.model.albums;

public class Album {
    private int id;
    private String name;
    private String pwd;
    private int index;
    private boolean isClicked = false;

    public Album(int id, String name, String pwd, int index) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.index = index;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    public void readScreenshotAlbum(){

    }
}
