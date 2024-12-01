package com.rat.utils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class NMaze {
    Integer size;
    int[][] physicalSpace;
    final List<ArrayList<Integer>> physicalProgress;
    Semaphore availablePaths;
    AtomicInteger solutions;
    ConcurrentHashMap<Integer, ArrayList<Integer>> mazeFrontier;
    ConcurrentHashMap<String, Boolean> mazeSkip;
    ThreadPoolExecutor miceManger;
    int stampId;
    ArrayList<ArrayList<ArrayList<Integer>>> solutionPaths;
    boolean allowRevisitWhenDead;

    public NMaze(int size, int[][] physical) {
        this.allowRevisitWhenDead = false;
        this.size = size;
        this.physicalSpace = physical;
        this.physicalProgress = Arrays.stream(physical)
                .map(row -> Arrays.stream(row)
                        .boxed()
                        .collect(Collectors.toCollection(ArrayList::new)))
                .collect(Collectors.toCollection(ArrayList::new));
        this.solutions = new AtomicInteger(0);
        this.mazeFrontier = new ConcurrentHashMap<>();
        this.mazeFrontier.put(0, new ArrayList<Integer>(List.of(0, 0)));
        this.mazeSkip = new ConcurrentHashMap<>();
        this.availablePaths = new Semaphore(1);
        this.solutionPaths = new ArrayList<>(30);
        this.stampId = 0;
    }

    synchronized public int solve() throws InterruptedException {
        this.miceManger = new ThreadPoolExecutor(8, 16, 200, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        this.miceManger.submit(new Rat(this.stampId, this));
        this.stampId += 1;

        while (miceManger.getActiveCount() > 0) {
            Thread.sleep(5000);
        }
        this.miceManger.shutdown();
        return this.solutions.intValue();
    }

    public void getSolutionPaths() {
        this.solutionPaths.stream().forEach((ArrayList<ArrayList<Integer>> mouseList) -> {
            System.out.print(mouseList.getFirst().getLast() + ": ");
            mouseList.stream().forEach((ArrayList<Integer> mouseStep) -> {
                System.out.print("(" + mouseStep.getFirst() + ", " + mouseStep.get(1) + ") -> ");
            });
            System.out.println("X");
        });
    }


    public ArrayList<ArrayList<Integer>> peekFrom(int x, int y) {
            ArrayList<ArrayList<Integer>> result = new ArrayList<>();
            result.add(new ArrayList<>(List.of(0, 0)));
            result.add(new ArrayList<>(List.of(0, 0)));
            String rightKey = (x + 1) + "," + y;
            String downKey = x + "," + (y + 1);
            boolean rightIsEffectiveOpen = x + 1 < this.size && this.physicalSpace[y][x+1] == 1 && this.mazeSkip.get(rightKey) == null;
            boolean downIsEffectiveOpen = y + 1 < this.size && this.physicalSpace[y+1][x] == 1 && this.mazeSkip.get(downKey) == null;
//        boolean rightIsEffectiveOpen = (rightState == 1 || ((rightState == 2 && downState == 0) && allowRevisitWhenDead));
//        boolean downIsEffectiveOpen = (downState == 1 || ((downState == 2 && rightState == 0 ) && allowRevisitWhenDead));
//            boolean rightIsEffectiveOpen = (rightState == 1);
//            boolean downIsEffectiveOpen = (downState == 1);
            if (rightIsEffectiveOpen) {
                result.set(0, new ArrayList<>(List.of(x + 1, y)));
            }
            if (downIsEffectiveOpen) {
                result.set(1, new ArrayList<>(List.of(x, y + 1)));
            }
            return result;
    }


    public void signalFork() {
        this.availablePaths.release();
//        ArrayList<Integer> newStart = this.mazeFrontier.poll();
        this.miceManger.submit(new Rat(this.stampId, this));
        this.stampId += 1;
    }

//    public boolean isDeadEnd(int x, int y) {
//        return !((this.peekFrom(x + 1, y) || this.peekFrom(x, y + 1)));
//    }
//
//    public boolean isFork(int x, int y) {
//        return (this.peekFrom(x + 1, y) && this.peekFrom(x, y + 1));
//
//    }
//
//    public boolean isSolved(int x, int y) {
//        return x == size - 1 && y == size - 1;
//    }
}

