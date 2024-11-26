import com.rat.utils.NMaze;
import com.rat.utils.Solver;

import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
    int size = 8;
        int[][] oneSolOneDeadMaze = {
                {0, 0, 0, 1, 1, 1, 1, 1},
                {1, 1, 0, 1, 1, 1, 1, 1},
                {1, 1, 0, 1, 1, 1, 1, 1},
                {1, 1, 0, 0, 0, 0, 1, 1},
                {1, 1, 1, 1, 1, 0, 1, 1},
                {1, 1, 1, 1, 1, 0, 1, 1},
                {1, 1, 0, 0, 1, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 0}
        };
        NMaze maze = new NMaze(size, oneSolOneDeadMaze);
        Solver oneSolSolver = new Solver(maze);
        System.out.println(oneSolSolver.solve());

    }
}