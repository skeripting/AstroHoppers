/**
 * This class represents a configuration of the clock puzzle. It implements the Configuration interface
 * and provides methods to check if it's a solution, get its neighbors, and override equals, hashCode,
 * and toString methods.
 *
 * @author Michael Bauer
 */

package puzzles.clock;

import puzzles.common.solver.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class ClockConfig implements Configuration {

    private int currentTime;
    private static int endTime;
    private static int hours;


    /**
     * Constructor to initialize a ClockConfig object with the current time, end time, and total hours.
     *
     * @param hours The total number of hours on the clock
     * @param currentTime The current time of the clock
     * @param endTime The time to reach as the end time
     */
    public ClockConfig(int hours, int currentTime, int endTime){
        this.currentTime = currentTime;
        ClockConfig.endTime = endTime;
        ClockConfig.hours = hours;
    }


    /**
     * Checks if the current time is the solution (equal to the end time).
     *
     * @return true if the current time is the solution, false otherwise
     */
    @Override
    public boolean isSolution() {
        return currentTime == endTime;
    }

    /**
     * Gets the neighboring configurations of the current time on the clock.
     *
     * @return A collection of neighboring configurations
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> neighbors = new ArrayList<>();

        int nextForward = (currentTime % hours) + 1;
        neighbors.add(new ClockConfig(hours, nextForward, endTime));

        int nextBack = (currentTime == 1) ? hours : currentTime - 1;
        neighbors.add(new ClockConfig(hours, nextBack, endTime));

        return neighbors;
    }

    /**
     * Checks if the current clock is equal to another object.
     *
     * @param other The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if(other instanceof ClockConfig){
            ClockConfig otherClock = (ClockConfig) other;
            result = currentTime == otherClock.currentTime;
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
        return Integer.hashCode(currentTime);
    }

    /**
     * Returns the string representation of the current time on the clock.
     *
     * @return The string representation of the current time
     */
    @Override
    public String toString() {
        return currentTime +"";
    }
}
