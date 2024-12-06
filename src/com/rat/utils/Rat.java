package com.rat.utils;

import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

class Rat implements Runnable {
    int mouseId;
    int currentX, currentY;
    NMaze maze;

    public Rat(int id, NMaze maze) {
        this.mouseId = id;
        this.maze = maze;
    }

    @Override
    public void run() {
        //Mouse stays alive as long as it keeps expanding its frontier.
        while (this.maze.mazeFrontier.containsKey(this.mouseId)) {
            try {
                this.maze.availablePaths.acquire();
                ArrayList<Integer> current = this.maze.mazeFrontier.get(this.mouseId);
                this.maze.mazeFrontier.remove(this.mouseId);
                this.currentX = current.get(0);
                this.currentY = current.get(1);
                String key = current.get(0) + "," + current.get(1);

                this.maze.mazeSkip.put(key, true);
                if (this.maze.isRunningInRealTime) {
                    Platform.runLater(() -> {
                        this.maze.uiNodes[currentY][currentX].setFill(Color.hsb(((this.mouseId + 1) * 15), 1.0, 1.0));
                    });
                    if (this.maze.realTimeStep > 0) {
                        Thread.sleep(this.maze.realTimeStep);
                    }
                }
                try {
                    this.maze.solutionPaths.get(this.mouseId).add(current);
                } catch (Exception e) {
                    this.maze.solutionPaths.add(this.mouseId, new ArrayList<>());
                    this.maze.solutionPaths.get(this.mouseId).add(current);
                }
                ArrayList<ArrayList<Integer>> peekResult = this.maze.peekFrom(current.get(0), current.get(1));
//            System.out.println("I'm mouse:" + this.mouseId + " and I'm at (" + current.get(0) + ", " + current.get(1) + ").");
                if (peekResult.get(0).equals(new ArrayList<Integer>(List.of(0, 0))) && peekResult.get(1).equals(new ArrayList<Integer>(List.of(0, 0)))) {
                    if (this.currentX == this.maze.mazeSize - 1 && this.currentY == this.maze.mazeSize - 1) {
                        this.maze.solutions.incrementAndGet();
                        this.maze.mouseKing = this.mouseId;
                    }
//                System.out.println("Dead-end at (" + current.get(0) + ", " + current.get(1) + ").");
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
            } catch (InterruptedException e) {
                throw new RuntimeException("Rat killed before reaching dead-end.");
            }
        }
    }
}
