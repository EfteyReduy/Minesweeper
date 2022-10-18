package com.example.minesweepertesting;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private GridView uncoveredGrid, mapGrid, flagGrid;
    private Button flagButton;
    private TextView restartButton, bombCounter, timer, homeButton;
    boolean flaged=false, gameStarted=false, gameWon=false, gameLost=false, gamePaused=false;
    private int numCol, numRow, usableHeight, level, remainingFlag, totalUncovered=0, time=0;
    private long maxTime=1000000000, tickTime=100, pausedTime=0, timeToWin;
    private Model model;

    private int[][] mapp, visited, uncovered;
    private ArrayAdapter<String> adapter, adapter1, adapter2;
    private String[] mapStr, uncoveredStr, flagStr;

    private AlertDialog.Builder alart;

    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flagButton=findViewById(R.id.flagButtonId);
        uncoveredGrid=findViewById(R.id.uncoveredGridId);
        mapGrid=findViewById(R.id.mapGridId);
        flagGrid=findViewById(R.id.flagGridId);

        bombCounter=findViewById(R.id.flagId);
        timer=findViewById(R.id.timerId);
        homeButton=findViewById(R.id.homeButtonId);
        restartButton=findViewById(R.id.restartId);

        Bundle bundle=getIntent().getExtras();
        level=2;
        if(bundle!=null) {
            level = bundle.getInt("level");
        }


        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        float density  = getResources().getDisplayMetrics().density;

        float dpHeight = (float)height / density;
        dpHeight+=0.5;
        float dpWidth  = (float)width / density;
        dpWidth+=0.5;
        width= (int) dpWidth;
        height=(int)dpHeight;

        numCol=(width)/35;
        usableHeight=height-50;
        numRow=usableHeight/35;
        numRow--;

        model=new Model(level, numCol, numRow);
        model.generateMap();
        remainingFlag=model.getNumberOfBombs();
        bombCounter.setText("\uD83D\uDCA3\n"+Integer.toString(remainingFlag));
        visited=new int[numRow][numCol];
        uncovered=new int[numRow][numCol];

        mapp=model.getMap();
        mapStr=new String[(numCol)*(numRow)];
        uncoveredStr=new String[(numCol)*(numRow)];
        flagStr=new String[(numCol)*(numRow)];
        for(int i=0; i<numRow; i++)
        {
            for(int j=0; j<numCol; j++)
            {
                visited[i][j]=uncovered[i][j]=0;
                if(mapp[i][j]==-1) {
                    mapStr[i * numCol + j] = "\uD83D\uDCA3";
                }
                else if(mapp[i][j]==0)
                    mapStr[i*numCol+j]="";
                else
                    mapStr[i*numCol+j]=Integer.toString(mapp[i][j]);
                uncoveredStr[i*numCol+j]=" ";
                flagStr[i*numCol+j]="\uD83D\uDEA9";
            }
        }

        mapGrid.setNumColumns(numCol);
        uncoveredGrid.setNumColumns(numCol);
        flagGrid.setNumColumns(numCol);

        adapter1=new ArrayAdapter<>(this, R.layout.map_grid_layout, R.id.sample_listId2, uncoveredStr);
        adapter=new ArrayAdapter<>(this, R.layout.list_sample, R.id.sample_listId, mapStr);
        adapter2=new ArrayAdapter<>(this, R.layout.map_grid_layout, R.id.sample_listId2, uncoveredStr);
        mapGrid.setAdapter(adapter);
        uncoveredGrid.setAdapter(adapter1);
        flagGrid.setAdapter(adapter2);


        uncoveredGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!gameWon && !gameLost &&!gamePaused) {
                    if(!gameStarted) {
                        countDownTimer.start();
                    }
                    gameStarted = true;
                   // Log.d("lowapi", "onItemClick: uncovered " + position + " Id is : " + id);
                    float x = view.getAlpha();
                    //Log.d("lowapi", "onItemClick: passed get alpha()");
                    if (x == 0) {
                        //view.animate().alpha(1).setDuration(00);
                        //view.setAlpha(1);
                    } else {//Checking wader flag button is clicked or not
                        if (!flaged) {//if flag button is not clicked
                            if (uncoveredStr[position] != flagStr[position]) {//
                                view.setAlpha(0);
                                int X, Y;
                                X = position / numCol;
                                Y = position % numCol;
                                if (mapStr[position] == "") {
                                    DFS_onGrid(X, Y);
                                } else {
                                    totalUncovered += (1 - uncovered[X][Y]);
                                    uncovered[X][Y] = 1;
                                }
                                if (checkLost(position)) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "You have staped into a Bomb. Restart the game.", Toast.LENGTH_LONG);
                                    toast.show();
                                    gameLost = true;
                                    revealBombs();
                                    vibrate();
                                    // need to have a setting where the user can on or off the vibration mode....
                                    //Game lost..
                                    //revel full map
                                    //ask if player wants to restart the game or want ot go to main menu...
                                }
                                else if (checkWin()) {
                                    gameWon = true;
                                    Toast toast = Toast.makeText(getApplicationContext(), "Congratulation!!! You have detected all the mines.", Toast.LENGTH_LONG);
                                    toast.show();
                                    revelWinMap();
                                    vibrate();
                                    //Game won....
                                    //create a congratulation screen
                                    //ask if player wants to restart the game or want to go to main menu...
                                }

                            }
                        } else {
                            if (uncoveredStr[position] == flagStr[position]) {
                                uncoveredStr[position] = "";
                                remainingFlag++;
                                bombCounter.setText("\uD83D\uDCA3\n" + Integer.toString(remainingFlag));
                            } else {
                                if (remainingFlag > 0) {
                                    uncoveredStr[position] = flagStr[position];
                                    remainingFlag--;
                                    bombCounter.setText("\uD83D\uDCA3\n" + Integer.toString(remainingFlag));
                                }
                            }
                            adapter1.notifyDataSetChanged();
                        }

                        //view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }

                }

            }
        });


        countDownTimer=new CountDownTimer(maxTime, tickTime) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minuit, second;
                boolean beenPaused=false;
                long time=(maxTime-millisUntilFinished-pausedTime)/1000;
                timeToWin=time;
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
                if(!gamePaused && !gameWon && !gameLost) {
                    timer.setText(stt);
                    //Log.d("ggg", "onTick: millisUnitFinished : "+millisUntilFinished);
                }
                else{
                    pausedTime+=(tickTime);
                    beenPaused=true;
                }

            }

            @Override
            public void onFinish() {

            }
        };
       // countDownTimer.start();
        Thread timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    if(gameStarted && !gameWon && !gameLost &&!gamePaused)
                    {
                        int minuit, second;
                        time++;
                        minuit=time/60;
                        second=time%60;
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
                        timer.setText(stt);

                        try {
                            Thread.sleep(1000);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });
       // timerThread.start();


        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //paused the game or play the game
                if(gameStarted && !gameLost && !gameWon) {
                    gamePaused = !gamePaused;
                    if (gamePaused) {
                        timer.setText("Paused");
                        mapGrid.setAlpha(0);
                    } else {
                        mapGrid.setAlpha(1);
                    }
                }
            }
        });
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gamePaused=true;
                mapGrid.setAlpha(0);
                alart=new AlertDialog.Builder(MainActivity.this);
                alart.setTitle("Do you want to restart?");
                alart.setCancelable(false);
                alart.setMessage("Your current progress will be lost");
                alart. setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mapGrid.setAlpha(1);
                        restart();
                    }
                });
                alart.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gamePaused=false;
                        mapGrid.setAlpha(1);
                    }
                });
                if(gameWon || gameLost || !gameStarted)
                {
                    mapGrid.setAlpha(1);
                    restart();
                }
                else {
                    AlertDialog dialog = alart.create();
                    dialog.show();
                }
            }
        });



        flagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flaged=!flaged;
                if(flaged)
                {
                    v.setAlpha(1);
                }
                else{
                    v.setAlpha(0.3f);
                }
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameLost || gameWon || !gameStarted)
                {
                    MainActivity.super.onBackPressed();
                }
                else {
                    showAlart();
                }
            }
        });

    }

    public void showAlart()
    {
        gamePaused=true;
        mapGrid.setAlpha(0);
        alart=new AlertDialog.Builder(this);
        alart.setTitle("Do you want to exit?");
        alart.setCancelable(false);
        alart.setMessage("Your current progress will be lost");
        alart. setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.super.onBackPressed();
            }
        });
        alart.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gamePaused=false;
                mapGrid.setAlpha(1);
            }
        });
        AlertDialog dialog= alart.create();
        dialog.show();
    }
    @Override
    public void onBackPressed() {
        if(gameLost || gameWon || !gameStarted)
        {
            MainActivity.super.onBackPressed();
        }
        else {
            showAlart();
        }
    }

    @Override
    protected void onDestroy() {
        countDownTimer.cancel();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("pause", "onPause: ");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("pause", "onResume: ");
        super.onResume();
    }

    public void restart()
    {
        maxTime=1000000000;
        countDownTimer.cancel();
        pausedTime=0;
        countDownTimer=new CountDownTimer(maxTime, tickTime) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minuit, second;
                long time=(maxTime-millisUntilFinished-pausedTime)/1000;
                timeToWin=time;
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
                if(!gamePaused && !gameWon && !gameLost)
                    timer.setText(stt);
                else{
                    pausedTime+=tickTime;
                }
            }

            @Override
            public void onFinish() {

            }
        };

        gamePaused=false;
        gameStarted=false;
        gameWon=false;
        gameLost=false;
        time=0;
        timer.setText("00:00");
        model=new Model(level, numCol, numRow);
        model.generateMap();
        model.restartGame();
        mapp=model.getMap();
        totalUncovered=0;
        remainingFlag=model.getNumberOfBombs();
        bombCounter.setText("\uD83D\uDCA3\n"+Integer.toString(remainingFlag));
        for(int i=0; i<numRow; i++)
        {
            for(int j=0; j<numCol; j++)
            {
                visited[i][j]=uncovered[i][j]=0;
                if(mapp[i][j]==-1)
                {
                    mapStr[i*numCol+j]="\uD83D\uDCA3";
                }
                else if(mapp[i][j]==0)
                    mapStr[i*numCol+j]="";
                else
                    mapStr[i*numCol+j]=Integer.toString(mapp[i][j]);
                uncoveredStr[i*numCol+j]=" ";
                flagStr[i*numCol+j]="\uD83D\uDEA9";
            }
        }

        adapter1=new ArrayAdapter<>(this, R.layout.map_grid_layout, R.id.sample_listId2, uncoveredStr);
        adapter=new ArrayAdapter<>(this, R.layout.list_sample, R.id.sample_listId, mapStr);
        adapter2=new ArrayAdapter<>(this, R.layout.map_grid_layout, R.id.sample_listId2, uncoveredStr);
        mapGrid.setAdapter(adapter);
        uncoveredGrid.setAdapter(adapter1);
        flagGrid.setAdapter(adapter2);
        //countDownTimer.start();
    }
    public boolean checkWin(){
        if((numCol*numRow)-totalUncovered==model.getNumberOfBombs()) {
            //save high score!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            SharedPreferences sharedPreferences=getSharedPreferences("minesweeperHiScore", MainActivity.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            //editor.putString("beginner", "00:00");
            String lvl="";
            if(level==1)
            {
                lvl="beginner";
            }
            if(level==2)
            {
                lvl="easy";
            }
            if(level==3)
            {
                lvl="medium";
            }
            if(level==4)
            {
                lvl="hard";
            }
            if(level==5)
            {
                lvl="insane";
            }
            if(sharedPreferences.contains(lvl))
            {
                long previousBest=sharedPreferences.getLong(lvl, 1000000000);
                if(previousBest>=timeToWin)
                {
                    editor.putLong(lvl, timeToWin);
                    editor.commit();
                }
            }
            else{
                editor.putLong(lvl, timeToWin);
                editor.commit();
            }


            return true;

        }
        return false;
    }
    public boolean checkLost(int position){
        if(mapStr[position]=="\uD83D\uDCA3")
            return true;
        return false;
    }


    //Finding all naiboring 0 and uncovering all of them...............
    public void DFS_onGrid(int X, int Y)
    {
        if(X<0 || Y<0 || Y>=numCol || X>=numRow)
        {
            return;
        }
        if(visited[X][Y]==0)
        {
            visited[X][Y]=1;
            for(int i=-1; i<2; i++)
            {
                for(int j=-1; j<2; j++)
                {
                    if(X+i<0 || Y+j<0 || Y+j>=numCol || X+i>=numRow)
                    {
                        continue;
                    }
                    View v = uncoveredGrid.getChildAt((X+i)*numCol+(Y+j));
                    v.setAlpha(0);

                    totalUncovered+=(1-uncovered[X+i][Y+j]);
                    uncovered[X+i][Y+j]=1;
                    if(mapp[X+i][Y+j]==0)
                    {
                        DFS_onGrid(X+i, Y+j);
                    }
                }
            }
        }
    }


    public void revelWinMap(){
        for(int i=0; i<numRow; i++)
        {
            for(int j=0; j<numCol; j++)
            {
                if(mapStr[i*numCol+j]=="\uD83D\uDCA3")
                {
                    uncoveredStr[i*numCol+j]=flagStr[0];
                    adapter1.notifyDataSetChanged();
                }
            }
        }
        remainingFlag=0;
        bombCounter.setText("\uD83D\uDCA3\n"+Integer.toString(remainingFlag));
    }

    public void revealBombs(){
        for(int i=0; i<numCol*numRow; i++)
        {
            if(mapStr[i]=="\uD83D\uDCA3" && uncoveredStr[i]!=flagStr[0])
            {
                uncoveredStr[i]="\uD83D\uDCA3";
            }
        }
        adapter1.notifyDataSetChanged();
    }


    public void vibrate(){
        Vibrator v = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
// Vibrate for 100 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(5);
        }
    }
}
/*
Development environment
    hardware configuration
    software configuration

Structure of an android project

Project folder structure

Designing Map for the game

Designing Activity Layouts
    Home Activity
    HighScore Activity
    Rules Activity
    Main Activity
    Determining Win or Lose


Future Aspects
Reference

*/
