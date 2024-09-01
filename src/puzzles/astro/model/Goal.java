package puzzles.astro.model;

public class Goal implements Entity {

    private int positionX;
    private int positionY;
    private String name;

    public Goal(String name, int x, int y){
        this.name = name;
        this.positionX = x;
        this.positionY = y;
    }


    public void setPosition(int x, int y) {
        this.positionX = x;
        this.positionY = y;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPositionY() {
        return positionY;
    }

    @Override
    public int getPositionX() {
        return positionX;
    }

    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if(other instanceof Goal){
            Goal otherGoal = (Goal) other;
            result = this.name.equals(otherGoal.getName());
        }
        return result;
    }

    @Override
    public String toString(){
        return "Goal{x:" + getPositionX() + ", y:" + getPositionY() + "}";
    }
}
