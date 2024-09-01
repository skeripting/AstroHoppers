package puzzles.hoppers.gui;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.hoppers.model.HoppersConfig;
import puzzles.hoppers.model.HoppersModel;
import javafx.scene.control.Label;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Represents the graphical user interface for the Hoppers puzzle game.
 * Extends the JavaFX Application class and implements the Observer interface.
 *
 * @author the somosas (Kushal, Michael, Soban)
 */

public class HoppersGUI extends Application implements Observer<HoppersModel, String> {
    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";
    private static final char GREEN_FROG = 'G';
    private static final char RED_FROG = 'R';
    private static final char EMPTY_SPACE = '.';
    private static final char INVALID_SPACE = '*';

    // for demonstration purposes
    private Image redFrog = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"red_frog.png"));
    private Image greenFrog = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"green_frog.png"));
    private Image water = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"water.png"));
    private Image lilyPad = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"lily_pad.png"));

    /** The size of all icons, in square dimension */
    private final static int ICON_SIZE = 75;

    private HoppersModel model;
    private Label topLabel;
    private GridPane gameGridPane = new GridPane();
    private Button[][] buttons;
    private String filename;
    private boolean nextMove = false;

    /**
     * Initializes the application.
     * Parses command-line arguments to obtain the filename.
     */
    public void init() {
        filename = getParameters().getRaw().getFirst();
        System.out.println(filename);
        nextMove = false;
        try{
            model = new HoppersModel(filename);
            model.addObserver(this);
            buttons = new Button[model.getCurrentConfig().getRows()][model.getCurrentConfig().getCols()];
        }
        catch(IOException e){
            System.out.println("File not found");
        }
    }

    /**
     * Starts the JavaFX application.
     *
     * @param stage The primary stage for the JavaFX application.
     */
    @Override
    public void start(Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();
        FlowPane flowPane = new FlowPane();
        GridPane gridPane = new GridPane();
        //GridPane gameGridPane = new GridPane();

        flowPane.setAlignment(Pos.CENTER);

        borderPane.setTop(flowPane);
        borderPane.setCenter(gameGridPane);
        borderPane.setBottom(gridPane);

        labelsGUI(flowPane, "");
        boardGUI(gameGridPane);
        bottomButtons(gridPane);

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.setTitle("HoppersGUI");
        stage.show();
    }


    /**
     * This method sets up the top portion of the GUI. It contains the moves, status, score, and best score label.
     *
     * @param flowPane placed at the top of the GUI - it holds the values of
     * @param msg the message to be displayed on the status label
     */
    public void labelsGUI(FlowPane flowPane, String msg){
        this.topLabel = new Label(msg);
        flowPane.getChildren().add(this.topLabel);
    }

    /**
     * Sets up the game board GUI.
     *
     * @param gridPane The GridPane representing the game board.
     */
    public void boardGUI(GridPane gridPane){
        for(int row = 0; row < model.getCurrentConfig().getRows(); row++){
            for(int col = 0; col < model.getCurrentConfig().getCols(); col++){
                char current = model.getPosition(row, col);
                Button button = new Button();
                switch(current){
                    case RED_FROG -> button.setGraphic(new ImageView(redFrog));
                    case GREEN_FROG -> button.setGraphic(new ImageView(greenFrog));
                    case INVALID_SPACE -> button.setGraphic(new ImageView(water));
                    case EMPTY_SPACE -> button.setGraphic(new ImageView(lilyPad));
                }
                button.setMinSize(ICON_SIZE, ICON_SIZE);
                button.setMaxSize(ICON_SIZE, ICON_SIZE);

                int tempRow = row;
                int tempCol = col;
                button.setOnAction(event -> buttonPressed(tempRow, tempCol));

                buttons[row][col] = button;
                gridPane.add(button, col, row);
            }
        }
    }

    /**
     * Handles button press events/movement of the frogs. Works like the select in the PTUI.
     * This method is called when a button representing a cell on the game board is pressed.
     * It determines the action to be taken based on the current game state and button press.
     *
     * @param row The row index of the pressed button.
     * @param col The column index of the pressed button.
     */
    private void buttonPressed(int row, int col) {
        if (!nextMove) {
            boolean isSelected = model.select(row, col);
            if(!isSelected){
                update(model, "No frog at (" + row + ", " + col + ")");
            }
            else{
                nextMove = true;
                update(model, "Selected (" + row + ", " + col + ")");
            }
        }
        else {
            boolean validMove = false;
            int moveRow = row - model.getSelectedFrogRow();
            int moveCol = col - model.getSelectedFrogCol();

            int absMoveRow = Math.abs(moveRow);
            int absMoveCol = Math.abs(moveCol);

            int tempRow = model.getSelectedFrogRow();
            int tempCol = model.getSelectedFrogCol();

            if ((absMoveRow == 2 && absMoveCol == 2)||
                    (absMoveRow == 0 && absMoveCol == 4) || (absMoveRow == 4 && absMoveCol == 0)) {
                validMove = true;
            }

            if(validMove) {
                int newRow = model.getSelectedFrogRow() + moveRow;
                int newCol = model.getSelectedFrogCol() + moveCol;

                int jumpedRow = (tempRow + newRow) / 2;
                int jumpedCol = (tempCol + newCol) / 2;

                if (model.getCurrentConfig().isValidMove(newRow, newCol) && model.getPosition(newRow, newCol) == EMPTY_SPACE
                        && model.getPosition(jumpedRow, jumpedCol) == GREEN_FROG) {

                    model.setCurrentConfig(new HoppersConfig(model.getCurrentConfig(), model.getSelectedFrogRow(),
                            model.getSelectedFrogCol(), newRow, newCol));
                    update(model, "Jumped from (" + tempRow + ", " + tempCol + ")  to " +
                            "(" + row + ", " + col + ")");
                } else {
                    update(model, "Can't jump from (" + tempRow + ", " + tempCol + ")  to (" + newRow + ", " + newCol + ")");
                }
            }
            else {
                update(model, "Can't jump from (" + tempRow + ", " + tempCol + ")  to (" + (model.getSelectedFrogRow() + moveRow) + ", " + (model.getSelectedFrogCol()+moveCol) + ")");
            }

            nextMove = false;
        }
    }

    /**
     * Updates the GUI in response to changes in the model.
     * This method is called whenever the game state changes.
     * It updates the GUI components to reflect the current state of the game.
     *
     * @param model The model to observe.
     * @param msg   The message to display in the GUI.
     */
    @Override
    public void update(HoppersModel model, String msg){
        if(msg != null && !msg.isEmpty())
            topLabel.setText(msg);

        Button button = new Button();

        for (int row = 0; row < model.getCurrentConfig().getRows(); row++) {
            for (int col = 0; col < model.getCurrentConfig().getCols(); col++) {
                button = buttons[row][col];
                char current = model.getPosition(row, col);
                switch (current) {
                    case RED_FROG -> button.setGraphic(new ImageView(redFrog));
                    case GREEN_FROG -> button.setGraphic(new ImageView(greenFrog));
                    case INVALID_SPACE -> button.setGraphic(new ImageView(water));
                    case EMPTY_SPACE -> button.setGraphic(new ImageView(lilyPad));
                }
            }
        }
    }


    /**
     * Sets up the buttons at the bottom of the GUI.
     * This method creates buttons for actions such as loading a new game, requesting hints,
     * and resetting the game board.
     *
     * @param gridPane The GridPane at the bottom of the GUI.
     */
    public void bottomButtons(GridPane gridPane){
        Button load = new Button("Load");
        Button hint = new Button("Hint");
        Button reset = new Button("Reset");

        load.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select file");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            File file = fileChooser.showOpenDialog(gridPane.getScene().getWindow());
            filename = file.getName();;
            loadNewGame();
        });

        hint.setOnAction(event -> {
            List<Configuration> solution = Solver.solve(model.getCurrentConfig());
            if(solution != null && solution.size() > 1){

                update(model,"Next step!");
                model.setCurrentConfig((HoppersConfig) solution.get(1));
            }
            else if(model.getGreenFrogs() == 0){
                update(model, "Already solved!");
            }
            else{
                update(model, "No solution");
            }
        });

        reset.setOnAction(event -> {
            update(model, "Puzzle reset! ");
            resetBoard();
        });

        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(load, 0, 0);
        gridPane.add(hint, 1, 0);
        gridPane.add(reset, 2, 0);
    }


    /**
     * Resets the game board to its initial state.
     * This method reloads the game board from the original file and updates the GUI accordingly.
     */
    public void resetBoard(){
        try{
            model = new HoppersModel(filename);
            model.addObserver(this);
            boardGUI(gameGridPane);
        }
        catch (IOException e){
            System.out.println("Error resetting board");
        }
    }

    /**
     * Loads a new game from a file.
     * This method prompts the user to select a game file using a file chooser dialog.
     * It then loads the selected game file, updates the model, and refreshes the GUI.
     */
    private void loadNewGame() {
        try {
            model = new HoppersModel(filename);
            model.addObserver(this);
            gameGridPane.getChildren().clear();
            buttons = new Button[model.getCurrentConfig().getRows()][model.getCurrentConfig().getCols()];
            boardGUI(gameGridPane);
            update(model, "loaded: " + filename);
            gameGridPane.getScene().getWindow().sizeToScene();
        } catch (IOException e) {
            System.out.println("Error loading new game");
        }

    }


    /**
     * The main method to launch the application.
     * It checks if a filename argument is provided and launches the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            Application.launch(args);
        }
    }
}