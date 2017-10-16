
package babayega;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import graphics3D.Overlay;
import Math.ViewWindow;

public class HeadsUpDisplay implements Overlay {

    // Increase health display by 20 points per second
    private static final float DISPLAY_INC_RATE = 0.04f;

    private Player player;
    private float displayedHealth;
    private Font font;
    private int screenWidth;
    private int screenHeight;

    // Heads Up Display ---------------------------------------------------------------------------------------------//
    public HeadsUpDisplay(Player player, int width, int height) {

        this.player     = player;

        displayedHealth = 0;

        this.screenWidth = width;

        this.screenHeight = height;
    }

    // Update -------------------------------------------------------------------------------------------------------//
    public void update(long elapsedTime) {

        // Increase or decrease displayedHealth a small amount
        // at a time, instead of just setting it to the player's
        // health.
        float actualHealth = player.getHealth();
        if (actualHealth > displayedHealth) {
            displayedHealth = Math.min(actualHealth, displayedHealth + elapsedTime * DISPLAY_INC_RATE);
        }
        else if (actualHealth < displayedHealth) {
            displayedHealth = Math.max(actualHealth, displayedHealth - elapsedTime * DISPLAY_INC_RATE);
        }

        if(displayedHealth < 0) displayedHealth = 0;
    }

    public void draw(Graphics2D g, ViewWindow window) {

        Font font = new Font("Ethnocentric Rg", Font.PLAIN, 24);

        g.setFont(font);
        g.translate(window.getLeftOffset(), window.getTopOffset());

        // Draw health value (number)
        String str = Integer.toString(Math.round(displayedHealth));
        g.setColor(Color.WHITE);
        g.drawString("Health: " + str, 10, screenHeight - 10);
    }

    // Is Enabled ---------------------------------------------------------------------------------------------------//
    public boolean isEnabled() {

        return (player != null && (player.isAlive() || displayedHealth > 0));
    }

} // End Heads Up Display
