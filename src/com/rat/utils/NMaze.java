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
    ConcurrentLinkedQueue<ArrayList<Integer>> mazeFrontier;
    ThreadPoolExecutor miceManger;
    int lastMouseId;
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
        this.mazeFrontier = new ConcurrentLinkedQueue<>();
        this.mazeFrontier.add(new ArrayList<Integer>(List.of(0, 0, 0)));
        this.availablePaths = new Semaphore(1);
        this.solutionPaths = new ArrayList<>(List.of(new ArrayList<>(), new ArrayList<>()));
        this.lastMouseId = 0;
    }

    synchronized public int solve() throws InterruptedException {
        this.miceManger = new ThreadPoolExecutor(8, 16, 200, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        this.miceManger.submit(new Rat(this.lastMouseId++, this));

        while (miceManger.getActiveCount() > 0) {
            Thread.sleep(5000);
        }
        this.miceManger.shutdown();
        return this.solutions.intValue();
    }

    public void getSolutionPaths() {
        this.solutionPaths.stream().forEach((ArrayList<ArrayList<Integer>> mouseList)->{
            System.out.print(mouseList.getFirst().getLast() + ": ");
           mouseList.stream().forEach((ArrayList<Integer> mouseStep)->{
            System.out.print("(" +mouseStep.getFirst() + ", " + mouseStep.get(1) + ") -> ");
           });
        System.out.println("X");
        });
    }


    public ArrayList<ArrayList<Integer>> peekFrom(int x, int y, int callerId, boolean allowRevisitWhenDead) {
        synchronized (this.physicalProgress) {
            ArrayList<ArrayList<Integer>> result = new ArrayList<>();
            result.add(new ArrayList<>(List.of(0, 0, callerId)));
            result.add(new ArrayList<>(List.of(0, 0, callerId)));
            int rightState = (x + 1 < this.size) ? this.physicalProgress.get(y).get(x + 1) : 0;
            int downState = (y + 1 < this.size) ? this.physicalProgress.get(y + 1).get(x) : 0;
//        boolean rightIsEffectiveOpen = (rightState == 1 || ((rightState == 2 && downState == 0) && allowRevisitWhenDead));
//        boolean downIsEffectiveOpen = (downState == 1 || ((downState == 2 && rightState == 0 ) && allowRevisitWhenDead));
            boolean rightIsEffectiveOpen = (rightState == 1);
            boolean downIsEffectiveOpen = (downState == 1);
            if (rightIsEffectiveOpen) {
                result.set(0, new ArrayList<>(List.of(x + 1, y, callerId)));
            }
            if (downIsEffectiveOpen) {
                result.set(1, new ArrayList<>(List.of(x, y + 1, callerId)));
            }

            return result;
        }
    }


    public void signalFork() {
        this.availablePaths.release();
//        ArrayList<Integer> newStart = this.mazeFrontier.poll();
        this.miceManger.submit(new Rat(this.lastMouseId++, this));

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

