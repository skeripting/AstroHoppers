package puzzles.hoppers.model;

import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


/**
 * Represents a configuration of the Hoppers puzzle.
 * Implements the Configuration interface required by the common solver.
 *
 * @author the samosas
 */


public class HoppersConfig implements Configuration{
    private static final char GREEN_FROG = 'G';
    private static final char RED_FROG = 'R';
    private static final char EMPTY_SPACE = '.';
    private static final char INVALID_SPACE = '*';

    private static int nRows;
    private static int nCols;
    private char [][] grid;

    private int numGreen;
    private char selectedFrog;

    private static final int[][] MOVES_EVEN = new int[][]{{-2, -2}, {-2, 2}, {2, -2}, {2, 2}, {4, 0}, {-4, 0}, {0, -4},
            {0, 4}};
    private static final int[][] MOVES_ODD = new int[][]{{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};


    /**
     * Constructs a HoppersConfig object by reading from a file.
     *
     * @param filename the name of the file containing the initial configuration
     * @throws IOException if an I/O error occurs while reading the file
     */
    public HoppersConfig(String filename) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))){
            String[] lineOne = in.readLine().split("\\s+");
            nRows = Integer.parseInt(lineOne[0]);
            nCols = Integer.parseInt(lineOne[1]);
            grid = new char[nRows][nCols];

            for (int i = 0; i < nRows; i++) {
                String line = in.readLine();
                String[] chars = line.split("\\s+");
                for (int j = 0; j < nCols; j++) {
                    grid[i][j] = chars[j].charAt(0);
                    if (chars[j].charAt(0) == GREEN_FROG){
                        numGreen++;
                    }
                }
            }
        }
    }

    /**
     * Constructs a HoppersConfig object based on another HoppersConfig object and a move.
     *
     * @param other    the original HoppersConfig object
     * @param fromRow  the starting row of the move
     * @param fromCol  the starting column of the move
     * @param toRow    the ending row of the move
     * @param toCol    the ending column of the move
     */
    public HoppersConfig(HoppersConfig other, int fromRow, int fromCol, int toRow, int toCol){

        this.grid = new char[nRows][nCols];
        this.numGreen = other.getNumGreen();

        for (int row = 0; row < nRows; row++) {
            System.arraycopy(other.grid[row], 0, grid[row], 0, nCols);
        }

        selectFrog(fromRow, fromCol);

        if(isValidMove(toRow, toCol)) {
            this.grid[toRow][toCol] = selectedFrog;
            this.grid[fromRow][fromCol] = EMPTY_SPACE;


            int jumpedRow = (fromRow + toRow) / 2;
            int jumpedCol = (fromCol + toCol) / 2;


            if (grid[jumpedRow][jumpedCol] != EMPTY_SPACE && grid[jumpedRow][jumpedCol] != RED_FROG &&
                    grid[jumpedRow][jumpedCol] != INVALID_SPACE) {
                this.grid[jumpedRow][jumpedCol] = EMPTY_SPACE;
                numGreen--;
            }
        }
    }

    /**
     * This method ensures that movement is inbounds
     *
     * @param row current Row
     * @param col current Column
     * @return if movement happens inbounds
     */
    public boolean isValidMove(int row, int col){
        return row >= 0 && row < nRows && col >= 0 && col < nCols && grid[row][col] == EMPTY_SPACE;
    }


    /**
     * Checks if a position is usable (not an obstacle).
     *
     * @param row the row of the position
     * @param col the column of the position
     * @return true if the position is usable, false otherwise
     */
    public boolean isUsableSpot(int row, int col){
        return ((row + col) % 2 == 0);
    }


    /**
     * This method checks the current location for the amount of possible moves that can be made. In corners,
     * you can go diagonal, (left or right, up or down) depending on location. If the current position has two
     * even coordinates it can go in all 8 possible directions. If not corner or even coords it must be odd.
     *
     * @param row current Row
     * @param col current Col
     * @return number of moves that can be made from that spot
     */
    public int numPossibleMoves(int row, int col) {
        if (row % 2 == 0 && col % 2 == 0 && grid[row][col] != EMPTY_SPACE && grid[row][col] != INVALID_SPACE)
            return 8;
        else
            return 4;

    }

    /**
     * Selects a frog at the specified position.
     *
     * @param row the row of the frog
     * @param col the column of the frog
     * @return true if a frog is successfully selected, false otherwise
     */
    public boolean selectFrog(int row, int col){
        if(col < 0 || row < 0 || row >= nRows ||col >= nCols || grid[row][col] == EMPTY_SPACE || grid[row][col] == INVALID_SPACE)
            return false;

        selectedFrog = grid[row][col];
        return true;
    }


    /**
     * Gets the number of green frogs in the configuration.
     *
     * @return the number of green frogs
     */
    public int getNumGreen() {
        return numGreen;
    }

    /**
     * Gets the number of rows in the grid.
     *
     * @return the number of rows
     */
    public int getRows(){
        return nRows;
    }

    /**
     * Gets the number of columns in the grid.
     *
     * @return the number of columns
     */
    public int getCols() {
        return nCols;
    }


    /**
     * Prints the current configuration of the board.
     */
    public void printBoard(){
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println(numGreen);
    }

    /**
     * Gets the character at the specified position in the grid.
     *
     * @param row the row of the position
     * @param col the column of the position
     * @return the character at the specified position
     */
    public char getGrid(int row, int col){
        return grid[row][col];
    }

    /**
     * Checks if the current configuration is a solution.
     *
     * @return true if the configuration is a solution, false otherwise
     */
    @Override
    public boolean isSolution() {
        return numGreen == 0;
    }


    /**
     * Generates all possible neighboring configurations from the specified position.
     * Neighbors are generated based on valid moves from the given position.
     *
     * @param row the row of the position
     * @param col the column of the position
     * @return a collection of neighboring configurations
     */
    public Collection<Configuration> generateNeighbors(int row, int col){
        Collection<Configuration> neighbors = new ArrayList<>();
        int newRow;
        int newCol;

        int[][] moves;
        int numMoves = numPossibleMoves(row, col);

        if(numMoves == 4)
            moves = MOVES_ODD;
        else if (numMoves == 8)
            moves = MOVES_EVEN;
        else
            moves = new int[0][];


        for(int[] move : moves){
            newRow = row + move[0];
            newCol = col + move[1];

            int jumpedRow = (row+ newRow) / 2;
            int jumpedCol = (col + newCol) / 2;

            if(isValidMove(newRow, newCol) && grid[newRow][newCol] == EMPTY_SPACE &&
                    grid[jumpedRow][jumpedCol] != RED_FROG && grid[jumpedRow][jumpedCol] == GREEN_FROG){

                if (Math.abs(row - newRow) == 2 && Math.abs(col - newCol) == 2) {
                    neighbors.add(new HoppersConfig(this, row, col, newRow, newCol));
                }
                if (row == newRow && Math.abs(col - newCol) == 4) {
                    neighbors.add(new HoppersConfig(this, row, col, newRow, newCol));
                }
                if (Math.abs(row - newRow) == 4 && col == newCol) {
                    neighbors.add(new HoppersConfig(this, row, col, newRow, newCol));
                }
            }
        }
        return neighbors;
    }


    /**
     * Gets all possible neighboring configurations for the current configuration.
     * Neighbors are generated based on valid moves of frogs in the grid.
     *
     * @return a collection of neighboring configurations
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> neighbors = new ArrayList<>();

        for(int row = 0; row < nRows; row++){
            for(int col = 0; col < nCols; col++){
                if(grid[row][col] == RED_FROG || grid[row][col] == GREEN_FROG){
                    selectedFrog = grid[row][col];
                    Collection<Configuration> allNeighbors = generateNeighbors(row, col);
                    neighbors.addAll(allNeighbors);
                }
            }
        }
        return neighbors;
    }



    /**
     * Indicates whether some other object is "equal to" this one.
     * This method compares two HoppersConfig objects based on their grid configurations.
     *
     * @param other the reference object with which to compare
     * @return true if this object is the same as the other object, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof HoppersConfig){
            HoppersConfig otherConfig = (HoppersConfig) other;
            result = Arrays.deepEquals(this.grid, otherConfig.grid);
        }
        return result;
    }

    /**
     * Returns a hash code value for the object.
     * This method generates a hash code based on the grid configuration of the HoppersConfig object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() { return Arrays.deepHashCode(this.grid); }


    /**
     * Returns a string representation of the object.
     * This method generates a string representation of the grid configuration.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                sb.append(grid[i][j]).append( " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}