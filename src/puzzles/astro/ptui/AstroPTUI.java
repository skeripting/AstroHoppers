package puzzles.astro.ptui;

import puzzles.astro.model.AstroConfig;
import puzzles.astro.model.Entity;
import puzzles.common.Direction;
import puzzles.common.Observer;
import puzzles.astro.model.AstroModel;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a plain text UI version of the Astro game.
 *
 * @author the somosas (Kushal, Michael, Soban)
 */
public class AstroPTUI implements Observer<AstroModel, String> {
    private AstroModel model;
    private static String fileName;

    private static final String INVALID_MSG = "Invalid user input";

    /**
     * Print the board
     */
    public void printBoard() {
        System.out.println(toString());
    }

    /**
     * Get the solution of the current config
     * @return The solution
     */
    public List<Configuration> getSolution() {
        return Solver.solve(this.model.getCurrentConfig());
    }

    /**
     * Get a hint for the game.
     * @return A Configuration that represents the next step.
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
     * Turn the PTUI into a string.
     * @return A string that shows the grid.
     */
    public String toString() {
        int nRows = model.getCurrentConfig().getNRows();
        int nColumns = model.getCurrentConfig().getNColumns();
        StringBuilder result = new StringBuilder(" ");
        result.append(System.lineSeparator());
        // displaying columns numbers
        result.append("  ");
        for (int col = 0; col < nColumns; col++) {
            result.append(String.format("%2" + "d", col));
        }
        result.append(System.lineSeparator());
        result.append("  ");
        for (int col = 0; col < nColumns; col++) {
            result.append("-".repeat(2));
        }
        result.append(System.lineSeparator());

        for (int row = 0; row < nRows; ++row) {
            result.append(String.format("%2d|", row));
            for (int col = 0; col < nColumns; ++col) {
                Entity current = this.model.getCurrentConfig().getGrid()[col][row];
                if (current != null) {
                    result.append(current.getName() + " ");
                }
                else {
                    result.append(". ");
                }
            }
            result.append(System.lineSeparator());
        }
        return result.toString();
    }

    /**
     * Initialize the PTUI
     * @param filename The file to load from
     * @throws IOException
     */
    public void init(String filename) throws IOException {
        this.model = new AstroModel(filename);
        this.model.setCurrentConfig(new AstroConfig(filename));
        this.model.addObserver(this);
        System.out.println("Loaded: " + filename);
        printBoard();
        this.model.ready();
        displayHelp();
    }

    /**
     * Update the PTUI
     * @param model the object that wishes to inform this object
     *                about something that has happened.
     * @param data optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(AstroModel model, String data) {
        // for demonstration purposes
        System.out.println(data);
        System.out.println(model);
    }

    /**
     * Display the help commands.
     */
    private void displayHelp() {
        System.out.println( "h(int)              -- hint next move" );
        System.out.println( "l(oad) filename     -- load new puzzle file" );
        System.out.println( "s(elect) r c        -- select cell at r, c" );
        System.out.println( "m(ove) n|s|e|w      -- move selected piece in direction" );
        System.out.println( "q(uit)              -- quit the game" );
        System.out.println( "r(eset)             -- reset the current game" );
    }

    /**
     * Run the PTUI.
     */
    public void run() {
        Scanner in = new Scanner( System.in );
        for ( ; ; ) {
            System.out.print( "> " );
            String line = in.nextLine();
            String[] words = line.split( "\\s+" );
            if (words.length > 0) {
                if (words[0].startsWith( "q" )) {
                    break;
                }
                else if (words[0].startsWith("s")) {
                    String selectX = words[1];
                    String selectY = words[2];
                    boolean selectResult = model.select(Integer.parseInt(selectX), Integer.parseInt(selectY));
                    if (!selectResult) {
                        System.out.println("No piece at (" + selectX + ", " + selectY + ")");
                    }
                    else {
                        System.out.println("Selected: (" + selectX + ", " + selectY + ")");
                    }
                }else if ( words[0].startsWith("m")) {
                    AstroConfig currentConfig = model.getCurrentConfig();
                    if (words[1].startsWith("n")) {
                        currentConfig.moveSelectedEntity(Direction.NORTH);
                    } else if (words[1].startsWith("s")) {
                        currentConfig.moveSelectedEntity(Direction.SOUTH);
                    } else if (words[1].startsWith("e")) {
                        currentConfig.moveSelectedEntity(Direction.EAST);
                    } else if (words[1].startsWith("w")) {
                        currentConfig.moveSelectedEntity(Direction.WEST);
                    } else {
                        System.out.println(INVALID_MSG);
                        displayHelp();
                    }
                    printBoard();
                }else if (words[0].startsWith("l")) {
                    String fileName = words[1];
                    try {
                        init(fileName);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else if (words[0].startsWith("r")){
                    try {
                        init(fileName);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else if (words[0].startsWith("h")) {
                    System.out.println("Next step!");
                    AstroConfig nextConfig = (AstroConfig) getHint();
                    if (nextConfig != null) {
                        model.setCurrentConfig(nextConfig);
                        printBoard();
                    }
                    else {
                        System.out.println("No solution!");
                    }


                }else {
                    displayHelp();
                }
            }
        }
    }

    /**
     * The main method for the AstroPTUI class.
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java AstroPTUI filename");
        } else {
            try {
                AstroPTUI ptui = new AstroPTUI();
                fileName = args[0];
                ptui.init(args[0]);
                ptui.run();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }
}
