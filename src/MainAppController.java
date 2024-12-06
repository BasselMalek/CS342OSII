import com.rat.utils.NMaze;
import javafx.application.Platform;
import javafx.collections.ObservableArrayBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;


public class MainAppController {
    @FXML
    private AnchorPane gridPanel;
    @FXML
    private TextField gridSizeInput;
    private Integer gridSize = 0;
    private int[][] gridSpace;
    private Rectangle[][] cells;
    private NMaze maze;
    private Integer msStep = 500;
    private Boolean isRunning = true;
    private final BlockingQueue<ArrayList<Integer>> nodeQueue = new LinkedBlockingQueue<>();

    public void onReset(ActionEvent actionEvent) {
        gridPanel.getChildren().clear();
        return;
    }

    public void onGenerate(ActionEvent actionEvent) {
        this.gridSize = Integer.valueOf(gridSizeInput.getText());
        this.gridSpace = new int[this.gridSize][this.gridSize];
        this.cells = new Rectangle[this.gridSize][this.gridSize];
        if (this.gridSize <= 0) {
            System.out.println("Grid cannot be 0x0.");
            return;
        }
        if (!gridPanel.getChildren().isEmpty()) {
            System.out.println("Grid must be cleared before generating a new one.");
            return;
        }
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(3);
        grid.setVgap(3);
        for (int i = 0; i < this.gridSize + 1; i++) {
            for (int j = 0; j < this.gridSize + 1; j++) {
                Rectangle cell = new Rectangle(35, 35);
                //Chess like numbering so you don't get dizzy looking at grids that are n>4.
                if ((i == 0 && j == 0)) {
                    cell.setFill(Color.TRANSPARENT);
                    cell.setStroke(Color.TRANSPARENT);
                    grid.add(cell, j, i);
                } else if (i == 0) {
                    cell.setFill(Color.TRANSPARENT);
                    cell.setStroke(Color.TRANSPARENT);
                    Text text = new Text(String.valueOf((char) ('A' + j - 1)));
                    StackPane stack = new StackPane();
                    stack.getChildren().addAll(cell, text);
                    grid.add(stack, j, i);
                } else if (j == 0) {
                    cell.setFill(Color.TRANSPARENT);
                    cell.setStroke(Color.TRANSPARENT);
                    Text text = new Text(String.valueOf(i));
                    StackPane stack = new StackPane();
                    stack.getChildren().addAll(cell, text);
                    grid.add(stack, j, i);
                } else {
                    cell.setFill(Color.GAINSBORO);
                    cell.setStroke(Color.BLACK);
                    int finalJ = j - 1;
                    int finalI = i - 1;
                    cell.setOnMouseClicked((event) -> {
                        if (this.gridSpace[finalJ][finalI] == 0) {
                            this.gridSpace[finalJ][finalI] = 1;
                            cell.setFill(Color.DARKGREY);
                        } else {
                            this.gridSpace[finalJ][finalI] = 0;
                            cell.setFill(Color.GAINSBORO);
                        }
                    });
                    grid.add(cell, j, i);
                    this.cells[j - 1][i - 1] = cell;
                }
            }
        }
        AnchorPane.setTopAnchor(grid, 0.0);
        AnchorPane.setBottomAnchor(grid, 0.0);
        AnchorPane.setLeftAnchor(grid, 0.0);
        AnchorPane.setRightAnchor(grid, 0.0);
        this.gridPanel.getChildren().add(grid);
    }


    public void onRun(ActionEvent actionEvent) throws InterruptedException {
        for (int i = 0; i < this.gridSize; i++) {
            for (int j = 0; j < this.gridSize; j++) {
                this.gridSpace[j][i] = this.gridSpace[j][i] == 1 ? 0 : 1;
            }
        }
        this.maze = new NMaze(this.gridSize, this.gridSpace);
        this.maze.setRealTime(true);
        this.maze.setRealTimeParams(this.msStep, this.isRunning, this.cells);
        ExecutorService solverThread = Executors.newSingleThreadExecutor();
        solverThread.execute(() -> {
            try {
                System.out.println("I'm solving");
                System.out.println(this.maze.solve());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        solverThread.shutdown();}
}

