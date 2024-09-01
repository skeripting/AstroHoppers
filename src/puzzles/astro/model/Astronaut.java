package puzzles.astro.model;

import puzzles.common.Direction;

import java.util.Arrays;

public class Astronaut implements Entity {
    private String name;
    private int positionX;
    private int positionY;

    public Astronaut(String name, int posX, int posY) {
        this.positionX = posX;
        this.positionY = posY;
        this.name = name;
    }

    public void setPosition(int x, int y) {
        this.positionX = x;
        this.positionY = y;
    }

    public boolean isRobot() {
        return false;
    }

    public String getName(){
        return name;
    }
    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Astronaut) {
            Astronaut otherAstronaut = (Astronaut) other;
            return this.name.equals(otherAstronaut.getName());
        }
        return false;
    }

    @Override
    public String toString(){
        return "Astronaut{x:" + getPositionX() + ", y:" + getPositionY() + "}";
    }
}
