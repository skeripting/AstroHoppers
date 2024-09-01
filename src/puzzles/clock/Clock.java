package puzzles.clock;

/**
 * This class represents the main class for the clock puzzle. It contains the main method to execute the clock puzzle solver.
 *
 * @author Michael Bauer
 */

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.util.List;

public class Clock {

    /**
     * The main method to execute the clock puzzle solver.
     *
     * @param args Command-line arguments containing the number of hours, start time, and end time
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java Clock hours start end");
        } else {
            int hours = Integer.parseInt(args[0]);
            int start = Integer.parseInt(args[1]);
            int end = Integer.parseInt(args[2]);

            System.out.println("Hours: " + hours + ", Start: " + start + ", End: " + end);

            ClockConfig clock = new ClockConfig(hours, start, end);
            List<Configuration> solution = Solver.solve(clock);

            if(solution != null){
                System.out.println("Total configs: " +  Solver.getTotalConfigs());
                System.out.println("Unique configs: " + Solver.getUniqueConfigs());

                for (int i = 0; i < solution.size(); i++) {
                    System.out.println("Step " + i + ": " + solution.get(i));
                }
            }
            else{
                System.out.println("No solution");
            }


        }
    }
}
