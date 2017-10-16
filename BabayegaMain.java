
package babayega;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.List;

import bsp2D.*;
import input.*;
import Math.*;
import game.*;
import game.GameCore3D;
import sound.Sound;
import sound.SoundManager;

import javax.sound.sampled.AudioFormat;

// Dark Realm Core --------------------------------------------------------------------------------------------------//
public abstract class BabayegaMain extends GameCore3D {

    private static final float PLAYER_SPEED = .5f;
    private static final float PLAYER_TURN_SPEED = 0.04f;
    private static final float CAMERA_HEIGHT = 100;

    //protected GameAction fire = new GameAction("fire", GameAction.DETECT_INITAL_PRESS_ONLY);
    protected GameAction jump       = new GameAction("jump",       GameAction.DETECT_INITAL_PRESS_ONLY);
    protected GameAction open       = new GameAction("open",       GameAction.DETECT_INITAL_PRESS_ONLY);
    protected GameAction grabeobject = new GameAction("grabeobject", GameAction.DETECT_INITAL_PRESS_ONLY);
    protected GameAction fire       = new GameAction("fire",       GameAction.DETECT_INITAL_PRESS_ONLY);

    protected GameObjectManager  gameObjectManager;
    protected DisplayMode[]      modes;
    protected BSPTree            bspTree;
    protected String             mapFile;
    private   HeadsUpDisplay     display;
    protected CollisionDetection collisionDetection;

    // Doors
    
    private boolean door_1_Open;
    private boolean door_2_Open;
    private boolean door_3_Open;
    private boolean door_4_Open;
    private boolean door_5_Open;
    private Timer   doorTimer;
    private Timer   fireTimer;
    private Timer   killeBanditTimer;
    

    public BabayegaMain(String[] args, String defaultMap) {

        modes = MID_RES_MODES;

        for (int i=0; i<args.length; i++) {
            if (args[i].equals("-lowres")) {
                modes = LOW_RES_MODES;
                fontSize = 12;
            }
        }
        for (int i=0; mapFile == null && i<args.length; i++) {
            if (mapFile == null && !args[i].startsWith("-")) {
                mapFile = args[i];
            }
        }
        if (mapFile == null) {
            mapFile = defaultMap;
        }

        door_1_Open        = false;
        door_2_Open        = false;
        door_3_Open        = false;
        door_4_Open        = false;
        door_5_Open        = false;
        doorTimer          = new Timer();
        killeBanditTimer   = new Timer();
        fireTimer          = new Timer();
    }

    // Init ---------------------------------------------------------------------------------------------------------//
    public void init() {

        // Set up the local lights for the model.
        float ambientLightIntensity = .8f;

        List lights = new LinkedList();
        lights.add(new PointLight3D(-100,100,100, .5f, -1));
        lights.add(new PointLight3D(100,100,0, .5f, -1));

        // load the object model
        ObjectLoader loader = new ObjectLoader();
        loader.setLights(lights, ambientLightIntensity);

        init(modes);

        inputManager.mapToKey(jump,          KeyEvent.VK_B);
        inputManager.mapToKey(open,          KeyEvent.VK_ENTER);
        inputManager.mapToKey(grabeobject,   KeyEvent.VK_G);
        inputManager.mapToKey(fire,          KeyEvent.VK_SPACE);
        inputManager.mapToMouse(fire,        InputManager.MOUSE_BUTTON_1);
        inputManager.mapToMouse(grabeobject, InputManager.MOUSE_BUTTON_2);

        display = new HeadsUpDisplay((Player)gameObjectManager.getPlayer(), screen.getWidth(), screen.getHeight());
    }

    // Create Polygon Renderer --------------------------------------------------------------------------------------//
    public void createPolygonRenderer() {

        // make the view window the entire screen
        viewWindow = new ViewWindow(0, 0, screen.getWidth(), screen.getHeight(), (float)Math.toRadians(75));

        Transform3D camera = new Transform3D();
        polygonRenderer = new BSPRenderer(camera, viewWindow);
    }

    // Update World -------------------------------------------------------------------------------------------------//
    public void updateWorld(long elapsedTime) {

        float angleVelocity;

        Player player = (Player)gameObjectManager.getPlayer();
        MovingTransform3D playerTransform = player.getTransform();
        Vector3D velocity = playerTransform.getVelocity();

        velocity.x = 0;
        velocity.z = 0;

        float x = -playerTransform.getSinAngleY();
        float z = -playerTransform.getCosAngleY();

        if (goForward.isPressed())  { velocity.add(x * PLAYER_SPEED, 0, z * PLAYER_SPEED); }
        if (goBackward.isPressed()) { velocity.add(-x * PLAYER_SPEED, 0, -z * PLAYER_SPEED); }
        if (goLeft.isPressed())     { velocity.add(z * PLAYER_SPEED, 0, -x * PLAYER_SPEED); }
        if (goRight.isPressed())    { velocity.add(-z * PLAYER_SPEED, 0, x * PLAYER_SPEED); }
        if (jump.isPressed())       { player.setJumping(true); }
        if (open.isPressed())       { openDoor(player);        }
        if (grabeobject.isPressed()) { grabobject(player);      }
        if (fire.isPressed()) { killbandit(player);      }

        playerTransform.setVelocity(velocity);

        // Look up/down (rotate around x)
        angleVelocity = Math.min(tiltUp.getAmount(), 200);
        angleVelocity += Math.max(-tiltDown.getAmount(), -200);
        playerTransform.setAngleVelocityX(angleVelocity * PLAYER_TURN_SPEED / 200);

        // Turn (rotate around y)
        angleVelocity = Math.min(turnLeft.getAmount(), 200);
        angleVelocity += Math.max(-turnRight.getAmount(), -200);
        playerTransform.setAngleVelocityY(angleVelocity * PLAYER_TURN_SPEED / 200);

        // Update objects
        gameObjectManager.update(elapsedTime);

        // Limit look up/down
        float angleX = playerTransform.getAngleX();
        float limit = (float)Math.PI / 2;

        if(angleX < -limit)     { playerTransform.setAngleX(-limit); }
        else if(angleX > limit) { playerTransform.setAngleX(limit); }

        // Set the camera to be 100 units above the player
        Transform3D camera = polygonRenderer.getCamera();
        camera.setTo(playerTransform);
        camera.getLocation().add(0,CAMERA_HEIGHT,0);

        display.update(elapsedTime);
    }

    

	// Create Polygons ----------------------------------------------------------------------------------------------//
    public void createPolygons() {

        Graphics2D g = screen.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0,0, screen.getWidth(), screen.getHeight());
        g.setColor(Color.WHITE);
        g.drawString("Loading...", 5, screen.getHeight() - 5);
        screen.update();

        float ambientLightIntensity = .2f;
        List lights = new LinkedList();
        lights.add(new PointLight3D(-100,100,100, .3f, -1));
        lights.add(new PointLight3D(100,100,0, .3f, -1));

        MapLoader loader = new MapLoader(new BSPTreeBuilderWithPortals());
        loader.setObjectLights(lights, ambientLightIntensity);

        try
        {
            bspTree = loader.loadMap(mapFile);
        }
        catch(IOException ex) { ex.printStackTrace(); }

        collisionDetection = new CollisionDetectionWithSliding(bspTree);
        gameObjectManager = new GridGameObjectManager(bspTree.calcBounds(), collisionDetection);
        gameObjectManager.addPlayer(new Player());

        ((BSPRenderer)polygonRenderer).setGameObjectManager(gameObjectManager);

        createGameObjects(loader.getObjectsInMap());
        Transform3D start = loader.getPlayerStartLocation();
        gameObjectManager.getPlayer().getTransform().setTo(start);
    }

    // Create Game Objects ------------------------------------------------------------------------------------------//
    protected abstract void createGameObjects(List mapObjects);

    // Draw Polygons ------------------------------------------------------------------------------------------------//
    public void drawPolygons(Graphics2D g) {

        polygonRenderer.startFrame(g);

        // Draw polygons in bsp tree (set z buffer)
        ((BSPRenderer)polygonRenderer).draw(g, bspTree);

        // Draw game object polygons (check and set z buffer)
        gameObjectManager.draw(g, (GameObjectRenderer)polygonRenderer);

        polygonRenderer.endFrame(g);

        display.draw(g, viewWindow);
    }

    // Open Door ----------------------------------------------------------------------------------------------------//
    private void openDoor(Player player) {

        if(player.isTouchingDoor())
        {
            if(player.isAtDoor1() && !door_1_Open)
            {
                doorTimer.cancel();
                doorTimer = new Timer();

                // After timer ends open the door
                TimerTask animAction = new TimerTask()
                {
                    public void run()
                    {
                        // Open Forward Door based on Z Axis
                        open_Forward_Door(600);
                        door_1_Open = true;
                        
                    }
                };

                // Start Timer with delay amount to open door
                doorTimer.schedule(animAction, 900);
                player.setammunition(player.getmaxAmmunition());
                player.setbanditlife(35);
                

                playDoorSFX();
            }

            if(player.isAtDoor2() && player.currentbandit() == 3)
            {
                System.out.println(player.isAtDoor2() + " " + !door_2_Open);
                if(player.isAtDoor2() && !door_2_Open)
                {
                    doorTimer.cancel();
                    doorTimer = new Timer();

                    // After timer ends open the door
                    TimerTask animAction = new TimerTask()
                    {
                        public void run()
                        {
                            // Open Forward Door based on Z Axis
                            open_Forward_Door(2000);
                            door_2_Open = true;
                        }
                    };

                    // Start Timer with delay amount to open door
                    doorTimer.schedule(animAction, 900);
                    player.setammunition(player.getmaxAmmunition());
                    player.setbanditlife(45);

                    playDoorSFX();
                }
            }
            else if(player.isAtDoor2())
                 {
                     killeBanditTimer.cancel();
                     killeBanditTimer = new Timer();

                     // After timer ends open the door
                     TimerTask animAction = new TimerTask() { public void run() { displaykillBandit = false; } };

                     // Start Timer with delay amount to open door
                     killeBanditTimer.schedule(animAction, 3000);

                     displaykillBandit = true;
                 }

            if(player.isAtDoor3() && player.currentbandit() >= 1)
            {
                if(player.isAtDoor3() &&!door_3_Open)
                {
                    doorTimer.cancel();
                    doorTimer = new Timer();

                    // After timer ends open the door
                    TimerTask animAction = new TimerTask()
                    {
                        public void run()
                        {
                            // Open Forward Door based on Z Axis
                            open_Side_Door(840, 1, -1);
                            door_3_Open = true;
                        }
                    };

                    // Start Timer with delay amount to open door
                    doorTimer.schedule(animAction, 900);
                    player.setammunition(player.getmaxAmmunition());

                    playDoorSFX();
                }

            }
            else if(player.isAtDoor3())
                 {
                     killeBanditTimer.cancel();
                     killeBanditTimer = new Timer();

                     // After timer ends open the door
                     TimerTask animAction = new TimerTask() { public void run() { displaykillBandit= false; } };

                     // Start Timer with delay amount to open door
                     killeBanditTimer.schedule(animAction, 3000);

                     displaykillBandit = true;
                 }

            if(player.isAtDoor4() && player.currentbandit() >= 2)
            {
                if(player.isAtDoor4() && !door_4_Open)
                {
                    doorTimer.cancel();
                    doorTimer = new Timer();

                    // After timer ends open the door
                    TimerTask animAction = new TimerTask()
                    {
                        public void run()
                        {
                            // Open Forward Door based on Z Axis
                            open_Side_Door(1840, -1, 1);
                            door_4_Open = true;
                        }
                    };

                    // Start Timer with delay amount to open door
                    doorTimer.schedule(animAction, 900);
                    player.setammunition(player.getmaxAmmunition());

                    playDoorSFX();
                }
            }
            else if(player.isAtDoor4())
                 {
                     killeBanditTimer.cancel();
                     killeBanditTimer = new Timer();

                     // After timer ends open the door
                     TimerTask animAction = new TimerTask() { public void run() { displaykillBandit= false; } };

                     // Start Timer with delay amount to open door
                     killeBanditTimer.schedule(animAction, 3000);

                     displaykillBandit = true;
                 }

            if(player.isAtDoor5())
            {
                if(player.isAtDoor5() && !door_5_Open)
                {
                    doorTimer.cancel();
                    doorTimer = new Timer();

                    // After timer ends open the door
                    TimerTask animAction = new TimerTask()
                    {
                        public void run()
                        {
                            // Open Forward Door based on Z Axis
                            open_Forward_Door(2500);
                            door_5_Open = true;
                        }
                    };

                    // Start Timer with delay amount to open door
                    doorTimer.schedule(animAction, 900);
                    

                    playDoorSFX();
                }
            }
        }
    }

    // Open Forward Door --------------------------------------------------------------------------------------------//
    private void open_Forward_Door(int z) {

        Iterator i = gameObjectManager.iterator();

        while(i.hasNext())
        {
            GameObject door = (GameObject)i.next();

            if(door.getZ() == z)
            {
                Vector3D doorLoc = door.getLocation();
                doorLoc.setTo(door.getX() + 41, 0, z + 36);
                door.getTransform().setVelocity(doorLoc);
                door.getTransform().setAngleVelocityY(0.785398F, 2);
            }
        }
    }

    // Open Side Door  ----------------------------------------------------------------------------------------------//
    private void open_Side_Door(int z, int xOffset, int zOffset) {

        Iterator i = gameObjectManager.iterator();

        while(i.hasNext())
        {

            GameObject door = (GameObject)i.next();

            if(door.getZ() == z)
            {
                Vector3D doorLoc = door.getLocation();
                doorLoc.setTo(door.getX() + 41 * xOffset, 0, z + 45 * zOffset);
                door.getTransform().setVelocity(doorLoc);
                door.getTransform().setAngleVelocityY(0.785398F, 2);
            }
        }
    }

    // Grab Object --------------------------------------------------------------------------------------------------//
    private void grabobject(Player player) {

      
    }

 // Kill Bandit --------------------------------------------------------------------------------------------------//
    private void killbandit(Player player) {
    	System.out.println("Player amunition is  "+ player.getammunitiont() );
    	System.out.println("bandit life is  "+ player.getbanditlife());
    	System.out.println("current bandit is  "+ player.currentbandit());
    	
    	if (player.getammunitiont() > 3 && player.getbanditlife()>0){
    		player.setbanditlife(player.getbanditlife()-((int) (Math.random()*20+5)));
    		player.setammunition((player.getammunitiont() - 3));
    		playfireSFX();
    		playShellsSFX();
    		if(player.getbanditlife()<= 0){
    			player.setisBanditdead(true);
    			player.setbanditkilled(player.getbanditkilled()+1);
    			player.setbandit(player.currentbandit()+1);
    			
    			
    			// After timer ends open the door
                TimerTask animAction = new TimerTask() { public void run() { displayBandkilled = false; } };
                
                // Start Timer with delay amount to open door
                killeBanditTimer.schedule(animAction, 3000);

                displayBandkilled = true;
    		}
    		
    	}
    		
        if(player.isBanditdead())
        {
            if(player.currentbandit() < 1 && player.getbanditkilled() == 1)
            {
                getBandit(115);
                player.setbandit(1);

                killeBanditTimer.cancel();
                killeBanditTimer = new Timer();

                // After timer ends open the door
                TimerTask animAction = new TimerTask() { public void run() { displayBanditisDead = false; } };

                // Start Timer with delay amount to open door
                killeBanditTimer.schedule(animAction, 3000);

                displayBanditisDead = true;
            }

            if(player.currentbandit() < 2 && player.getbanditkilled() == 2)
            {
                getBandit(1300);
                player.setbandit(2);

                killeBanditTimer.cancel();
                killeBanditTimer = new Timer();

                // After timer ends open the door
                TimerTask animAction = new TimerTask() { public void run() { displayBanditisDead = false; } };

                // Start Timer with delay amount to open door
                killeBanditTimer.schedule(animAction, 3000);

                displayBanditisDead = true;
            }

            if(player.currentbandit() < 3 && player.getbanditkilled() == 3)
            {
                getBandit(1280);
                player.setbandit(3);

                killeBanditTimer.cancel();
                killeBanditTimer = new Timer();

                // After timer ends open the door
                TimerTask animAction = new TimerTask() { public void run() { displayBanditisDead = false; } };

                // Start Timer with delay amount to open door
                killeBanditTimer.schedule(animAction, 3000);

                displayBanditisDead = true;
            }
            
            player.setisBanditdead(false);
        }
    }

    // bandit killed ------------------------------------------------------------------------------------------------------//
    private void getBandit(int z) {

        Iterator i = gameObjectManager.iterator();

        while(i.hasNext())
        {

            GameObject bandit = (GameObject)i.next();

            if(bandit.getZ() == z) bandit.setState(GameObject.STATE_DESTROYED);
        }
    }
    
// // firing --------------------------------------------------------------------------------------------//
//    public void fireProjectile(Player player) {
//    	
//    	if (player.getammunitiont() > 3){
//    		player.setbanditlife(player.getbanditlife()-((int) (Math.random()*20+5)));
//    		playfireSFX();
//    		playShellsSFX();
//    		
////            float x = -getTransform().getSinAngleY();
////            float z = -getTransform().getCosAngleY();
////            float cosX = getTransform().getCosAngleX();
////            float sinX = getTransform().getSinAngleX();
////            Projectile blast = new Projectile(
////                (PolygonGroup)blastModel.clone(),
////                new Vector3D(cosX*x, sinX, cosX*z),
////                null,
////                40, 60);
////            float dist = getBounds().getRadius() +
////                blast.getBounds().getRadius();
////            // blast starting location needs work. looks like
////            // the blast is coming out of your forehead when
////            // you're shooting down.
////            blast.getLocation().setTo(
////                getX() + x*dist,
////                getY() + BULLET_HEIGHT,
////                getZ() + z*dist);
////
////            // "spawns" the new game object
////            addSpawn(blast);
////
////            // make a "virtual" noise that bots can "hear"
////            // (500 milliseconds)
////            makeNoise(500);
//            
//    	}
//
//    }
    
} // End Dark Realm Core
