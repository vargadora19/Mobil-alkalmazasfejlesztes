package com.example.f1blog;

public class BlogItem {
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

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getResource(){
        return imageResource;
    }
}
