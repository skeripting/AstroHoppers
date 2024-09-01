/**
 * This class represents a dice puzzle solver. It parses the command-line arguments,
 * initializes dice objects, builds their configurations, and finds a solution using
 * the Solver class.
 *
 * @author Michael Bauer
 */

package puzzles.dice;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import java.io.IOException;
import java.util.*;

public class Dice {

    /**
     * Main method to run the dice puzzle solver.
     *
     * @param args The command-line arguments. Expected format: start end die1 die2...
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java Dice start end die1 die2...");
        } else {
            String start = args[0];
            String end = args[1];

            ArrayList<String> allDice = new ArrayList<>();
            allDice.addAll(Arrays.asList(args).subList(2, args.length));

            List<Die> dice = new ArrayList<>();
            for (int i = 0; i < allDice.size(); i++) {
                dice.add(new Die("die-" + allDice.get(i) + ".txt"));
                try {
                    dice.get(i).buildDice();
                }
                catch (IOException e){
                    System.out.println("File not found");
                }
            }

            DiceConfig diceConfig = new DiceConfig(start, end, dice);
            List<Configuration> solution = Solver.solve(diceConfig);

            for (int i = 0; i < dice.size(); i++) {
                dice.get(i).setDieNum(i);
                System.out.print(dice.get(i));
            }
            System.out.println("Start: " + start + ", End: " + end);
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