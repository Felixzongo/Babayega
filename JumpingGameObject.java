
package babayega;

import Math.*;
import game.*;

public class JumpingGameObject extends GameObject {

    public static final float DEFAULT_JUMP_HEIGHT = 64;

    protected float jumpVelocity;

    public JumpingGameObject(PolygonGroup group) {

        super(group);

        setJumpHeight(DEFAULT_JUMP_HEIGHT);
    }

    // Sets how high this GameObject can jump.
    public void setJumpHeight(float jumpHeight) { jumpVelocity = Physics.getInstance().getJumpVelocity(jumpHeight); }


    /**
        Causes this GameObject to jump if the jumping flag is
        set and this object is not already jumping.
    */
    public void setJumping(boolean isJumping) {

        if(isJumping() != isJumping)
        {

            super.setJumping(isJumping);

            if(isJumping) { Physics.getInstance().jump(this, jumpVelocity); }
        }
    }

    // Notify Floor Collision ---------------------------------------------------------------------------------------//
    public void notifyFloorCollision() { setJumping(false); }

} // End Jumping Game Object