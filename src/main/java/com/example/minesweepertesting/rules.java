package com.example.minesweepertesting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class rules extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        Log.d("intch", "onCreate: at rules");

        getSupportActionBar().setTitle("Help");
    }
}
