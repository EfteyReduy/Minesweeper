package com.example.minesweepertesting;

import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import java.util.Random;

public class Model {

    private int numberOfBombs;
    private int numberOfCol;
    private int numberOfRow;
    private int[][] map, mapState;
    private int isBomb =-1;
    private int uncovered=-2;
    private int covered=-3;
    private int level;

    public int getNumberOfCol()
    {
        return this.numberOfCol;
    }
    public int getNumberOfRow()
    {
        return this.numberOfRow;
    }
    public int getNumberOfBombs()
    {
        return this.numberOfBombs;
    }

    Model(int level, int numberOfCol, int numberOfRow)
    {
        this.level=level;
        this.numberOfCol=numberOfCol;
        this.numberOfRow=numberOfRow;
    }
    public void setNumberOfBombs(int level)// set a particular level and set number of bombs based on level....
    {
        this.level=level;
        if(level==1)
        {
            this.numberOfBombs=(numberOfCol*numberOfRow/14);
        }
        if(level==2)
        {
            this.numberOfBombs=(numberOfCol*numberOfRow/10);
        }
        if(level==3)
        {
            this.numberOfBombs=(numberOfCol*numberOfRow/8);
        }
        if(level==4)
        {
            this.numberOfBombs=(numberOfCol*numberOfRow/6);
        }
        if(level==5)
        {
            this.numberOfBombs=(numberOfCol*numberOfRow/4);
        }
    }

    public void generateMap()// generate a map on the basis of given constrains....
    {
        setNumberOfBombs(this.level);
        this.map=new int[numberOfRow][numberOfCol];
        this.mapState=new int[numberOfRow][numberOfCol];
        int bombsRemainingToPlace=this.numberOfBombs;
        while(bombsRemainingToPlace>0)
        {
            Random random=new Random();
            int x=random.nextInt(this.numberOfCol);
            int y=random.nextInt(this.numberOfRow);
            if(x==0)
            {
                if(y==0 || y==this.numberOfRow-1)
                {
                    continue;
                }
            }
            if(x==this.numberOfCol-1)
            {
                if(y==0 || y==this.numberOfRow-1)
                {
                    continue;
                }
            }
            if(this.map[y][x]!= this.isBomb)
            {
                this.map[y][x]= this.isBomb;
                bombsRemainingToPlace--;
            }

        }

        for(int i=0; i<this.numberOfRow; i++)
        {
            for(int j=0; j<this.numberOfCol; j++)
            {
                this.mapState[i][j]=this.covered;
            }
        }

        //generates how many bombs are sarunding a particular cell
        generateNeighbor();
    }
    public void generateNeighbor()//generates number of bombs sarrunding a particular cell..
    {
        for(int i=0; i<this.numberOfRow; i++)
        {
            for(int j=0; j<this.numberOfCol; j++)
            {
                if(this.map[i][j]== isBomb)
                {
                    continue;
                }
                else{
                    int count=0;
                    count+=hasBomb(i+1, j+1);
                    count+=hasBomb(i, j+1);
                    count+=hasBomb(i, j-1);
                    count+=hasBomb(i+1, j);
                    count+=hasBomb(i+1, j-1);
                    count+=hasBomb(i-1, j);
                    count+=hasBomb(i-1, j-1);
                    count+=hasBomb(i-1, j+1);
                    this.map[i][j]=count;

                }
            }
        }
    }

    public int hasBomb(int rowNumber, int colNumber)// hasBomn function checks weather there is a
                                                        // bomb or not in a particular cell
    {
        if(colNumber>=this.numberOfCol || colNumber<0)
        {
            return 0;
        }
        if(rowNumber>=this.numberOfRow || rowNumber<0)
        {
            return 0;
        }
        if(this.map[rowNumber][colNumber]==isBomb)
        {
            return 1;
        }
        else{
            return 0;
        }
    }

    public int[][] getMap() {
        return this.map;
    }

    public void restartGame()
    {

    }

}
