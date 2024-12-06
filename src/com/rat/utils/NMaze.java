package com.rat.utils;

import javafx.scene.shape.Rectangle;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NMaze {
    //Physical properties.
    Integer mazeSize;
    int[][] mazeSpace;

    //Pathfinding.
    Semaphore availablePaths;
    int stampId;
    ThreadPoolExecutor miceManger;
    ConcurrentHashMap<Integer, ArrayList<Integer>> mazeFrontier;
    final ConcurrentHashMap<String, Boolean> mazeSkip;

    //Solution Storage.
    AtomicInteger solutions;
    int mouseKing;
    ArrayList<ArrayList<ArrayList<Integer>>> solutionPaths;

    //Live-update.
    Boolean isRunningInRealTime;
    Rectangle[][] uiNodes;
    Integer realTimeStep;

    public NMaze(int size, int[][] physical) {
        this.mazeSize = size;
        this.mazeSpace = physical;

        this.availablePaths = new Semaphore(1);
        this.stampId = 0;
        this.mazeFrontier = new ConcurrentHashMap<>();
        this.mazeFrontier.put(0, new ArrayList<Integer>(List.of(0, 0)));
        this.mazeSkip = new ConcurrentHashMap<>();

        this.solutions = new AtomicInteger(0);
        this.solutionPaths = new ArrayList<>(30);
        this.mouseKing = -1;
    }

    public void setRealTime(Boolean setVal) {
        this.isRunningInRealTime = setVal;
    }

    public void setRealTimeParams(Integer realTimeStep, Rectangle[][] uiNodes) {
        this.realTimeStep = realTimeStep;
        this.uiNodes = uiNodes;
    }

    //Pathfinding logic.
    protected ArrayList<ArrayList<Integer>> peekFrom(int x, int y) {
        synchronized (this.mazeSkip) {
            ArrayList<ArrayList<Integer>> result = new ArrayList<>(List.of(new ArrayList<>(List.of(0, 0)), new ArrayList<>(List.of(0, 0))));
            boolean rightIsEffectiveOpen = x + 1 < this.mazeSize && this.mazeSpace[y][x + 1] == 1 && this.mazeSkip.get((x + 1) + "," + y) == null;
            boolean downIsEffectiveOpen = y + 1 < this.mazeSize && this.mazeSpace[y + 1][x] == 1 && this.mazeSkip.get(x + "," + (y + 1)) == null;
            if (rightIsEffectiveOpen) {
                result.set(0, new ArrayList<>(List.of(x + 1, y)));
            }
            if (downIsEffectiveOpen) {
                result.set(1, new ArrayList<>(List.of(x, y + 1)));
            }
            return result;
        }
    }

    protected void signalFork() {
        this.availablePaths.release();
        this.miceManger.execute(new Rat(this.stampId, this));
        this.stampId += 1;
    }

    synchronized public int solve() throws InterruptedException {
        //Spawn first mouse at (0,0).
        this.miceManger = new ThreadPoolExecutor(8, 16, 200, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        this.miceManger.execute(new Rat(this.stampId, this));
        this.stampId += 1;

        while (this.miceManger.getActiveCount() > 0) {
            Thread.sleep(5);
        }
        this.miceManger.shutdown();
        if (this.miceManger.awaitTermination(500, TimeUnit.MILLISECONDS)) {
            return this.solutions.intValue();
        } else {
            throw new InterruptedException("Threads exceeded timeout.");
        }
    }

    public ArrayList<ArrayList<ArrayList<Integer>>> getSolutionPaths() {
        return this.solutionPaths;
    }

    public ArrayList<ArrayList<Integer>> getSolutionPath() {
        return this.mouseKing == -1 ? new ArrayList<>() : this.solutionPaths.get(this.mouseKing);
    }

    public void printSolutionPaths() {
        this.solutionPaths.forEach((ArrayList<ArrayList<Integer>> mouseList) -> {
            System.out.print(mouseList.getFirst().getLast() + ": ");
            mouseList.forEach((ArrayList<Integer> mouseStep) -> {
                System.out.print("(" + mouseStep.getFirst() + ", " + mouseStep.get(1) + ") -> ");
            });
            System.out.println("X");
        });
    }

    public void printSolutionPath() {
        ArrayList<ArrayList<Integer>> mouseList = this.getSolutionPath();
        mouseList.forEach((ArrayList<Integer> mouseStep) -> {
            System.out.print("(" + mouseStep.getFirst() + ", " + mouseStep.get(1) + ") -> ");
        });
        System.out.println("X");
    }


}

