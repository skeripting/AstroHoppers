package puzzles.astro.model;

public class Robot implements Entity {
    private int positionX;
    private int positionY;
    private String name;

     public Robot(String name, int locationX, int locationY){
        this.positionX = locationX;
        this.positionY = locationY;
        this.name = name;
    }

    public void setPosition(int x, int y) {
        this.positionX = x;
        this.positionY = y;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY(){
         return positionY;
    }

    @Override
    public String getName() {
        return name;
    }

    public int hashCode(){
         return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if(obj instanceof Robot){
            Robot otherRobot = (Robot)obj;
            result = name.equals(otherRobot.name);
        }
        return result;
    }

    @Override
    public String toString(){
         return "Robot{x:" + getPositionX() + ", y:" + getPositionY() + "}";
    }
}
