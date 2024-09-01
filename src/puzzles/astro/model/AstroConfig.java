package puzzles.astro.model;

import puzzles.astro.solver.Astro;
import puzzles.common.Direction;
import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Represents an instance of a configuration for the Astro game.
 *
 * @author the somosas (Kushal, Michael, Soban)
 */
public class AstroConfig implements Configuration{
    /** a cell that has not been assigned a value yet */
    private final static char EMPTY = '.';
    private static int nRows;
    private static int nColumns;
    private static int nRobots;

    private Astronaut astro;
    private Robot[] robots;
    private static Goal goal;

    private Entity[][] grid;
    private static List<Entity> entityList;
    public static Entity selectedEntity;

    public int getNRows() {
        return nRows;
    }

    public int getNColumns() {
        return nColumns;
    }

    public Entity[][] getGrid() {
        return grid;
    }
    /**
     * Parse a string that follows the structure:
     * character x,y.
     * @param line The line that we are parsing.
     */
    private Entity ParseEntity(String[] line) {
        String name = line[0];
        String[] goal = line[1].split(",");
        int y = Integer.parseInt(goal[0]);
        int x = Integer.parseInt(goal[1]);

        if (name.charAt(0) == 'A') {
            Astronaut astro = new Astronaut(name, x, y);
            setLocation(x, y, astro);
            entityList.add(astro);
            return astro;
        }
        else if (name.charAt(0) == '*') {
            Goal g = new Goal(name, x, y);
            //this.grid[x][y] = g;
            return g;
        }

        Robot robot = new Robot(name, x, y);
        setLocation(x, y, robot);
        return robot;
    }

    /**
     * Select an entity, given x and y coordinates.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return true, if a valid entity was found and selected.
     */
    public boolean selectEntity(int x, int y){
        if (x < 0 || y < 0 || x > nColumns - 1 || y > nRows - 1) {
            return false;
        }
        if (grid[x][y] != null && !(grid[x][y] instanceof Goal)) {
            selectedEntity = grid[x][y];
            return true;
        }
        return false;
    }

    /**
     * Get the goal.
     * @return The goal object
     */
    public Goal getGoal() {
        return goal;
    }

    /**
     * Move the selected entity in a given direction.
     * @param dir The direction to move the entity in.
     * @return Whether the entity was removed.
     */
    public boolean moveSelectedEntity(Direction dir) {
        return moveEntity(selectedEntity, dir);
    }

    /**
     * Get the selected entity object.
     * @return The selected entity.
     */
    public Entity getSelectedEntity() {
        return selectedEntity;
    }

    /**
     * Move an entity in a given direction
     * @param entity The entity to move
     * @param dir The direction to move the entity in
     * @return true if successful
     */
    public boolean moveEntity(Entity entity, Direction dir) {
        int currentEntityPositionX = entity.getPositionX();
        int currentEntityPositionY = entity.getPositionY();

        if (entity == null || entity instanceof Goal) {
            return false;
        }

        if (dir == Direction.WEST) {
            for (int i = currentEntityPositionX - 1; i >= 0; i--) {
                if (this.grid[i][currentEntityPositionY] != null && !(this.grid[i][currentEntityPositionY] instanceof Goal)) {
                    this.grid[currentEntityPositionX][currentEntityPositionY] = null;
                    setLocation(i + 1, currentEntityPositionY, entity);
                    return true;
                }
            }
        } else if (dir == Direction.EAST) {
            for (int i = currentEntityPositionX + 1; i < nColumns; i++) {
                if (this.grid[i][currentEntityPositionY] != null && !(this.grid[i][currentEntityPositionY] instanceof Goal)) {
                    this.grid[currentEntityPositionX][currentEntityPositionY] = null;
                    setLocation(i - 1, currentEntityPositionY, entity);
                    return true;
                }
            }
        } else if (dir == Direction.NORTH) {
            for (int i = currentEntityPositionY - 1; i >= 0; i--) {
                if (this.grid[currentEntityPositionX][i] != null && !(this.grid[currentEntityPositionX][i] instanceof Goal)) {
                    this.grid[currentEntityPositionX][currentEntityPositionY] = null;
                    setLocation(currentEntityPositionX, i + 1, entity);
                    return true;
                }
            }
        }
        else if (dir == Direction.SOUTH) {
            for (int i = currentEntityPositionY + 1; i < nRows; i++) {
                if (this.grid[currentEntityPositionX][i] != null && !(this.grid[currentEntityPositionX][i] instanceof Goal)) {
                    this.grid[currentEntityPositionX][currentEntityPositionY] = null;
                    setLocation(currentEntityPositionX, i - 1, entity);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Create a new AstroConfig object
     * @param filename The filename to load it from.
     * @throws IOException
     */
    public AstroConfig(String filename) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))){
            entityList = new ArrayList<>();

            String line;
            String[] firstLine = in.readLine().split("\\s+");
            nRows = Integer.parseInt(firstLine[0]);
            nColumns = Integer.parseInt(firstLine[1]);
            grid = new Entity[nColumns][nRows];

            String[] goalLine = in.readLine().split("\\s+");
            goal = (Goal) ParseEntity(goalLine);

            String[] astronautLine = in.readLine().split("\\s+");
            astro = (Astronaut) ParseEntity(astronautLine);

            nRobots = Integer.parseInt(in.readLine());
            robots = new Robot[nRobots];

            for (int i = 0; i < nRobots; i++) {
                String[] robotLine = in.readLine().split("\\s+");
                Robot r = (Robot)ParseEntity(robotLine);
                robots[i] = r;
            }

            selectedEntity = astro;
        }
    }

    /**
     * Create a copy of an AstroConfig.
     * @param original The original AstroConfig to copy from.
     */
    public AstroConfig(AstroConfig original){
        this.grid = deepCopyGrid(original.grid);
        this.astro = (Astronaut) this.grid[original.astro.getPositionX()][original.astro.getPositionY()];//new Astronaut("A", original.astro.getPositionX(), original.astro.getPositionY());

    }

    /**
     * Check if the Configuration is a solution.
     * @return true if it's a solution
     */
    @Override
    public boolean isSolution() {
        return astro.getPositionX() == goal.getPositionX() &&
                astro.getPositionY() == goal.getPositionY();
    }

    /**
     * A helper function to deepcopy the grid.
     * @param originalGrid The original grid to copy.
     * @return A new grid.
     */
    private Entity[][] deepCopyGrid(Object[][] originalGrid) {
        Entity[][] newGrid = new Entity[originalGrid.length][];
        for (int i = 0; i < originalGrid.length; i++) {
            newGrid[i] = new Entity[originalGrid[i].length];
            for (int j = 0; j < originalGrid[i].length; j++) {
                if (originalGrid[i][j] instanceof Astronaut) {
                    Astronaut originalAstro = (Astronaut) originalGrid[i][j];
                    newGrid[i][j] = new Astronaut("A", originalAstro.getPositionX(), originalAstro.getPositionY());
                } else if (originalGrid[i][j] instanceof Robot) {
                    Robot originalRobot = (Robot) originalGrid[i][j];
                    newGrid[i][j] = new Robot(originalRobot.getName(), originalRobot.getPositionX(), originalRobot.getPositionY());
                }
                else {
                    newGrid[i][j] = null;
                }
            }
        }
        return newGrid;
    }

    /**
     * Get the neighbors of all Entities in this configuration.
     * @return The neighbors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> neighbors = new ArrayList<>();
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                Entity gridObject = grid[x][y];
                if (gridObject != null && !(gridObject instanceof Goal)) {
                    AstroConfig neighbor = new AstroConfig(this);
                    if (neighbor.moveEntity(neighbor.grid[x][y], Direction.NORTH)){
                        neighbors.add(neighbor);
                    }
                    neighbor = new AstroConfig(this);
                    if (neighbor.moveEntity(neighbor.grid[x][y], Direction.SOUTH)){
                        neighbors.add(neighbor);
                    }
                    neighbor = new AstroConfig(this);
                    if (neighbor.moveEntity(neighbor.grid[x][y], Direction.EAST)){
                        neighbors.add(neighbor);
                    }
                    neighbor = new AstroConfig(this);
                    if (neighbor.moveEntity(neighbor.grid[x][y], Direction.WEST)){
                        neighbors.add(neighbor);
                    }
                }
            }
        }
        return neighbors;
    }

    /**
     * Check if this AstroConfig equals another one.
     * @param other The other AstroConfig that is being checked.
     * @return Whether they are equal.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof AstroConfig) {
            AstroConfig otherConfig = (AstroConfig) other;
            return Arrays.deepEquals(this.grid, otherConfig.grid);
        }
        return false;
    }

    /**
     * Get the hashcode for this AstroConfig.
     * @return The hashcode.
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.grid);
    }

    /**
     * Get the number of columns in the grid.
     * @return The number of columns
     */
    public int getCols(){
        return nColumns;
    }

    /**
     * Get the number of rows in the grid.
     * @return The number of rows
     */
    public int getRows(){
        return nRows;
    }

    /**
     * Set the position of an Entity.
     * @param x The x component of the new position.
     * @param y The y component of the new position.
     * @param entity The entity that we're changing the position of.
     */
    public void setLocation(int x, int y, Entity entity){
        grid[x][y] = entity;
        if (entity != null) {
            entity.setPosition(x, y);
        }
    }

    /**
     * Returns a string representation of the puzzle including all necessary info.
     *
     * @return the string
     */
    @Override
    public String toString() {
        String output = "";
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getCols(); j++) {
                Entity current = grid[j][i];
                if (current != null) {
                    output += current.getName() + " ";
                }
                else if (current == null && j == goal.getPositionX() && i == goal.getPositionY()) {
                    output += "* ";
                }
                else {
                    output += ". ";
                }
            }
            output += "\n";
        }
        return output;
    }
}
