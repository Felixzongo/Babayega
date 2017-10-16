
package babayega;

import Math.*;
import ai.Projectile;

// Player Object ----------------------------------------------------------------------------------------------------//
public class Bandit extends JumpingGameObject {

    private static final float DEFAULT_BANDIT_RADIUS = 15;
    private static final float DEFAULT_BANDIT_HEIGHT = 128;
    private static final float DEFAULT_MAX_HEALTH    = 100;
	


    private float   BanditmaxHealth;
    private float   BanditHealth;



    // Bandit -------------------------------------------------------------------------------------------------------//
    public Bandit() {

        super(new PolygonGroup("Bandit"));

        // Set up Bandit bounds
        PolygonGroupBounds BanditBounds = getBounds();
        BanditBounds.setTopHeight(DEFAULT_BANDIT_HEIGHT);
        BanditBounds.setRadius(DEFAULT_BANDIT_RADIUS);

        // Set up health
        BanditmaxHealth = DEFAULT_MAX_HEALTH;
        setBanditHealth(BanditmaxHealth);
    }

    // Get Bandit health ---------------------------------------------------------------------------------------------------//
    public float getBanditHealth() {
        return BanditHealth;
    }

    // Set Bandit health ---------------------------------------------------------------------------------------------------//
    public void setBanditHealth(float Bandithealth) {
        this.BanditHealth = Bandithealth;
    }

    // Get Max Bandit health -----------------------------------------------------------------------------------------------//
    public float getMaxBandithealth() {
        return getBanditHealth();
    }

    // Add BanditHealth ---------------------------------------------------------------------------------------------------//
    public void addBanditHealth(float addition) {
        setBanditHealth(BanditHealth + addition);
    }

    // Decrease Bandit Health ----------------------------------------------------------------------------------------------//
    public void decreaseBanditHealth(float subtract) { setBanditHealth(BanditHealth - subtract); }

    // Is Alive -----------------------------------------------------------------------------------------------------//
    public boolean isAlive() {
        return (BanditHealth > 0);
    }

    

} // End Bandit Class