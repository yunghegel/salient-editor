package core.attributes;

public class PhysicsAttributes extends Attribute
{

    public static final int DYNAMIC = 1;
    public static final int STATIC = 2;
    public static final int KINEMATIC = 4;
    public static final int GHOST_OBJECT = 8;
    public static final int DEBUG_DRAW_ENABLED = 16;

    protected static long Mask = DYNAMIC | STATIC | KINEMATIC | GHOST_OBJECT | DEBUG_DRAW_ENABLED;

    public PhysicsAttributes(int flags) {
        super(flags);
    }

}
