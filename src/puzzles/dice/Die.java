/**
 * This class represents a singular Dice. It stores the file that it comes from, the number of faces,
 * the neighbors, and which die it is.
 *
 * @Author Michael Bauer
 */


package puzzles.dice;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Die {

    private String filename;
    private int numFaces;
    private Map<Character, Set<Character>> predMap;
    private int dieNum;

    /**
     * This method initializes a die object
     *
     * @param filename The file in which this die came from
     */
    public Die(String filename){
        this.filename = filename;
        this.numFaces = 0;
        this.predMap = new LinkedHashMap<>();
    }


    /**
     * This method returns the number of faces that a die has
     *
     * @return numFaces: num of faces on the die object
     */
    public int getNumFaces() {
        return numFaces;
    }

    /**
     * Retrieves the neighbors of a specified face on this die.
     *
     * @param ch The face for which to retrieve the neighbors.
     * @return A set containing the neighboring faces.
     */
    public Set<Character> getNeighborFaces(char ch){
        return predMap.get(ch);
    }

    /**
     * Loads the configuration of this die from the specified file.
     *
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public void buildDice() throws IOException {

        try(BufferedReader in = new BufferedReader(new FileReader(filename))){

            String[] line = in.readLine().split(" ");
            this.numFaces = Integer.parseInt(line[0]);

            for(int i = 0; i <= numFaces; i++){
                line = in.readLine().split(" ");

                char face = line[0].charAt(0);
                Set<Character> neighbors = new LinkedHashSet<>();
                for(int j = 1; j < line.length; j++){
                    neighbors.add(line[j].charAt(0));
                }
                predMap.put(face, neighbors);
            }

        }

    }

    /**
     * Sets the number of this die.
     *
     * @param num The number of this die.
     */
    public void setDieNum(int num){
        this.dieNum = num;
    }

    /**
     * Generates a string representation of this die, including its filename, number of faces,
     * and the neighbors of each face.
     *
     * @return A string representation of this die.
     */
    @Override
    public String toString(){
        String result = "Die #" + dieNum + ": File: " + filename + ", Faces: " + numFaces + "\n";

        for (Map.Entry<Character, Set<Character>> entry : predMap.entrySet()) {
            char face = entry.getKey();
            Set<Character> neighbors = entry.getValue();
            result += "\t" + face + "=[";

            int index = 0;
            for (char neighbor : neighbors) {
                result += neighbor;
                if (index < neighbors.size() - 1) {
                    result += ", ";
                }
                index++;
            }
            result += "]\n";
        }

        return result;
    }
}
