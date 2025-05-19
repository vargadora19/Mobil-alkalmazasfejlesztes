package com.example.f1blog;

public class BlogItem {
    private String id; // dokumentum azonosító a Firestore-ban
    private String name;
    private String info;
    private int imageResource;

    public BlogItem() {
    }

    public BlogItem(int imageResource, String info, String name) {
        this.imageResource = imageResource;
        this.info = info;
        this.name = name;
    }

    // Getterek és setterek
    public String _getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }
    public int getImageResource() {
        return imageResource;
    }
    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
