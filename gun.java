package babayega;

import Math.*;
import ai.Projectile;

// Player Object ----------------------------------------------------------------------------------------------------//
public class gun extends JumpingGameObject {

    private static final float DEFAULT_GUN_RADIUS = 10;
    private static final float DEFAULT_GUN_HEIGHT = 20;
    private static final int DEFAULT_MAX_AMMUNITION    = 40;
    
    private int   maxammunition;
    private int   ammunition;
//    private boolean isfiring;

   

    // Gun -------------------------------------------------------------------------------------------------------//
    public gun() {

        super(new PolygonGroup("gun"));

        // Set up gun bounds
        PolygonGroupBounds gunBounds = getBounds();
        gunBounds.setTopHeight(DEFAULT_GUN_HEIGHT);
        gunBounds.setRadius(DEFAULT_GUN_RADIUS);

        // Set up ammunition
        maxammunition = DEFAULT_MAX_AMMUNITION;
        setammunition(maxammunition);
           
    }

    // Get ammunition ---------------------------------------------------------------------------------------------------//
    public float getammunition() {
        return ammunition;
    }

    // Set ammunition ---------------------------------------------------------------------------------------------------//
    public void setammunition(int health) {
        this.ammunition = ammunition;
    }

    // Get Max ammunition -----------------------------------------------------------------------------------------------//
    public float getmaxammunition() {
        return maxammunition;
    }

    // Add ammunition ---------------------------------------------------------------------------------------------------//
    public void ammunition(int addition) {
        setammunition(ammunition + addition);
    }

    // Decrease ammunition ----------------------------------------------------------------------------------------------//
    public void decreaseammunition(int subtract) { setammunition(ammunition - subtract); }

    // has Ammunition -----------------------------------------------------------------------------------------------------//
    public boolean hasammunition() {
        return (ammunition > 0);
    }

    

} // End Gun Class