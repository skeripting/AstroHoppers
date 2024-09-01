package puzzles.astro.model;

import puzzles.astro.solver.Astro;
import puzzles.common.Observer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a model of the Astro game
 * @author the somosas (Kushal, Michael, Soban)
 */
public class AstroModel {
    /** the collection of observers of this model */
    private final List<Observer<AstroModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private AstroConfig currentConfig;

    public AstroConfig getCurrentConfig() {
        return currentConfig;
    }
    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<AstroModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void notifyObservers(String msg) {
        for (var observer : observers) {
            observer.update(this, msg);
        }
    }

    /**
     * Select an entity, given coordinates.
     * @param x The x component of the coordinate to select an entity from.
     * @param y The y component of the coordinate to select an entity from.
     * @return True if an entity was selected
     */
    public boolean select(int x, int y) {
        return this.currentConfig.selectEntity(x, y);
    }

    /**
     * Notify the observers that the model is ready
     */
    public void ready() {
        notifyObservers("READY");
    }

    /**
     * Set the current config to a specified config
     * @param config The config to set the current config to.
     */
    public void setCurrentConfig(AstroConfig config ){
        currentConfig = config;
    }

    /**
     * Load an AstroModel
     * @param fileName The name of the file to load
     * @throws IOException
     */
    public AstroModel(String fileName) throws IOException {
        setCurrentConfig(new AstroConfig(fileName));

    }
}
