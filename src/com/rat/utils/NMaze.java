package com.rat.utils;

import javafx.application.Platform;
import javafx.scene.paint.Color;
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
    Semaphore skippingNow;
    AtomicInteger stampId;
    ThreadPoolExecutor miceManger;
    ConcurrentHashMap<Integer, ArrayList<Integer>> mazeFrontier;
    final ConcurrentHashMap<String, Boolean> mazeSkip;

    //Solution Storage.
    AtomicInteger solutions;
    Integer mouseKing;
    ArrayList<ArrayList<ArrayList<Integer>>> solutionPaths;

    //Live-update.
    Boolean isRunningInRealTime;
    Rectangle[][] uiNodes;
    Integer realTimeStep;

    public NMaze(int size, int[][] physical) {
        this.mazeSize = size;
        this.mazeSpace = physical;

        this.availablePaths = new Semaphore(1);
        this.skippingNow = new Semaphore(1);
        this.stampId = new AtomicInteger(0);
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
    protected ArrayList<ArrayList<Integer>> peekFrom(int x, int y) throws InterruptedException {
        synchronized (this.mazeSkip) {
            ArrayList<ArrayList<Integer>> result = new ArrayList<>(List.of(new ArrayList<>(List.of(0, 0)), new ArrayList<>(List.of(0, 0))));
            boolean rightIsEffectiveOpen = x + 1 < this.mazeSize && this.mazeSpace[y][x + 1] == 1 && this.mazeSkip.get((x + 1) + ", " + y) == null;
            boolean downIsEffectiveOpen = y + 1 < this.mazeSize && this.mazeSpace[y + 1][x] == 1 && this.mazeSkip.get(x + ", " + (y + 1)) == null;
            if (rightIsEffectiveOpen) {
                result.set(1, new ArrayList<>(List.of(x + 1, y)));
            }
            if (downIsEffectiveOpen) {
                result.set(0, new ArrayList<>(List.of(x, y + 1)));
            }
            return result;
        }
    }

    protected void signalFork() {
        this.availablePaths.release();
        this.miceManger.execute(new Rat(this.stampId.getAndIncrement(), this));
    }

    synchronized public int solve() throws InterruptedException {
        //Spawn first mouse at (0,0).
        this.miceManger = new ThreadPoolExecutor(8, 16, 200, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        this.miceManger.execute(new Rat(this.stampId.getAndIncrement(), this));

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

    void drawAndSleep(Integer id, Integer currentX, Integer currentY) throws InterruptedException {
        if (this.isRunningInRealTime) {
            Platform.runLater(() -> {
                this.uiNodes[currentY][currentX].setFill(Color.hsb(((id + 1) * 15), (id%2==0)?1.0:0.5, 1.0));
            });
            if (this.realTimeStep > 0) {
                Thread.sleep(this.realTimeStep);
            }
        }
    }

    public void printSolutionPaths() {
        for (int i = 0; i < this.solutionPaths.size(); i++) {
            System.out.print(i + ": ");
            for (int j = 0; j < this.solutionPaths.get(i).size(); j++) {
                System.out.print("(" + this.solutionPaths.get(i).get(j).get(0) + ", " + this.solutionPaths.get(i).get(j).get(1) + ") -> ");
            }
            System.out.println("X");
        }
    }

    public void printSolutionPath() {
        ArrayList<ArrayList<Integer>> mouseList = this.getSolutionPath();
        mouseList.forEach((ArrayList<Integer> mouseStep) -> {
            System.out.print("(" + mouseStep.getFirst() + ", " + mouseStep.get(1) + ") -> ");
        });
        System.out.println("X");
    }


    public Integer getSolutions() {
        return this.solutions.intValue();
    }

    public Integer getWinner() {
        return this.mouseKing;
    }
}

