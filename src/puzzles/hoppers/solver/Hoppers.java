package puzzles.hoppers.solver;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.hoppers.model.HoppersConfig;

import java.io.IOException;
import java.util.List;

/**
 * The main class for solving Hoppers puzzle instances.
 * It reads a Hoppers puzzle configuration from a file, solves the puzzle,
 * and prints the solution steps to the console.
 *
 * @author the samosas
 */
public class Hoppers {

    /**
     * Main method to run the Hoppers puzzle solver.
     *
     * @param args Command-line arguments. Expects a single argument - the filename
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Hoppers filename");
        }
        else{
            try{
                HoppersConfig hoppersConfig = new HoppersConfig(args[0]);

                System.out.println("File: " + args[0]);
                System.out.print(hoppersConfig);

                List<Configuration> solution = Solver.solve(hoppersConfig);
                if(solution != null){
                    System.out.println("Total configs: " +  Solver.getTotalConfigs());
                    System.out.println("Unique configs: " + Solver.getUniqueConfigs());

                    for (int i = 0; i < solution.size(); i++) {
                        System.out.println("Step " + i + ": \n" + solution.get(i));
                    }
                }
                else{
                    System.out.println("No solution");
                }
            }
            catch(IOException e){
                System.out.println("File not found");
            }
        }
    }
}
