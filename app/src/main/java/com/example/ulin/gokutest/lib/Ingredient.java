package com.example.ulin.gokutest.lib;

/*
 * Created by skywarden on 21/06/16.
 */
public class Ingredient {
    public final String  id;
    public final String  category;
    public final boolean checked;
    public final String  name;
    public final String  image;

    public Ingredient(String id, String category, boolean checked, String name, String image) {
        this.id       = id;
        this.category = category;
        this.checked  = checked;
        this.name     = name;
        this.image    = image;
    }

    @Override
    public String toString() {
        return name;
    }
}
