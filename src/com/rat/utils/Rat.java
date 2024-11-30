package com.rat.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Rat implements Callable<int[]> {
    int mouseId;
    int startX, startY;
    int currentX, currentY;
    ArrayList<ArrayList<Integer>> takenPath;
    NMaze maze;

    public Rat(int id, NMaze maze) {
//        this.startX = x;
//        this.currentX = x;
//        this.startY = y;
//        this.currentY = y;
        this.mouseId = id;
        this.maze = maze;
    }

    @Override
    public int[] call() throws InterruptedException {
        while (!maze.mazeFrontier.isEmpty()) {
            this.maze.availablePaths.acquire();
            if (this.maze.mazeFrontier.peek().get(2) != this.mouseId) {
                Thread.sleep(5);
            }
            ArrayList<Integer> current = maze.mazeFrontier.poll();
            synchronized (this.maze.physicalProgress) {
                this.maze.physicalProgress.get(current.get(1)).set(current.get(0), 2);
            }
            ArrayList<ArrayList<Integer>> peekResult = maze.peekFrom(current.get(0), current.get(1), this.mouseId, false);
            System.out.println("I'm mouse:" + this.mouseId + " and I'm at (" + current.get(0) + ", " + current.get(1) + ").");
            if (peekResult.get(0).equals(new ArrayList<Integer>(List.of(0, 0, this.mouseId))) && peekResult.get(1).equals(new ArrayList<Integer>(List.of(0, 0, this.mouseId)))) {
                System.out.println("Dead-end at (" + current.get(0) + ", " + current.get(1) + ").");
                return new int[]{0, 0};
            } else if (!peekResult.get(0).equals(new ArrayList<Integer>(List.of(0, 0, this.mouseId))) && !peekResult.get(1).equals(new ArrayList<Integer>(List.of(0, 0, this.mouseId)))) {
                System.out.println("Fork at (" + current.get(0) + ", " + current.get(1) + ").");
                maze.mazeFrontier.add(peekResult.get(0));
                peekResult.get(1).set(2, this.mouseId + 1);
                maze.mazeFrontier.add(peekResult.get(1));
                this.maze.availablePaths.release();
                this.maze.signalFork();
            } else {
                maze.mazeFrontier.add(!peekResult.get(0).equals(new ArrayList<Integer>(List.of(0, 0, this.mouseId))) ? peekResult.get(0) : peekResult.get(1));
                System.out.println();
                this.maze.availablePaths.release();
            }
        }
        return new int[0];
    }
}
