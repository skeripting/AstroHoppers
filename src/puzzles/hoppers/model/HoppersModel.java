package puzzles.hoppers.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents an instance of the model for Hoppers.
 * @author the somosas (Kushal, Michael, Soban)
 */
public class HoppersModel {
    /** the collection of observers of this model */
    private final List<Observer<HoppersModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private HoppersConfig currentConfig;

    private int selectedFrogRow;
    private int selectedFrogCol;
    private int greenFrogs;

    private static int nRows;
    private static int nCols;

    /**
     * Gets the current config that's being used
     * @return the current config
     */
    public HoppersConfig getCurrentConfig(){
        nRows = currentConfig.getRows();
        nCols = currentConfig.getCols();
        greenFrogs = currentConfig.getNumGreen();
        return currentConfig;
    }

    /**
     * The view calls this to add itself as an observer.
     * @param observer the view
     */
    public void addObserver(Observer<HoppersModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void notifyObservers(String msg) {
        for (var observer : observers) {
            observer.update(this, msg);
        }
    }

    /**
     * Checks the to see if the position in the board
     * @param row the row of the position
     * @param col the col of the position
     * @return true if valid and false if not
     */
    private boolean isValidPosition(int row, int col){
        return row >= 0 && row < nRows && col >= 0 && col < nCols;
    }

    /**
     * Used to select a frog to move
     * @param row the row of the frog you want to select
     * @param col the col of the frog you want to select
     * @return the frog that's being selected
     */
    public boolean select(int row, int col){
        if(isValidPosition(row,col)) {
            selectedFrogRow = row;
            selectedFrogCol = col;
        }
        return this.currentConfig.selectFrog(row, col);
    }

    /**
     * gets the row of the selected frog
     * @return the row position
     */
    public int getSelectedFrogRow(){
        return selectedFrogRow;
    }

    /**
     * gets the col of the selected frog
     * @return the col position
     */
    public int getSelectedFrogCol() {
        return selectedFrogCol;
    }

    /**
     * Gets the number of green frogs
     * @return how many green frogs are in the board
     */
    public int getGreenFrogs() {
        return currentConfig.getNumGreen();
    }

    /**
     * Gets the contents of a certain position of the board
     * @param row the row being checked
     * @param col the col being checked
     * @return the contents of the cell being checked
     */
    public char getPosition(int row, int col){
        return currentConfig.getGrid(row, col);
    }


    /**
     * Lets the observer know that the game is ready
     */
    public void ready(){
        notifyObservers("Ready");
    }

    /**
     * sets the config the model is using
     * @param config
     */
    public void setCurrentConfig(HoppersConfig config){
        currentConfig = config;
    }

    /**
     * Constructor for the model
     * @param filename the file being used in the model
     * @throws IOException
     */
    public HoppersModel(String filename) throws IOException {
        setCurrentConfig(new HoppersConfig(filename));
    }
}