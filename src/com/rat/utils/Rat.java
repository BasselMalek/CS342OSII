package com.rat.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Rat implements Callable<int[]> {
    int mouseId;
    int currentX, currentY;
    NMaze maze;

    public Rat(int id, NMaze maze) {
        this.mouseId = id;
        this.maze = maze;
    }

    @Override
    public int[] call() throws InterruptedException {
        //Mouse stays alive as long as it keeps expanding its frontier.
        while (this.maze.mazeFrontier.containsKey(this.mouseId)) {
            this.maze.availablePaths.acquire();
            ArrayList<Integer> current = this.maze.mazeFrontier.get(this.mouseId);
            this.maze.mazeFrontier.remove(this.mouseId);
            this.currentX = current.get(0);
            this.currentY = current.get(1);
            String key = current.get(0) + "," + current.get(1);

            this.maze.mazeSkip.put(key, true);
            try {
                this.maze.solutionPaths.get(this.mouseId).add(current);
            } catch (Exception e) {
                this.maze.solutionPaths.add(this.mouseId, new ArrayList<>());
                this.maze.solutionPaths.get(this.mouseId).add(current);
            }
            ArrayList<ArrayList<Integer>> peekResult = this.maze.peekFrom(current.get(0), current.get(1));
//            System.out.println("I'm mouse:" + this.mouseId + " and I'm at (" + current.get(0) + ", " + current.get(1) + ").");
            if (peekResult.get(0).equals(new ArrayList<Integer>(List.of(0, 0))) && peekResult.get(1).equals(new ArrayList<Integer>(List.of(0, 0)))) {
                if (this.currentX == this.maze.mazeSize -1 && this.currentY == this.maze.mazeSize -1){
                    this.maze.solutions.incrementAndGet();
                    this.maze.mouseKing = this.mouseId;
                }
//                System.out.println("Dead-end at (" + current.get(0) + ", " + current.get(1) + ").");
                return new int[]{0, 0};
            } else if (!peekResult.get(0).equals(new ArrayList<Integer>(List.of(0, 0))) && !peekResult.get(1).equals(new ArrayList<Integer>(List.of(0, 0)))) {
//                System.out.println("Fork at (" + current.get(0) + ", " + current.get(1) + ").");
                maze.mazeFrontier.put(this.mouseId, peekResult.get(0));
                maze.mazeFrontier.put(this.maze.stampId, peekResult.get(1));
                this.maze.availablePaths.release();
                this.maze.signalFork();
            } else {
                maze.mazeFrontier.put(this.mouseId, !peekResult.get(0).equals(new ArrayList<Integer>(List.of(0, 0))) ? peekResult.get(0) : peekResult.get(1));
                this.maze.availablePaths.release();
            }
        }
        return new int[0];
    }
}
