package puzzles.astro.model;

public interface Entity {

    public String getName();
    public int getPositionX();
    public int getPositionY();
    public void setPosition(int x, int y);
    public boolean equals(Object other);
    public int hashCode();
}
