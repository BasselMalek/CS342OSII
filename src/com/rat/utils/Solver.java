package com.rat.utils;
import java.util.*;
import java.util.concurrent.*;

public class Solver {

    int solutions;
    List<List<Integer>> Mice;
    int lastMouseId;
    NMaze maze;
    List<List<List<Integer>>> solutionPaths;

    public Solver(NMaze maze){
        this.solutions = 0;
        this.solutionPaths = new ArrayList<>();
        this.Mice = new ArrayList<>();
        this.maze = maze;
    }

    public int solve() throws ExecutionException, InterruptedException {
        ExecutorService ratSpawner = Executors.newFixedThreadPool(16);
        Mice.add(Arrays.stream(ratSpawner.submit(new Rat(0, 0, this.maze)).get()).boxed().toList());
        return this.Mice.get(0).get(2);
    }
}
