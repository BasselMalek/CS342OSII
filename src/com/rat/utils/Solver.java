package com.rat.utils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Solver {
    AtomicInteger solutions;
    ConcurrentLinkedQueue<Future<int[]>> Mice;
    int lastMouseId;
    NMaze maze;
    List<List<List<Integer>>> solutionPaths;

    public Solver(NMaze maze) {
        this.solutions = new AtomicInteger(0);
        this.solutionPaths = new ArrayList<>();
        this.Mice = new ConcurrentLinkedQueue<>();
        this.maze = maze;
    }

    public int solve(){

    return this.solutions.intValue();
    }

    public List<List<List<Integer>>> getSolutionPaths(){

        return this.solutionPaths;
    }
}
