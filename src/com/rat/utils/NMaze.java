package com.rat.utils;
public class NMaze {
    Integer size;
    int[][] physical_space;

    public NMaze(int size, int[][] physical){
        this.size = size;
        this.physical_space = physical;
    }

    public boolean Peek(int x, int y){
        return !(physical_space[y][x] == 1);
    }

    public boolean isDeadEnd(int x, int y){
        return !((this.Peek(x+1, y) || this.Peek(x, y+1)));
    }

    public boolean isFork(int x, int y){
        return (this.Peek(x+1, y) && this.Peek(x, y+1));

    }

    public boolean isSolved(int x, int y){
        return x == size-1 && y == size-1;
    }
}

