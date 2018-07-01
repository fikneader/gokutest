package com.example.ulin.gokutest;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

public class ProcedureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(com.example.ulin.gokutest.R.layout.activity_procedure);

        String procedures = getIntent().getStringExtra(RecommendationDetailActivity.EXTRA_COOKING_PROCEDURE);
        generateProcedures(procedures);
    }

    private void generateProcedures(String procedures) {
        try {
            LinearLayout l =(LinearLayout) findViewById(com.example.ulin.gokutest.R.id.procedureLayout);

            JSONArray array = new JSONArray(procedures);

            int n = array.length();
            for(int i=0; i <n;i++) {
                TextView t = new TextView(getApplicationContext());


                t.setText(array.get(i).toString());
                t.setTextColor(Color.parseColor("#ff333333"));

                t.setLayoutParams(new ViewGroup.LayoutParams(
                                dpToPixel(270),
                                dpToPixel(150))
                );

                t.setGravity(Gravity.CENTER);

                if(i==0) {
                    t.setBackgroundResource(com.example.ulin.gokutest.R.mipmap.langkah_bg_top);
                } else if(i==n-1) {
                    t.setBackgroundResource(com.example.ulin.gokutest.R.mipmap.langkah_bg_bottom);
                } else {
                    t.setBackgroundResource(com.example.ulin.gokutest.R.mipmap.langkah_bg_midle);
                }

                t.setPadding(dpToPixel(15), dpToPixel(15), dpToPixel(15), dpToPixel(15));



                l.addView(t);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private int dpToPixel(int dp) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public void toHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
