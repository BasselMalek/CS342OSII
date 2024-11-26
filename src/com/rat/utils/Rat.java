package com.rat.utils;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Rat implements Callable<int[]> {
    int mouseId;
    int startX, startY;
    int currentX, currentY;
    boolean isSuccessful;
    NMaze maze;

    public Rat(int x, int y, NMaze maze){
        this.startX = x;
        this.currentX = x;
        this.startY = y;
        this.currentY = y;
        this.maze = maze;
        this.isSuccessful = maze.isSolved(x, y);
    }
    @Override
    public int[] call() {
        while(!this.maze.isDeadEnd(this.currentX, this.currentY)){
            if (this.maze.isFork(this.currentX, this.currentY)){
                return new int[]{this.currentX, this.currentY, 2};
            } else if (this.maze.Peek(currentX+1, currentY)){
                this.currentX +=1;
            }else {
                this.currentY+=1;
            }
        }
        if (this.maze.isSolved(currentX, currentY)){
            return new int[]{this.currentX, this.currentY, 1};
        }else{
            return new int[]{this.currentX, this.currentY, 0};
        }
    }
}
