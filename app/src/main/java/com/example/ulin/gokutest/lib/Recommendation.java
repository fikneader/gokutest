package com.example.ulin.gokutest.lib;

/**
 * Created by ulin on 6/22/2016.
 */
public class Recommendation extends Recipe {
    public int matchIngredients;
    public int totalIngredients;
    public int percentage;
    public Recommendation(String id, String name, String image, int time, int rating,int matchIngredients, int totalIngredients, int percentage) {
        super(id,name,image,time,rating);
    }
}
