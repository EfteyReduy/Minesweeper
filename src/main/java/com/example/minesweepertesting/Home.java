package com.example.minesweepertesting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Home extends AppCompatActivity {
    TextView beginner, easy, medium, hard, insane;
    ImageView setting, rule;
    int level;
    boolean clicked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        clicked=false;
        beginner=findViewById(R.id.beginner);
        easy=findViewById(R.id.easy);
        medium=findViewById(R.id.medium);
        hard=findViewById(R.id.hard);
        insane=findViewById(R.id.insane);

        setting=findViewById(R.id.settingButton);
        rule=findViewById(R.id.rulesButton);

        beginner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level=1;
                startGame(level);
            }
        });
        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level=2;
                startGame(level);
            }
        });
        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level=3;
                startGame(level);
            }
        });
        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level=4;
                v.animate().alpha(0).setDuration(400);
                v.animate().alpha(1).setDuration(500);
                startGame(level);
            }
        });
        insane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level=5;
                startGame(level);
            }
        });



        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to setting.........

                //Log.d("intch", "onClick: after intent declaration");
                Intent intent=new Intent (Home.this, hiscore.class);
                //Log.d("intch", "onClick: after intent declaration2");
                startActivity(intent);
            }
        });
        rule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to game rules......
                //Log.d("intch", "onClick: after intent declaration");
                Intent intent=new Intent (Home.this, rules.class);
                //Log.d("intch", "onClick: after intent declaration2");
                startActivity(intent);
            }
        });
    }
    void startGame(int level)
    {
        Intent intent=new Intent(Home.this, MainActivity.class);
        intent.putExtra("level", level);
        startActivity(intent);
    }
}
