package dev.adventurecraft.awakening.script;

public class ScriptVec3Block extends ScriptVec3 {

    public int blockId;
    public int blockFace;

    public ScriptVec3Block(double x, double y, double z, int blockId, int blockFace) {
        super(x, y, z);
        this.blockId = blockId;
        this.blockFace = blockFace;
    }

    public ScriptVec3Block(float x, float y, float z, int blockId, int blockFace) {
        super(x, y, z);
        this.blockId = blockId;
        this.blockFace = blockFace;
    }

    public ScriptVec3Block add(ScriptVec3Block vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }


    public ScriptVec3Block subtract(ScriptVec3Block vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        return this;
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double distance(ScriptVec3Block vec) {
        double dX = this.x - vec.x;
        double dY = this.y - vec.y;
        double dZ = this.z - vec.z;
        return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
    }

    public ScriptVec3Block scale(double value) {
        this.x *= value;
        this.y *= value;
        this.z *= value;
        return this;
    }
}
