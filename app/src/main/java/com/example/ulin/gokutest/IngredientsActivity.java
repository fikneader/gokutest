package com.example.ulin.gokutest;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.ulin.gokutest.fragments.IngredientFragment;
import com.example.ulin.gokutest.lib.Ingredient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IngredientsActivity extends FragmentActivity implements IngredientFragment.OnListFragmentInteractionListener {

    public static final String EXTRA_INGREDIENT_IDS = "com.example.ulin.gokutest.INGREDIENT_IDS";
    boolean isSearching = false;

    private String[] categories = new String[4];
    private String[] selectedIngredients = {"", "", "", ""};
    private int      currentCategory = 0;
    private int      selectedCount = 0;
    private boolean  isConfirmed = false;

    //PART 1 MENU & SEARCH
    EditText    editTextSearch;
    ImageButton buttonSearch;

    //PART 2 CATEGORY SELECT
    LinearLayout    layoutCategory;
    TextView        textViewCategory;
    ImageButton     btnPrevCategory;
    ImageButton     btnNextCategory;

    //PART 3
    TextView        textViewSelected;
    TextView        textViewSelectedCounter;
    ImageButton     buttonNext;
    LinearLayout    layoutList;
    ScrollView      scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(com.example.ulin.gokutest.R.layout.activity_ingredients);

        categories[0] = "UTAMA";
        categories[1] = "PELENGKAP";
        categories[2] = "BUMBU";
        categories[3] = "ALAT";

        //1
        editTextSearch  = (EditText) findViewById(com.example.ulin.gokutest.R.id.editTextSearch);
        buttonSearch    = (ImageButton) findViewById(com.example.ulin.gokutest.R.id.buttonSearch);

        //2
        layoutCategory   = (LinearLayout) findViewById(com.example.ulin.gokutest.R.id.layoutCategory);
        textViewCategory = (TextView) findViewById(com.example.ulin.gokutest.R.id.textViewCategory);
        btnPrevCategory  = (ImageButton) findViewById(com.example.ulin.gokutest.R.id.btnPrevCategory);
        btnNextCategory  = (ImageButton) findViewById(com.example.ulin.gokutest.R.id.btnNextCategory);

        //3
        textViewSelected = (TextView) findViewById(com.example.ulin.gokutest.R.id.textViewSelected);
        textViewSelectedCounter = (TextView) findViewById(com.example.ulin.gokutest.R.id.textViewSelectedCounter);
        layoutList = (LinearLayout) findViewById(com.example.ulin.gokutest.R.id.layoutList);
        scrollView = (ScrollView) findViewById(com.example.ulin.gokutest.R.id.scrollView);
        buttonNext = (ImageButton) findViewById(com.example.ulin.gokutest.R.id.buttonNext);
        try {
            buttonNext.setVisibility(View.INVISIBLE);
        }
        catch (NullPointerException error){
            Log.e("Ingredient Activity", "buttonNext is null.");
        }

        //LISTENER 1
        editTextSearch.setAlpha(0);
        setSearchVisible(false);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s.toString(), currentCategory + 1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        search("");

        updateArrows();
    }

    // EVENT HANDLERS
    public void toggleSearch(View view) {
        if(!categories[currentCategory].equals(getString(com.example.ulin.gokutest.R.string.search_result))) {
            search(editTextSearch.getText().toString());
        }
        if(!isSearching)
            searchOpen();
        else
            searchClose();

        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    public void toMenu(View view){
        NavUtils.navigateUpFromSameTask(this);
    }

    public void selectIngredient(View view){

        View parent = (View) view.getParent();

        LinearLayout checkbox   = (LinearLayout) parent.findViewById(com.example.ulin.gokutest.R.id.checked);
        TextView idView         = (TextView) parent.findViewById(com.example.ulin.gokutest.R.id.ingredient_id);
        TextView categoryView   = (TextView) parent.findViewById(com.example.ulin.gokutest.R.id.category_id);


        String id    = idView.getText().toString();
        int category = Integer.parseInt(categoryView.getText().toString()) - 1;

        String[] ingredients  = selectedIngredients[category].split(",");

        int idPosition = -1;

        boolean isChecked = false;

        for(int i = 0;i < ingredients.length;i++){
            if(ingredients[i].equals(id)){
                idPosition = i;
                isChecked  = true;
                break;
            }
        }

        if(isChecked){
            if(ingredients.length > 1){
                if(idPosition == 0)
                    selectedIngredients[category] = selectedIngredients[category].replace(id + ",", "");
                else
                    selectedIngredients[category] = selectedIngredients[category].replace("," + id, "");
            }
            else {
                selectedIngredients[category] = selectedIngredients[category].replace(id, "");
            }

            selectedCount--;
            checkbox.setBackgroundResource(com.example.ulin.gokutest.R.mipmap.bg_ingredientname);
        }
        else{
            if(selectedIngredients[category].equals(""))
                selectedIngredients[category] = id;
            else
                selectedIngredients[category] = selectedIngredients[category] + "," + id;

            selectedCount++;
            checkbox.setBackgroundResource(com.example.ulin.gokutest.R.mipmap.bg_ingredientname_checked);
        }
        textViewSelectedCounter.setText(String.valueOf(selectedCount));

        if(checkComplete()){
            buttonNext.setVisibility(View.VISIBLE);
        }
        else{
            buttonNext.setVisibility(View.INVISIBLE);
        }
    }

    public void commitCategory(View view) {
        if(checkComplete()){
            if(!isConfirmed) {
                isConfirmed = true;

                search("", 0);

                textViewCategory.setText(getString(com.example.ulin.gokutest.R.string.picked));
                setEnablePrevCategory(true);
                setEnableNextCategory(false);
            }
            else{
                Intent intent = new Intent(this, RecommendationActivity.class);
                intent.putExtra(EXTRA_INGREDIENT_IDS, TextUtils.join(",", selectedIngredients));

                startActivity(intent);
            }
        }
    }

    public void prevCategory(View view) {
        if(!isSearching)
            currentCategory--;

        isConfirmed = false;
        buttonArrowHandler();
    }

    public void nextCategory(View view) {
        currentCategory++;
        buttonArrowHandler();
    }

    private void buttonArrowHandler() {
        String category = categories[currentCategory];

        textViewCategory.setText(category);

        search("", currentCategory + 1);
        searchClose();
        updateArrows();
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        }, 50);
    }
    // END EVENT HANDLERS

    private boolean checkComplete(){
        boolean isComplete = true;
        for (String selectedIngredient:selectedIngredients) {
            if(selectedIngredient.equals("")){
                isComplete = false;
                break;
            }
        }
        return isComplete;
    }

    private void searchOpen() {
        isSearching = true;
        final InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        // Change text to search result.
        textViewCategory.setText(getString(com.example.ulin.gokutest.R.string.search_result));

        // Only turn on prev arrow.
        setEnablePrevCategory(true);
        setEnableNextCategory(false);

        setSearchVisible(true);
        layoutCategory.animate().translationY(dpToPixel(15));
        editTextSearch.animate().alpha(1);
        buttonSearch.postDelayed(new Runnable() {
            @Override
            public void run() {
                keyboard.showSoftInput(editTextSearch, 0);
            }
        }, 200);
    }

    private void searchClose() {
        isSearching = false;
        InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        editTextSearch.animate().alpha(0);
        layoutCategory.animate().translationY(dpToPixel(0));
        keyboard.hideSoftInputFromWindow(editTextSearch.getApplicationWindowToken(), 0);
        setSearchVisible(false);
    }

    private void setSearchVisible(boolean b) {
        editTextSearch.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }

    private void search(String keyword) {
        search(keyword, 1);
    }

    private void search(String keyword, int category) {
        if(findViewById(com.example.ulin.gokutest.R.id.layoutList) != null){
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo                 = connectivityManager.getActiveNetworkInfo();

            if(networkInfo != null && networkInfo.isConnected()){
                String selected   = (category > 0)? selectedIngredients[category-1] : TextUtils.join(",", selectedIngredients);
                String pickedOnly = (isConfirmed) ? "1" : "0";
                String parameters = "search=" + keyword + "&category=" + category + "&picked=" + selected + "&pickedOnly=" + pickedOnly + "&limit=10&start=0";
                String url = "http://goku.ngeartstudio.com/services/bahan.php";



                new GetIngredientsTask().execute(url, parameters);
            }
            else {
                Log.e("CONNECTION: ", "NOT CONNECTED");
            }
        }
    }

    @Override
    public void onListFragmentInteraction(Ingredient item) {

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    private class GetIngredientsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try{
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
            }
            catch(IOException e){
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                super.onPostExecute(response);
                IngredientFragment ingredientFragment = new IngredientFragment();

                Bundle args = new Bundle();
                args.putString(IngredientFragment.ARG_DATA, response);

                ingredientFragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(com.example.ulin.gokutest.R.id.layoutList, ingredientFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            catch(IllegalStateException error){
                Log.e("IngredientsActivity", "Illegal state exception on onPostExecute().");
            }
        }
    }

    private int dpToPixel(int dp) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void updateArrows() {
        setEnablePrevCategory(currentCategory > 0);
        setEnableNextCategory(currentCategory != categories.length - 1);
    }

    private void setEnablePrevCategory(boolean enabled){
        if(enabled) {
            btnPrevCategory.setEnabled(true);
            btnPrevCategory.setVisibility(View.VISIBLE);
            btnPrevCategory.setAlpha(0f);
            btnPrevCategory.animate().alpha(1f);
        }
        else {
            btnPrevCategory.setEnabled(false);
            btnPrevCategory.setVisibility(View.INVISIBLE);
        }
    }

    private void setEnableNextCategory(boolean enabled){
        if(enabled) {
            btnNextCategory.setEnabled(true);
            btnNextCategory.setVisibility(View.VISIBLE);
            btnNextCategory.setAlpha(0f);
            btnNextCategory.animate().alpha(1f);
        }
        else {
            btnNextCategory.setEnabled(false);
            btnNextCategory.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.example.ulin.gokutest.R.menu.menu_ingredients, menu);
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
}