
package babayega;

import java.util.*;
import Math.*;
import game.*;
import scripting.*;

// The Dark Realm 3D Class ------------------------------------------------------------------------------------------//
public class Babayega3D extends BabayegaMain {

    public static void main(String[] args) { new Babayega3D(args, "images/level1.map").run(); }

    protected ScriptManager   scriptManager;

    protected GameTaskManager gameTaskManager;

    public Babayega3D(String[] args, String defaultMap) { super(args, defaultMap); }

    // Init ---------------------------------------------------------------------------------------------------------//
    public void init() {

        super.init();

        gameTaskManager = new GameTaskManager();

        scriptManager   = new ScriptManager();

        scriptManager.setupLevel(gameObjectManager, gameTaskManager, new String[]
                                         { "src/Resources/Scripts/main.bsh", "src/Resources/Scripts/level1.bsh" });
    }

    // Create Game Objects ------------------------------------------------------------------------------------------//
    protected void createGameObjects(List mapObjects) {

        Iterator i = mapObjects.iterator();

        while (i.hasNext())
        {
            Object object = i.next();

            if(object instanceof PolygonGroup)
            {
                PolygonGroup group = (PolygonGroup)object;

                gameObjectManager.add(new GameObject(group));
            }
            else if(object instanceof GameObject)
            {
                gameObjectManager.add((GameObject)object);
            }
        }
    }

    // Update World -------------------------------------------------------------------------------------------------//
    public void updateWorld(long elapsedTime) {

        super.updateWorld(elapsedTime);

        gameTaskManager.update(elapsedTime);
    }

} // End Dark Realm 3D Class