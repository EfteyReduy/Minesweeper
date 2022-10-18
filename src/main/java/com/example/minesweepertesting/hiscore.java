package com.example.minesweepertesting;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class hiscore extends AppCompatActivity {

    TextView beginner, easy, medium, hard, insane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiscore);
        getSupportActionBar().setTitle("High Score");
        SharedPreferences sharedPreferences=getSharedPreferences("minesweeperHiScore", MainActivity.MODE_PRIVATE);
        beginner=findViewById(R.id.beginnerScore);
        easy=findViewById(R.id.easyScore);
        medium=findViewById(R.id.mediumScore);
        hard=findViewById(R.id.hardScore);
        insane=findViewById(R.id.insaneScore);
        if(sharedPreferences.contains("beginner"))
        {
            long time=sharedPreferences.getLong("beginner", 1000000000);
            String str=process(time);
            beginner.setText(str);
        }
        if(sharedPreferences.contains("easy"))
        {
            long time=sharedPreferences.getLong("easy", 1000000000);
            String str=process(time);
            easy.setText(str);
        }
        if(sharedPreferences.contains("medium"))
        {
            long time=sharedPreferences.getLong("medium", 1000000000);
            String str=process(time);
            medium.setText(str);
        }
        if(sharedPreferences.contains("hard"))
        {
            long time=sharedPreferences.getLong("hard", 1000000000);
            String str=process(time);
            hard.setText(str);
        }
        if(sharedPreferences.contains("insane"))
        {
            long time=sharedPreferences.getLong("insane", 1000000000);
            String str=process(time);
            insane.setText(str);
        }
    }
    public String process(long time)//convert time from second to minute and second format
    {
        if(time>=1000000000)
        {
            return "--:--";
        }
        int minuit, second;
        minuit=(int)time/60;
        second=(int)time%60;
        String S0="", M0="";
        if(minuit<10)
        {
            M0="0";
        }
        if(second<10)
        {
            S0="0";
        }
        String stt=M0+Integer.toString(minuit)+":"+S0+Integer.toString(second);
        return stt;
    }
}
