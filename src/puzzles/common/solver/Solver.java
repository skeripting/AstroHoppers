/**
 * The Solver class provides methods for solving puzzles using a breadth-first search algorithm.
 * It keeps track of the total and unique configurations visited during the search process.
 *
 * @Author Michael Bauer
 */

package puzzles.common.solver;

import java.util.*;

public class Solver{
    private static Map<Configuration, Configuration> predMap = new HashMap<>();
    private static LinkedList<Configuration> queue = new LinkedList<>();
    private static List<Configuration> solution = new ArrayList<>();
    private static Set<Configuration> visited = new HashSet<>();
    private static int totalConfigs = 1;


    /**
     * Solves the puzzle starting from the given initial configuration.
     * Performs a breadth-first search to explore all possible configurations until a solution is found.
     * Returns the solution path as a list of configurations.
     *
     * @param first The initial configuration of the puzzle.
     * @return The list of configurations representing the solution path, or null if no solution is found.
     */
    public static List<Configuration> solve(Configuration first){

        queue.clear();
        visited.clear();
        solution.clear();
        predMap.clear();

        queue.offer(first);
        visited.add(first);

        while(!queue.isEmpty()){
            Configuration current = queue.poll();

            if(current.isSolution()){
                while(current != null){
                    solution.addFirst(current);
                    current = predMap.get(current);
                }
                return solution;
            }

            Collection<Configuration> neighbors = current.getNeighbors();
            totalConfigs += neighbors.size();

            for (Configuration neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    queue.offer(neighbor);
                    visited.add(neighbor);
                    predMap.put(neighbor, current);

                }
            }

        }
        return null;
    }

    /**
     * Gets the total number of configurations explored during the search process.
     *
     * @return The total number of configurations explored.
     */
    public static int getTotalConfigs(){
        return totalConfigs;
    }

    /**
     * Gets the number of unique configurations visited during the search process.
     *
     * @return The number of unique configurations visited.
     */
    public static int getUniqueConfigs(){
        return visited.size();
    }



}
