package com.example.ulin.gokutest;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.ulin.gokutest.fragments.RecipeFragment;
import com.example.ulin.gokutest.lib.Recipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecipesActivity extends FragmentActivity implements RecipeFragment.OnListFragmentInteractionListener{
    //PART 1 MENU & SEARCH
    EditText    editTextSearch;
    ImageButton buttonSearch;
    boolean     isSearching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(com.example.ulin.gokutest.R.layout.activity_recipes);
        //1
        editTextSearch  = (EditText) findViewById(com.example.ulin.gokutest.R.id.editTextSearch);
        buttonSearch    = (ImageButton) findViewById(com.example.ulin.gokutest.R.id.buttonSearch);

        //LISTENER 1
        editTextSearch.setAlpha(0);
        setSearchVisible(false);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // If new state, do initial search.
        if(savedInstanceState == null)
            search("");
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    public void toMenu(View view){
        NavUtils.navigateUpFromSameTask(this);
    }

    public void toggleSearch(View view) {
        search(editTextSearch.getText().toString());
        if(!isSearching)
            searchOpen();
        else
            searchClose();
    }

    //PART 1 HANDLERS
    private void search(final String keyword) {
        if(findViewById(com.example.ulin.gokutest.R.id.listRecipe) != null){
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo         networkInfo         = connectivityManager.getActiveNetworkInfo();

            if(networkInfo != null && networkInfo.isConnected()){
                String parameters = "search=" + keyword + "&limit=5&start=0";
                String url = "http://goku.ngeartstudio.com/services/resep.php";
                //String url = "http://192.168.43.164/test.php";
                new GetRecipesTask().execute(url, parameters);
            }
            else {
                Log.e("CONNECTION: ", "NOT CONNECTED");
            }
        }
    }

    private class GetRecipesTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            try{
                URL url = new URL(urls[0] + "?" + urls[1]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                /*
                conn.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(urls[1]);
                wr.flush();
                wr.close();
                */

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            }
            catch(IOException e){
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                super.onPostExecute(response);
                RecipeFragment recipeFragment = new RecipeFragment();

                Bundle args = new Bundle();
                args.putString(RecipeFragment.ARG_DATA, response);

                recipeFragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(com.example.ulin.gokutest.R.id.listRecipe, recipeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            catch(IllegalStateException error){
                Log.e("RecipesActivity", "Illegal state exception on onPostExecute().");
            }
        }
    }

    private void searchOpen() {
        final InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //search(editTextSearch.getText().toString());

        isSearching = true;
        setSearchVisible(true);

        editTextSearch.animate().alpha(1);
        buttonSearch.postDelayed(new Runnable() {
            @Override
            public void run() {
                keyboard.showSoftInput(editTextSearch, 0);
            }
        }, 200);
    }

    private void searchClose() {
        InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        isSearching = false;
        setSearchVisible(false);

        editTextSearch.animate().alpha(0);
        keyboard.hideSoftInputFromWindow(editTextSearch.getApplicationWindowToken(), 0);

        //search(editTextSearch.getText().toString());
    }

    private void setSearchVisible(boolean bool) {
        editTextSearch.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.example.ulin.gokutest.R.menu.menu_recipes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.example.ulin.gokutest.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onListFragmentInteraction(Recipe item){
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, item.id+"");
        startActivity(intent);
    }
}
