/**
 * This class represents a configuration of the dice puzzle. It implements the Configuration interface
 * and provides methods to check if it's a solution, get its neighbors, and override equals, hashCode,
 * and toString methods.
 *
 * @author Michael Bauer
 */


package puzzles.dice;

import puzzles.common.solver.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class DiceConfig implements Configuration {

    private String currentString;
    private static String endString;
    private static List<Die> dice;


    /**
     * Constructor to initialize a DiceConfig object with the current configuration,
     * end configuration, and list of dice.
     *
     * @param current The current configuration of the dice
     * @param end The end configuration to reach
     * @param diceList The list of dice objects
     */
    public DiceConfig(String current, String end, List<Die> diceList){
        this.currentString = current;
        endString = end;
        dice = diceList;
    }


    /**
     * Checks if the current configuration is the solution.
     *
     * @return true if the current configuration is the solution, false otherwise
     */
    @Override
    public boolean isSolution() {
        return currentString.equals(endString);
    }


    /**
     * Gets the neighboring configurations of the current configuration.
     *
     * @return A collection of neighboring configurations
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> neighbors = new ArrayList<>();

        for (int i = 0; i < dice.size(); i++) {
            Set<Character> neighborFaces = dice.get(i).getNeighborFaces(currentString.charAt(i));

            for(char neighbor : neighborFaces){
                StringBuilder newString = new StringBuilder(currentString);
                newString.setCharAt(i, neighbor);

                neighbors.add(new DiceConfig(newString.toString(), endString, dice));
            }
        }
        return neighbors;
    }

    /**
     * Checks if the current dice is equal to another object.
     *
     * @param other The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if(other instanceof DiceConfig){
            DiceConfig otherDice = (DiceConfig) other;
            result = this.currentString.equals(otherDice.currentString);
        }
        return result;
    }


    /**
     * Generates the hash code for the current object.
     *
     * @return The hash code value for the object
     */
    @Override
    public int hashCode() {
        return this.currentString.hashCode();
    }


    /**
     * Returns the string representation of the current configuration.
     *
     * @return The string representation of the current configuration
     */
    @Override
    public String toString() {
        return this.currentString;
    }
}