package puzzles.astro.gui;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import puzzles.astro.model.*;
import puzzles.common.Direction;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.hoppers.model.HoppersConfig;
import puzzles.hoppers.model.HoppersModel;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Represents the graphical user interface for the Astro puzzle game.
 * Extends the JavaFX Application class and implements the Observer interface.
 *
 * @author the somosas (Kushal, Michael, Soban)
 */
public class AstroGUI extends Application implements Observer<AstroModel, String> {
    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";

    private Label statusLabel = new Label();
    private Image robot = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"robot-blue.png"));
    private Image astronautImage = new Image(getClass().getResourceAsStream(RESOURCES_DIR + "astro.png"));
    private Image goalImage = new Image(getClass().getResourceAsStream(RESOURCES_DIR + "earth.png"));
    private BackgroundImage backgroundImage = new BackgroundImage( new Image( getClass().getResource("resources/space.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
    private Background background = new Background(backgroundImage);
    private BorderPane arrowBorderPane = new BorderPane();
    private FlowPane rightFlowPane = new FlowPane();
    private Button[][] buttons;
    private GridPane gridPane = new GridPane();
    private HBox menuButtons;

    private static final int WINDOW_PADDING = 7;
    /** The size of all icons, in square dimension */
    private final static int ICON_SIZE = 75;

    private Label[][] board;
    private Label fileLabel;
    private static int Ncols;
    private static int Nrows;
    private AstroModel model;

    private static String filename;

    /**
     * Loads a new game from a file.
     * This method prompts the user to select a game file using a file chooser dialog.
     * It then loads the selected game file, updates the model, and refreshes the GUI.
     */
    private void loadNewGame(String fName) {
        filename = fName;
        try{
            System.out.println("1");
            this.model = new AstroModel(filename);
            this.buttons = new Button[model.getCurrentConfig().getNColumns()][model.getCurrentConfig().getNRows()];
            this.model.addObserver(this);
            this.gridPane.getChildren().clear();
            setupBoardGUI();
            if (this.gridPane.getScene() != null) {
                this.gridPane.getScene().getWindow().sizeToScene();
            }
            update(this.model, "Loaded: " + filename);
        }
        catch(IOException e){
            System.out.println("File not found");
        }
    }

    /**
     * Load a new game.
     * @throws IOException
     */
    public void init() throws IOException {
        loadNewGame(getParameters().getRaw().get(0));
    }

    /**
     * This method handles updating the MVC once the user requests
     * movement for the selected entity.
     * @param dir The direction to move.
     */
    private void onMoveSelectedEntity(Direction dir) {
        if (model.getCurrentConfig().isSolution()) {
            update(model, "You already won!");
            return;
        }
        int oldSelectedEntityX = model.getCurrentConfig().getSelectedEntity().getPositionX();
        int oldSelectedEntityY = model.getCurrentConfig().getSelectedEntity().getPositionY();
        boolean success = model.getCurrentConfig().moveSelectedEntity(dir);
        int newSelectedEntityX = model.getCurrentConfig().getSelectedEntity().getPositionX();
        int newSelectedEntityY = model.getCurrentConfig().getSelectedEntity().getPositionY();

        if (success) {
            update(model, "Moved from (" + oldSelectedEntityX + ", " + oldSelectedEntityY + ") to (" + newSelectedEntityX + ", " + newSelectedEntityY + ")");
        }
        else {
            update(model, "Could not move (" + oldSelectedEntityX + ", " + oldSelectedEntityY + ")");
        }
    }

    /**
     * Set up the directional arrows that allow the user
     * to move.
     */
    private void setupDirectionalArrows() {
        Button upArrowButton = new Button("↑");
        Button leftArrowButton = new Button("←");
        Button rightArrowButton = new Button("→");
        Button downArrowButton = new Button("↓");

        VBox topContainer = new VBox();
        topContainer.setAlignment(Pos.CENTER);
        topContainer.getChildren().add(upArrowButton);

        VBox bottomContainer = new VBox();
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.getChildren().add(downArrowButton);

        HBox leftContainer = new HBox();
        leftContainer.setAlignment(Pos.CENTER);
        leftContainer.getChildren().add(leftArrowButton);

        HBox rightContainer = new HBox();
        rightContainer.setAlignment(Pos.CENTER);
        rightContainer.getChildren().add(rightArrowButton);

        arrowBorderPane.setTop(topContainer);
        arrowBorderPane.setBottom(bottomContainer);
        arrowBorderPane.setLeft(leftContainer);
        arrowBorderPane.setRight(rightContainer);

        leftArrowButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                onMoveSelectedEntity(Direction.WEST);
            }

        });
        rightArrowButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                onMoveSelectedEntity(Direction.EAST);
            }
        });
        upArrowButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                onMoveSelectedEntity(Direction.NORTH);
            }
        });
        downArrowButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                onMoveSelectedEntity(Direction.SOUTH);
            }
        });

        rightFlowPane.setOrientation(Orientation.VERTICAL);
        rightFlowPane.setVgap(20);
        rightFlowPane.setAlignment(Pos.CENTER_RIGHT);
        rightFlowPane.getChildren().add(arrowBorderPane);
    }

    /**
     * Update the model once an Entity has been selected.
     * @param x The x position
     * @param y The y position
     */
    private void buttonSelected(int x, int y) {
        boolean success = model.getCurrentConfig().selectEntity(x, y);
        if (success) {
            update(model, "Selected (" + x + ", " + y + ")");
        }
        else {
            update(model, "No piece at (" + x + ", " + y + ")");
        }

    }

    /**
     * Setup the board GUI for the game.
     */
    public void setupBoardGUI(){
        Goal goal = model.getCurrentConfig().getGoal();
        for(int row = 0; row < model.getCurrentConfig().getRows(); row++){
            for(int col = 0; col < model.getCurrentConfig().getCols(); col++){
                Entity current = model.getCurrentConfig().getGrid()[col][row];
                Button button = new Button();

                button.setBackground(background);

                if (goal.getPositionX() == col && goal.getPositionY() == row) {
                    button.setGraphic(new ImageView(goalImage));
                }
                else if (current instanceof Astronaut) {
                    button.setGraphic(new ImageView(astronautImage));
                }
                else if (current instanceof Robot) {
                    button.setGraphic(new ImageView(robot));
                }

                button.setMinSize(ICON_SIZE, ICON_SIZE);
                button.setMaxSize(ICON_SIZE, ICON_SIZE);

                int tempRow = row;
                int tempCol = col;

                button.setOnAction(event -> buttonSelected(tempCol, tempRow));

                buttons[col][row] = button;
                gridPane.add(button, col, row);
            }
        }
    }

    /**
     * Get a Configuration hint for the game.
     * @return A Configuration object representing a hint.
     */
    public Configuration getHint() {
        List<Configuration> solution = Solver.solve(this.model.getCurrentConfig());
        if (solution != null) {
            if (solution.size() == 1) {
                return solution.get(0);
            }
            return solution.get(1);
        }
        return null;
    }

    /**
     * Set up the load/reset/hint buttons.
     * @param sceneBorderPane The BorderPane object representing the scene.
     */
    private void setupMenuButtons(BorderPane sceneBorderPane) {
        menuButtons = new HBox();
        menuButtons.setAlignment(Pos.CENTER);

        Button load = new Button("Load");
        Button reset = new Button("Reset");
        Button hint = new Button("Hint");

        menuButtons.getChildren().add(load);
        menuButtons.getChildren().add(reset);
        menuButtons.getChildren().add(hint);

        sceneBorderPane.setBottom(menuButtons);

        hint.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (model.getCurrentConfig().isSolution()) {
                    update(model, "You already won!");
                }
                else {
                    AstroConfig nextConfig = (AstroConfig) getHint();
                    if (nextConfig != null) {
                        model.setCurrentConfig(nextConfig);
                        update(model,"Next step!");
                    } else {
                        update(model, "No solution!");
                    }
                }
            }}
        );
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loadNewGame(filename);
            }}
        );
        load.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select file");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                File file = fileChooser.showOpenDialog(gridPane.getScene().getWindow());
                filename = file.getName();;
                loadNewGame(filename);
            }

        });
    }

    /**
     * Start the application.
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        BorderPane sceneBorderPane = new BorderPane();
        BorderPane.setAlignment(statusLabel, Pos.CENTER);
        statusLabel.setAlignment(Pos.CENTER);

        gridPane.setBackground(background);
        sceneBorderPane.setCenter(gridPane);
        sceneBorderPane.setTop(statusLabel);

        sceneBorderPane.setPadding(new Insets(WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING));
        sceneBorderPane.setRight(rightFlowPane);

        setupDirectionalArrows();
        setupBoardGUI();
        setupMenuButtons(sceneBorderPane);

        Scene scene = new Scene(sceneBorderPane);
        stage.setScene(scene);
        stage.setTitle("AstroGUI");
        stage.show();
    }

    /**
     * A method to update the astro model and change the info
     * string to a message.
     * @param astroModel The model to update
     * @param msg The message that is sent.
     *
     */
    @Override
    public void update(AstroModel astroModel, String msg) {
        if(msg != null && !msg.isEmpty())
            statusLabel.setText(msg);
        Button button = new Button();
        Goal goal = model.getCurrentConfig().getGoal();
        for (int row = 0; row < model.getCurrentConfig().getRows(); row++) {
            for (int col = 0; col < model.getCurrentConfig().getCols(); col++) {
                button = buttons[col][row];
                if (button != null) {
                    Entity current = model.getCurrentConfig().getGrid()[col][row];
                    if (goal.getPositionX() == col && goal.getPositionY() == row) {
                        button.setGraphic(new ImageView(goalImage));
                    }
                    else if (current instanceof Astronaut) {
                        button.setGraphic(new ImageView(astronautImage));
                    }
                    else if (current instanceof Robot) {
                        button.setGraphic(new ImageView(robot));
                    }
                    else {
                        button.setGraphic(null);
                    }
                }

            }
        }
    }

    /**
     * The main method to launch the application.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java AstroGUI filename");
        } else {
            Application.launch(args);
        }
    }
}
