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
 * Created by skywarden on 21/06/16.
 */
public class IngredientList {

    public List<Ingredient> items;
    public Map<String, Ingredient> itemMap;

    public IngredientList(String jsonData){
        items   = new ArrayList<>();
        itemMap = new HashMap<>();

        try {
            JSONArray ingredients = new JSONArray(jsonData);

            // Add some sample items.
            int nIngredients = ingredients.length();
            for (int i = 0; i < nIngredients; i++) {
                JSONObject ingredient = ingredients.getJSONObject(i);

                String code     = ingredient.optString("kd_bahan");
                String category = ingredient.optString("kd_kategori");
                String name     = ingredient.optString("nama");
                String image    = ingredient.optString("gambar");
                boolean checked = (ingredient.optInt("picked") == 1);

                addItem(code, category, checked, name, image);
            }
        }
        catch (JSONException e) {
            Log.e("INGREDIENTS", e.getMessage());
        }
    }

    private void addItem(Ingredient item) {
        items.add(item);
        itemMap.put(item.id, item);
    }

    private void addItem(String id, String category, boolean checked, String name, String image){
        Ingredient item = new Ingredient(id, category, checked, name, image);
        addItem(item);
    }
}
