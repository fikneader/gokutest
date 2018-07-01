package com.example.ulin.gokutest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {
    public static final String EXTRA_RECIPE_ID = "com.example.ulin.gokutest.EXTRA_RECIPE_ID";
    public static final String EXTRA_COOKING_PROCEDURE = "com.example.ulin.gokutest.EXTRA_COOKING_PROCEDURE";
    String procedure;
    ArrayList<CheckBox> checkBoxes = new ArrayList<CheckBox>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(com.example.ulin.gokutest.R.layout.activity_recommendation_detail);

        String id = getIntent().getStringExtra(EXTRA_RECIPE_ID);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String parameters = "kode_resep=" + id;
            String url = "http://goku.ngeartstudio.com/services/resep_detail.php";

            new GetRecipeDetailTask().execute(url, parameters);
        } else {
            Log.e("CONNECTION: ", "NOT CONNECTED");
        }
    }

    public void toProcedure(View view) {
        Intent intent = new Intent(this, ProcedureActivity.class);
        intent.putExtra(EXTRA_COOKING_PROCEDURE, procedure);
        startActivity(intent);
    }

    private class GetRecipeDetailTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0] + "?" + urls[1]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                super.onPostExecute(response);
                try {
                    JSONObject detail = new JSONObject(response);
                    JSONArray recipeArray = detail.getJSONArray("detail_resep");
                    JSONObject recipe = recipeArray.getJSONObject(0);
                    JSONArray ingredients = detail.getJSONArray("detail_bahan");
                    JSONArray procedures = detail.getJSONArray("langkah_masak");

                    procedure = procedures.toString();

                    TextView timeView = (TextView) findViewById(com.example.ulin.gokutest.R.id.time);
                    TextView nameView = (TextView) findViewById(com.example.ulin.gokutest.R.id.name);
                    ImageView imageView = (ImageView) findViewById(com.example.ulin.gokutest.R.id.image);
                    RatingBar ratingBar = (RatingBar) findViewById(com.example.ulin.gokutest.R.id.rating);
                    LinearLayout ingredientView = (LinearLayout) findViewById(com.example.ulin.gokutest.R.id.ingredients);
                    LinearLayout procedureView = (LinearLayout) findViewById(com.example.ulin.gokutest.R.id.procedures);

                    if (nameView != null)
                        nameView.setText(recipe.getString("nama"));

                    Picasso.with(getApplicationContext()).load("http://goku.ngeartstudio.com/images/" + recipe.getString("gambar")).into(imageView);

                    if (timeView != null) {
                        timeView.setText(recipe.optInt("waktu") + " Menit");
                    }
                    if (ratingBar != null) {
                        ratingBar.setRating((float) recipe.optDouble("rating"));
                    }

                    if (ingredientView != null) {
                        int nIngredients = ingredients.length();
                        for (int i = 0; i < nIngredients; i++) {
                            JSONObject ingredient = ingredients.getJSONObject(i);
                            String text = ingredient.optString("nama") + " " + ingredient.optString("jumlah") + " " + ingredient.optString("satuan") + ".";

                            CheckBox textView = new CheckBox(getApplicationContext());
                            textView.setLayoutParams(
                                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            );
                            //textView.setTextSize(18);
                            textView.setTextColor(Color.BLACK);
                            textView.setText(text);

                            checkBoxes.add(textView);
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    updatePercentage();
                                }
                            });
                            ingredientView.addView(textView);
                        }
                    }
                    updatePercentage();

                    if (procedureView != null) {
                        int nProcedures = procedures.length();
                        for (int i = 0; i < nProcedures; i++) {
                            TextView textView = new TextView(getApplicationContext());
                            textView.setLayoutParams(
                                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            );
                            textView.setTextSize(18);
                            textView.setTextColor(Color.BLACK);
                            textView.setText(procedures.getString(i));

                            procedureView.addView(textView);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("RECIPE DETAIL", e.getMessage());
                }
            } catch (IllegalStateException error) {
                Log.e("IngredientsActivity", "Illegal state exception on onPostExecute().");
            }
        }

        private void updatePercentage() {
            TextView percentageView = (TextView) findViewById(com.example.ulin.gokutest.R.id.percentage);
            int n = checkBoxes.size();
            int counter = 0;
            for (int i = 0; i < n; i++) {
                if (checkBoxes.get(i).isChecked()) {
                    counter++;
                }
            }
            float a = (float) counter;
            float b = (float) n;
            int result = (int) Math.floor((a / b) * 100f);
            percentageView.setText(result + "%");
        }
    }
}