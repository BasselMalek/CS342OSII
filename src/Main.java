import com.rat.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int mazeSize = 8;
        int[][] mazeSpace = {
                {1, 1, 1, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 0, 0, 1, 1, 1},
                {0, 0, 1, 0, 0, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 1}
        };
        NMaze maze = new NMaze(mazeSize, mazeSpace);
        System.out.println(maze.solve());

    }
}
    