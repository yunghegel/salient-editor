package core.attributes;

public class Attribute
{

    protected int flags;

    public Attribute(int flags) {
        this.flags = flags;
    }

    public void setFlag(int flag) {
        flags |= flag;
    }

    public void clearFlag(int flag) {
        flags &= ~flag;
    }

    public boolean checkFlag(int flag) {
        return ( flags & flag ) != 0;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public void addFlags(int flags) {
        this.flags |= flags;
    }

}
