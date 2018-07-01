package com.example.ulin.gokutest.lib;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Created by skywarden on 20/06/16.
 */
public class RecommendationList {

    public List<Recipe>         items;
    public Map<String, Recipe>  itemMap;

    public RecommendationList(String jsonData){
        items   = new ArrayList<>();
        itemMap = new HashMap<>();

        try {
            JSONArray recipes = new JSONArray(jsonData);

            // Add some sample items.
            int nRecipes = recipes.length();
            for (int i = 0; i < nRecipes; i++) {
                JSONObject recipe = recipes.getJSONObject(i);

                String code   = recipe.optString("kd_resep");
                String name   = recipe.optString("nama");
                String image  = recipe.optString("gambar");
                int    time   = recipe.optInt("waktu");
                int    rating = recipe.optInt("rating");
                int    match  = recipe.optInt("jumlah");

                addItem(code, name, image, time, rating);
            }
        }
        catch (JSONException e) {
            Log.e("RECIPE", e.getMessage());
        }
    }

    private void addItem(Recipe item) {
        items.add(item);
        itemMap.put(item.id, item);
    }

    private void addItem(String id, String name, String image, int time, int rating){
        Recipe item = new Recipe(id, name, image, time, rating);
        addItem(item);
    }
}
