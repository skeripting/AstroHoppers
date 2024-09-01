package puzzles.hoppers.ptui;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.hoppers.model.HoppersConfig;
import puzzles.hoppers.model.HoppersModel;

import java.io.IOException;
import java.util.*;

public class HoppersPTUI implements Observer<HoppersModel, String> {
    private HoppersModel model;
    private static String filename;

    private boolean gameDone = false;
    private boolean nextMove = false;


    /**
     * Initializes the view that model uses
     * @param filename
     * @throws IOException
     */
    public void init(String filename) throws IOException {
        gameDone = false;
        nextMove = false;
        this.model = new HoppersModel(filename);
        this.model.addObserver(this);
        System.out.println("Loaded: " + filename);
        System.out.println(this);
        this.model.ready();
    }

    /**
     *
     * @param model the object that wishes to inform this object
     *                about something that has happened.
     * @param data optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(HoppersModel model, String data) {
        // for demonstration purposes
        //System.out.println(data);
        //System.out.println(model);
    }

    /**
     * Gets the PTUI representation of the board
     * @return returns the PTUI
     */
    @Override
    public String toString() {
        HoppersConfig config = model.getCurrentConfig();
        int rows = config.getRows();
        int cols = config.getCols();
        StringBuilder result = new StringBuilder();

        // displaying columns numbers
        result.append("  ");
        for (int col = 0; col < cols; col++) {
            result.append(String.format("%2d", col));
        }
        result.append(System.lineSeparator());

        result.append("  ");
        for (int col = 0; col < cols; col++) {
            result.append("--");
        }
        result.append(System.lineSeparator());

        // Displaying grid contents
        for (int row = 0; row < rows; ++row) {
            result.append(String.format("%d|", row));
            for (int col = 0; col < cols; ++col) {
                char current = config.getGrid(row, col);
                result.append(" ").append(current);
            }
            result.append(System.lineSeparator());
        }

        return result.toString();
    }


    /**
     * displays all the commands
     */
    private void displayHelp() {
        System.out.println( "h(int)              -- hint next move" );
        System.out.println( "l(oad) filename     -- load new puzzle file" );
        System.out.println( "s(elect) r c        -- select cell at r, c" );
        System.out.println( "q(uit)              -- quit the game" );
        System.out.println( "r(eset)             -- reset the current game" );
    }

    /**
     * the mainloop for the PTUI
     * @throws IOException
     */
    public void run() throws IOException {
        displayHelp();
        Scanner in = new Scanner( System.in );
        for ( ; ; ) {
            System.out.print( "> " );
            String line = in.nextLine().trim();
            String[] words = line.split( "\\s+" );

            if (words.length > 0) {

                if (words[0].startsWith( "q" )) {
                    break;
                }
                else if (words[0].startsWith("s")) {
                    if(words.length == 3) {
                        int selectRow = Integer.parseInt(words[1]);
                        int selectCol = Integer.parseInt(words[2]);
                        if (!nextMove) {
                            boolean isSelected = model.select(selectRow, selectCol);
                            if (!isSelected) {
                                System.out.println("No frog at (" + selectRow + ", " + selectCol + ")");
                            } else {
                                System.out.println("Selected (" + selectRow + ", " + selectCol + ")");
                                nextMove = true;
                            }
                        } else {
                            int moveRow = selectRow - model.getSelectedFrogRow();
                            int moveCol = selectCol - model.getSelectedFrogCol();
                            int newRow = model.getSelectedFrogRow() + moveRow;
                            int newCol = model.getSelectedFrogCol() + moveCol;

                            int tempRow = model.getSelectedFrogRow();
                            int tempCol = model.getSelectedFrogCol();

                            int jumpedRow = (tempRow + newRow) / 2;
                            int jumpedCol = (tempCol + newCol) / 2;

                            if(model.getCurrentConfig().isValidMove(newRow, newCol) && model.getPosition(newRow, newCol) == '.'
                                    && model.getPosition(jumpedRow, jumpedCol) == 'G') {
                                model.setCurrentConfig(new HoppersConfig(model.getCurrentConfig(), model.getSelectedFrogRow(),
                                        model.getSelectedFrogCol(), newRow, newCol));
                                System.out.println("Jumped from (" + tempRow + ", " + tempCol + ")  to " +
                                        "(" + selectRow + ", " + selectCol + ")");
                            }
                            else {
                                System.out.println("Can't jump from ("+tempRow+", "+tempCol+")  to ("+newRow+", "+newCol+")");
                            }
                            nextMove = false;
                            gameDone = model.getGreenFrogs() == 0;
                        }
                        System.out.println(this);
                    }
                }else if (words[0].startsWith("l")) {
                    filename = words[1];
                    try {
                        init(filename);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else if(words[0].startsWith("r")){
                    try {
                        init(filename);
                        System.out.println("Puzzle reset!");
                        System.out.println(this);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (words[0].startsWith("h"))
                {
                    List<Configuration> solution = Solver.solve(model.getCurrentConfig());
                    if(solution != null && solution.size() > 1){

                        System.out.println("Next step!");
                        model.setCurrentConfig((HoppersConfig) solution.get(1));
                        System.out.println(this);
                    }
                    else{
                        System.out.println("No solution");
                    }
                } else {
                    displayHelp();
                }
            }
        }
    }

    /**
     * the main method
     * @param args the filename that's being used for the PTUI
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            try {
                HoppersPTUI ptui = new HoppersPTUI();
                filename = args[0];
                ptui.init(args[0]);
                ptui.run();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }
}