import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import com.rat.utils.*;

class MazeTest {

    @Test
    @DisplayName("One n maze open.")
    void testOneNOpen(){
        int[][] maze = {{1}};
        NMaze mazeSolver = new NMaze(1, maze);
        assertDoesNotThrow(() -> {
            assertEquals(1, mazeSolver.solve());
            ArrayList<ArrayList<Integer>> solutionPath = mazeSolver.getSolutionPath();
            validateSolutionPath(solutionPath, maze);
        });
    }


//    @Test
//    @DisplayName("One n maze closed.")
//    void testOneNClosed(){
//        int[][] maze = {{0}};
//        NMaze mazeSolver = new NMaze(1, maze);
//        assertDoesNotThrow(() -> {
//            assertEquals(0, mazeSolver.solve());
//            ArrayList<ArrayList<Integer>> solutionPath = mazeSolver.getSolutionPath();
//            validateSolutionPath(solutionPath, maze);
//        });
//    }

    @Test
    @DisplayName("Multiple dead-ends with only one valid path to the goal")
    void testMultipleDeadEnds() {
        int[][] maze = {
                {1, 1, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 0},
                {0, 0, 1, 0, 0, 0, 1, 1},
                {0, 0, 1, 1, 1, 1, 1, 1}
        };
        NMaze mazeSolver = new NMaze(8, maze);
        assertDoesNotThrow(() -> {
            assertEquals(1, mazeSolver.solve());
            ArrayList<ArrayList<Integer>> solutionPath = mazeSolver.getSolutionPath();
            validateSolutionPath(solutionPath, maze);
        });
    }

    @Test
    @DisplayName("Multiple paths to the solution")
    void testMultiplePathsToSolution() {
        int[][] maze = {
                {1, 1, 1, 0, 0, 0},
                {0, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 1, 0},
                {0, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 1, 1}
        };
        NMaze mazeSolver = new NMaze(6, maze);
        assertDoesNotThrow(() -> {
            assertEquals(1, mazeSolver.solve());
            ArrayList<ArrayList<Integer>> solutionPath = mazeSolver.getSolutionPath();
            validateSolutionPath(solutionPath, maze);
        });
    }

    @Test
    @DisplayName("No solution exists, should return 0")
    void testNoSolution() {
        int[][] maze = {
                {1, 1, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 1}
        };
        NMaze mazeSolver = new NMaze(5, maze);
        assertDoesNotThrow(() -> {
            assertEquals(0, mazeSolver.solve());
            ArrayList<ArrayList<Integer>> solutionPath = mazeSolver.getSolutionPath();
            assertTrue(solutionPath.isEmpty(), "Solution path should be empty when no solution exists");
        });
    }

    @Test
    @DisplayName("Open map with no walls, should have one valid solution")
    void testOpenMap() {
        int[][] maze = {
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1}
        };
        NMaze mazeSolver = new NMaze(7, maze);
        assertDoesNotThrow(() -> {
            assertEquals(1, mazeSolver.solve());
            ArrayList<ArrayList<Integer>> solutionPath = mazeSolver.getSolutionPath();
            validateSolutionPath(solutionPath, maze);
        });
    }

    @Test
    @DisplayName("Fully blocked map with no paths, should return 0 solutions")
    void testClosedMap() {
        int[][] maze = {
                {1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1}
        };
        NMaze mazeSolver = new NMaze(6, maze);
        assertDoesNotThrow(() -> {
            assertEquals(0, mazeSolver.solve());
            ArrayList<ArrayList<Integer>> solutionPath = mazeSolver.getSolutionPath();
            assertTrue(solutionPath.isEmpty(), "Solution path should be empty for a blocked map");
        });
    }

    @Test
    @DisplayName("Long narrow path with one solution")
    void testLongAndNarrowPath() {
        int[][] maze = {
                {1, 1, 1, 1, 1},
                {0, 0, 0, 0, 1},
                {0, 0, 0, 0, 1},
                {0, 0, 0, 0, 1},
                {0, 0, 0, 0, 1}
        };
        NMaze mazeSolver = new NMaze(5, maze);
        assertDoesNotThrow(() -> {
            assertEquals(1, mazeSolver.solve());
            ArrayList<ArrayList<Integer>> solutionPath = mazeSolver.getSolutionPath();
            validateSolutionPath(solutionPath, maze);
        });
    }

    @Test
    @DisplayName("Zigzag path, solver should find one valid solution")
    void testZigzagPath() {
        int[][] maze = {
                {1, 1, 0, 0, 0, 0},
                {0, 1, 1, 0, 0, 0},
                {0, 0, 1, 1, 0, 0},
                {0, 0, 0, 1, 1, 0},
                {0, 0, 0, 0, 1, 1},
                {0, 0, 0, 0, 0, 1}
        };
        NMaze mazeSolver = new NMaze(6, maze);
        assertDoesNotThrow(() -> {
            assertEquals(1, mazeSolver.solve());
            ArrayList<ArrayList<Integer>> solutionPath = mazeSolver.getSolutionPath();
            validateSolutionPath(solutionPath, maze);
        });
    }

    @Test
    @DisplayName("Small maze with a dead-end, should find one valid path")
    void testSmallMazeWithDeadEnd() {
        int[][] maze = {
                {1, 1, 0},
                {0, 1, 0},
                {0, 1, 1}
        };
        NMaze mazeSolver = new NMaze(3, maze);
        assertDoesNotThrow(() -> {
            assertEquals(1, mazeSolver.solve());
            ArrayList<ArrayList<Integer>> solutionPath = mazeSolver.getSolutionPath();
            validateSolutionPath(solutionPath, maze);
        });
    }

    @Test
    @DisplayName("Fully blocked start, should return 0 solutions")
    void testFullyBlockedStart() {
        int[][] maze = {
                {1, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 1}
        };
        NMaze mazeSolver = new NMaze(4, maze);
        assertDoesNotThrow(() -> {
            assertEquals(0, mazeSolver.solve());
            ArrayList<ArrayList<Integer>> solutionPath = mazeSolver.getSolutionPath();
            assertTrue(solutionPath.isEmpty(), "Solution path should be empty for a blocked start");
        });
    }

    @Test
    @DisplayName("Fully blocked end, should return 0 solutions")
    void testFullyBlockedEnd() {
        int[][] maze = {
                {1, 1, 1, 1},
                {0, 0, 0, 1},
                {0, 0, 0, 1},
                {0, 0, 0, 0}
        };
        NMaze mazeSolver = new NMaze(4, maze);
        assertDoesNotThrow(() -> {
            assertEquals(0, mazeSolver.solve());
            ArrayList<ArrayList<Integer>> solutionPath = mazeSolver.getSolutionPath();
            assertTrue(solutionPath.isEmpty(), "Solution path should be empty for a blocked end");
        });
    }

    @Test
    @DisplayName("Long open corridor, should find one valid solution")
    void testLongOpenCorridor() {
        int[][] maze = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
        };
        NMaze mazeSolver = new NMaze(10, maze);
        assertDoesNotThrow(() -> {
            assertEquals(1, mazeSolver.solve());
            ArrayList<ArrayList<Integer>> solutionPath = mazeSolver.getSolutionPath();
            validateSolutionPath(solutionPath, maze);
        });
    }

    @Test
    @DisplayName("Only downward movements allowed, should find one solution")
    void testOnlyDownwardMovements() {
        int[][] maze = {
                {1, 0, 0, 0, 0},
                {1, 0, 0, 0, 0},
                {1, 0, 0, 0, 0},
                {1, 0, 0, 0, 0},
                {1, 1, 1, 1, 1}
        };
        NMaze mazeSolver = new NMaze(5, maze);
        assertDoesNotThrow(() -> {
            assertEquals(1, mazeSolver.solve());
            ArrayList<ArrayList<Integer>> solutionPath = mazeSolver.getSolutionPath();
            validateSolutionPath(solutionPath, maze);
        });
    }

    private void validateSolutionPath(ArrayList<ArrayList<Integer>> solutionPath, int[][] maze) {
        assertFalse(solutionPath.isEmpty(), "Invalid Path: must not be empty for solvable mazes");
        for (int i = 1; i < solutionPath.size(); i++) {
            ArrayList<Integer> current = solutionPath.get(i);
            ArrayList<Integer> previous = solutionPath.get(i - 1);
            int xDiff = current.get(0) - previous.get(0);
            int yDiff = current.get(1) - previous.get(1);
            // Ensure legal moves.
            assertTrue(xDiff + yDiff == 1, "Invalid move: must be net +1 distance moved.");
            assertTrue(maze[current.get(1)][current.get(0)] == 1, "Invalid move: must only move over open cells");
        }
    }
}
