
package babayega;

import Math.*;
import ai.Projectile;

// Player Object ----------------------------------------------------------------------------------------------------//
public class Player extends JumpingGameObject {

    private static final float DEFAULT_PLAYER_RADIUS = 15;
    private static final float DEFAULT_PLAYER_HEIGHT = 128;
    private static final float DEFAULT_MAX_HEALTH    = 100;
    private static final float BULLET_HEIGHT = 75;

    private float   maxHealth;
    private float   health;
    private boolean isBanditdead;
    private boolean isTouchingDoor;

    // Doors
    private static final int door1                   = 1;
    private static final int door2                   = 2;
    private static final int door3                   = 3;
    private static final int door4                   = 4;
    private static final int door5                   = 5;
    private              int currentDoor             = 0;
    
    // Ammunition
    private              int ammunition              = 0;
    private              int maxeAmmunition		     = 50;
    //private              int currentammunition     = 50;

    // bandit
    private static final int bandit1                 = 1;
    private static final int bandit2                 = 2;
    private static final int bandit3                 = 3;
    private              int banditkilled            = 0;
    private              int currentbandit           = 0;
    private				 int banditlife              = 0;

    // Player -------------------------------------------------------------------------------------------------------//
    public Player() {

        super(new PolygonGroup("player"));

        // Set up player bounds
        PolygonGroupBounds playerBounds = getBounds();
        playerBounds.setTopHeight(DEFAULT_PLAYER_HEIGHT);
        playerBounds.setRadius(DEFAULT_PLAYER_RADIUS);

        // Set up health
        maxHealth = DEFAULT_MAX_HEALTH;
        setHealth(maxHealth);

        isBanditdead   = false;
        isTouchingDoor = false;
        ammunition     = maxeAmmunition	;
        banditlife     = 25;
        
    }

    // Get Health ---------------------------------------------------------------------------------------------------//
    public float getHealth() {
        return health;
    }

    // Set Health ---------------------------------------------------------------------------------------------------//
    public void setHealth(float health) {
        this.health = health;
    }

    // Get Max Health -----------------------------------------------------------------------------------------------//
    public float getMaxHealth() {
        return maxHealth;
    }

    // Add Health ---------------------------------------------------------------------------------------------------//
    public void addHealth(float addition) {
        setHealth(health + addition);
    }

    // Decrease Health ----------------------------------------------------------------------------------------------//
    public void decreaseHealth(float subtract) { setHealth(health - subtract); }

    // Is Alive -----------------------------------------------------------------------------------------------------//
    public boolean isAlive() {
        return (health > 0);
    }

    // Is Touching Door ---------------------------------------------------------------------------------------------//
    public boolean isTouchingDoor() { return this.isTouchingDoor; }

    // Set Is Touching Door------------------------------------------------------------------------------------------//
    public void setIsTouchingDoor(boolean state) { this.isTouchingDoor = state; }

    // Is bandit dead ----------------------------------------------------------------------------------------------//
    public boolean isBanditdead() { return this.isBanditdead; }

    // Set Is Bandit dead ------------------------------------------------------------------------------------------//
    public void setisBanditdead(boolean state) { this.isBanditdead = state; }

    // Set Door -----------------------------------------------------------------------------------------------------//
    public void setDoor(int touchDoor) { this.currentDoor = touchDoor; }

    // Is Door 1 ----------------------------------------------------------------------------------------------------//
    public boolean isAtDoor1() { return this.currentDoor == door1; }

    // Is Door 2 ----------------------------------------------------------------------------------------------------//
    public boolean isAtDoor2() { return this.currentDoor == door2; }

    // Is Door 3 ----------------------------------------------------------------------------------------------------//
    public boolean isAtDoor3() { return this.currentDoor == door3; }

    // Is Door 4 ----------------------------------------------------------------------------------------------------//
    public boolean isAtDoor4() { return this.currentDoor == door4; }

    // Is Door 5 ----------------------------------------------------------------------------------------------------//
    public boolean isAtDoor5() { return this.currentDoor == door5; }

    // Get Current ammunition ----------------------------------------------------------------------------------------------//
    public  int getammunitiont() { return this.ammunition; }

    // Set ammunition ------------------------------------------------------------------------------------------------------//
    public void setammunition(int currentammunition) { this.ammunition = currentammunition; }
    
   // Get Current bandit ----------------------------------------------------------------------------------------------//
    public int currentbandit() { return this.currentbandit; }
    
 // Get maxAmmunition ----------------------------------------------------------------------------------------------//
    public int getmaxAmmunition() { return this.maxeAmmunition; }

    // Set bandit ------------------------------------------------------------------------------------------------------//
    public void setbandit(int bandit) { this.currentbandit = bandit; }

    // Set bandit killed ----------------------------------------------------------------------------------------------//
    public void setbanditkilled(int bandit) { this.banditkilled = bandit; }

    // Get bandit killed ----------------------------------------------------------------------------------------------//
    public int getbanditkilled() { return this.banditkilled; }
    
    // Set bandit life ----------------------------------------------------------------------------------------------//
    public void setbanditlife(int currentbanditlife) { this.banditlife = currentbanditlife; }

    // Get bandit life ----------------------------------------------------------------------------------------------//
    public int getbanditlife() { return this.banditlife; }

} // End Player Class