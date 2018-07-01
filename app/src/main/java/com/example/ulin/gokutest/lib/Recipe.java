package com.example.ulin.gokutest.lib;

/*
 * Created by skywarden on 20/06/16.
 */
public class Recipe {
    public final String id;
    public final String name;
    public final String image;
    public final int    time;
    public final int    rating;

    public Recipe(String id, String name, String image, int time, int rating) {
        this.id     = id;
        this.name   = name;
        this.image  = image;
        this.time   = time;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return name;
    }

}
