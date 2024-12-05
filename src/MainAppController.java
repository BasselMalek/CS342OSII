import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


public class MainAppController {
    @FXML
    private AnchorPane gridPanel;
    @FXML
    private TextField gridSizeInput;
    private Integer gridSize = 0;

    public void onReset(ActionEvent actionEvent) {
        gridPanel.getChildren().clear();
        return;
    }

    public void onGenerate(ActionEvent actionEvent) {
        this.gridSize = Integer.valueOf(gridSizeInput.getText());
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
                } else if (i == 0) {
                    cell.setFill(Color.TRANSPARENT);
                    cell.setStroke(Color.TRANSPARENT);
                    Text text = new Text (String.valueOf((char)('A'+j-1)));
                    StackPane stack = new StackPane();
                    stack.getChildren().addAll(cell, text);
                    grid.add(stack, j, i);
                } else if (j==0) {
                    cell.setFill(Color.TRANSPARENT);
                    cell.setStroke(Color.TRANSPARENT);
                    Text text = new Text (String.valueOf(i));
                    StackPane stack = new StackPane();
                    stack.getChildren().addAll(cell, text);
                    grid.add(stack, j, i);
                } else {
                    cell.setFill(Color.DARKGREY);
                    cell.setStroke(Color.BLACK);
                grid.add(cell, j, i);
                }
            }
        }
        AnchorPane.setTopAnchor(grid, 0.0);
        AnchorPane.setBottomAnchor(grid, 0.0);
        AnchorPane.setLeftAnchor(grid, 0.0);
        AnchorPane.setRightAnchor(grid, 0.0);
        gridPanel.getChildren().add(grid);
    }


    public void onRun(ActionEvent actionEvent) throws InterruptedException {
        GridPane grid = (GridPane) this.gridPanel.getChildren().getFirst();
        for (int i = 1; i<this.gridSize+1;i++){
            for (int j = 0; j < this.gridSize*2; j++) {
                if (grid.getChildren().get(i*this.gridSize+j).getClass() == Rectangle.class){
                Rectangle r = (Rectangle) grid.getChildren().get(i*this.gridSize+j);
                r.setFill(Color.LAVENDER);}
            }
        }
    }
}
