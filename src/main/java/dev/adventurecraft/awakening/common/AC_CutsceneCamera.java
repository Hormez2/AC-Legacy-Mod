package dev.adventurecraft.awakening.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class AC_CutsceneCamera {

    public long startTime;
    public AC_CutsceneCameraPoint curPoint = new AC_CutsceneCameraPoint(
        0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, AC_CutsceneCameraBlendType.NONE);
    public AC_CutsceneCameraPoint prevPrevPoint;
    public AC_CutsceneCameraPoint prevPoint;
    public ArrayList<AC_CutsceneCameraPoint> cameraPoints = new ArrayList<>();
    public ArrayList<Vec3d> linePoints = new ArrayList<>();
    public AC_CutsceneCameraBlendType startType = AC_CutsceneCameraBlendType.QUADRATIC;

    public void addCameraPoint(
        float time, float x, float y, float z, float yaw, float pitch, AC_CutsceneCameraBlendType type) {
        int pointCount = 0;

        for (AC_CutsceneCameraPoint var10 : this.cameraPoints) {
            if (time < var10.time) {
                break;
            }
            ++pointCount;
        }

        this.cameraPoints.add(pointCount, new AC_CutsceneCameraPoint(time, x, y, z, yaw, pitch, type));
        this.fixYawPitch(0.0F, 0.0F);
    }

    public void loadCameraEntities() {

        for (Entity entity : (List<Entity>) Minecraft.instance.world.entities) {
            if (entity instanceof AC_EntityCamera) {
                entity.remove();
            }
        }

        for (AC_CutsceneCameraPoint point : this.cameraPoints) {
            var camera = new AC_EntityCamera(Minecraft.instance.world, point.time, point.blendType, point.cameraID);
            camera.method_1338(point.posX, point.posY, point.posZ, point.rotYaw, point.rotPitch);
            Minecraft.instance.world.spawnEntity(camera);
        }

        var camera = new AC_CutsceneCamera();

        for (AC_CutsceneCameraPoint point : this.cameraPoints) {
            camera.addCameraPoint(
                point.time, point.posX, point.posY, point.posZ, point.rotYaw, point.rotPitch, point.blendType);
        }

        AC_CutsceneCameraPoint prevPoint = null;
        this.linePoints.clear();

        for (AC_CutsceneCameraPoint point : this.cameraPoints) {
            if (prevPoint != null) {
                for (int i = 0; i < 25; ++i) {
                    float currTime = (float) (i + 1) / 25.0F;
                    float nextTime = this.lerp(currTime, prevPoint.time, point.time);
                    AC_CutsceneCameraPoint nextPoint = camera.getPoint(nextTime);
                    Vec3d linePoint = Vec3d.create(nextPoint.posX, nextPoint.posY, nextPoint.posZ);
                    this.linePoints.add(linePoint);
                }
            } else {
                this.linePoints.add(Vec3d.create(point.posX, point.posY, point.posZ));
            }
            prevPoint = point;
        }
    }

    public void drawLines(LivingEntity entity, float time) {
        double prX = entity.prevRenderX + (entity.x - entity.prevRenderX) * (double) time;
        double prY = entity.prevRenderY + (entity.y - entity.prevRenderY) * (double) time;
        double prZ = entity.prevRenderZ + (entity.z - entity.prevRenderZ) * (double) time;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 0.2F, 0.0F, 1.0F);
        GL11.glLineWidth(5.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator ts = Tessellator.INSTANCE;
        ts.start(3);

        for (Vec3d linePoint : this.linePoints) {
            ts.addVertex(linePoint.x - prX, linePoint.y - prY, linePoint.z - prZ);
        }

        ts.tessellate();
        GL11.glLineWidth(1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void fixYawPitch(float yaw, float pitch) {
        float yawSum = 0.0F;
        float pitchSum = 0.0F;
        var prevPoint = new AC_CutsceneCameraPoint(
            0.0F, 0.0F, 0.0F, 0.0F, yaw, pitch, AC_CutsceneCameraBlendType.NONE);

        for (AC_CutsceneCameraPoint point : this.cameraPoints) {
            if (prevPoint != null) {
                point.rotYaw += yawSum;
                boolean unfixed = true;

                do {
                    float yawDiff = point.rotYaw - prevPoint.rotYaw;
                    if (yawDiff > 180.0F) {
                        yawSum -= 360.0F;
                        point.rotYaw -= 360.0F;
                    } else if (yawDiff < -180.0F) {
                        yawSum += 360.0F;
                        point.rotYaw += 360.0F;
                    } else {
                        unfixed = false;
                    }
                } while (unfixed);

                point.rotPitch += pitchSum;
                unfixed = true;

                do {
                    float pitchDiff = point.rotPitch - prevPoint.rotPitch;
                    if (pitchDiff > 180.0F) {
                        pitchSum -= 360.0F;
                        point.rotPitch -= 360.0F;
                    } else if (pitchDiff < -180.0F) {
                        pitchSum += 360.0F;
                        point.rotPitch += 360.0F;
                    } else {
                        unfixed = false;
                    }
                } while (unfixed);
            }
            prevPoint = point;
        }
    }

    public void clearPoints() {
        this.prevPrevPoint = null;
        this.prevPoint = null;
        this.cameraPoints.clear();
    }

    public void startCamera() {
        this.prevPrevPoint = null;
        this.prevPoint = null;
        this.startTime = Minecraft.instance.world.getWorldTime();
    }

    private float cubicInterpolation(float mu, float y0, float y1, float y2, float y3) {
        float mu2 = mu * mu;
        float a0 = -0.5F * y0 + 1.5F * y1 - 1.5F * y2 + 0.5F * y3;
        float a1 = y0 - 2.5F * y1 + 2.0F * y2 - 0.5F * y3;
        float a2 = -0.5F * y0 + 0.5F * y2;
        return a0 * mu * mu2 + a1 * mu2 + a2 * mu + y1;
    }

    private float lerp(float amount, float start, float end) {
        return (1.0F - amount) * start + amount * end;
    }

    public boolean isEmpty() {
        return this.cameraPoints.isEmpty();
    }

    public float getLastTime() {
        return this.cameraPoints.get(this.cameraPoints.size() - 1).time;
    }

    public AC_CutsceneCameraPoint getCurrentPoint(float time) {
        float normalizedTime = ((float) (Minecraft.instance.world.getWorldTime() - this.startTime) + time) / 20.0F;
        return this.getPoint(normalizedTime);
    }

    public AC_CutsceneCameraPoint getPoint(float time) {
        if (this.prevPoint == null) {
            if (this.cameraPoints.isEmpty()) {
                return this.curPoint;
            }

            if (this.startType != AC_CutsceneCameraBlendType.NONE) {
                AbstractClientPlayerEntity entity = Minecraft.instance.player;
                this.prevPoint = new AC_CutsceneCameraPoint(
                    0.0F, (float) entity.x, (float) entity.y, (float) entity.z, entity.yaw, entity.pitch, this.startType);
                this.fixYawPitch(entity.yaw, entity.pitch);
            } else {
                AC_CutsceneCameraPoint point = this.cameraPoints.get(0);
                this.prevPoint = new AC_CutsceneCameraPoint(
                    0.0F, point.posX, point.posY, point.posZ, point.rotYaw, point.rotPitch, this.startType);
            }
        }

        if (!(this.prevPoint.time <= time) || this.cameraPoints.isEmpty()) {
            return this.prevPoint;
        }

        AC_CutsceneCameraPoint point = this.cameraPoints.get(0);

        while (point != null && point.time < time && !this.cameraPoints.isEmpty()) {
            this.prevPrevPoint = this.prevPoint;
            this.prevPoint = this.cameraPoints.remove(0);
            point = null;
            if (this.cameraPoints.isEmpty()) {
                continue;
            }
            point = this.cameraPoints.get(0);
            if (this.prevPrevPoint == null) {
                continue;
            }

            float timeSincePrev = point.time - this.prevPoint.time;
            float prevTimeSincePrev = this.prevPoint.time - this.prevPrevPoint.time;
            if (prevTimeSincePrev > 0.0F) {
                float factor = timeSincePrev / prevTimeSincePrev;
                this.prevPrevPoint = new AC_CutsceneCameraPoint(
                    0.0F,
                    this.prevPoint.posX - factor * (this.prevPoint.posX - this.prevPrevPoint.posX),
                    this.prevPoint.posY - factor * (this.prevPoint.posY - this.prevPrevPoint.posY),
                    this.prevPoint.posZ - factor * (this.prevPoint.posZ - this.prevPrevPoint.posZ),
                    this.prevPoint.rotYaw - factor * (this.prevPoint.rotYaw - this.prevPrevPoint.rotYaw),
                    this.prevPoint.rotPitch - factor * (this.prevPoint.rotPitch - this.prevPrevPoint.rotPitch),
                    AC_CutsceneCameraBlendType.NONE);
            } else {
                this.prevPrevPoint = new AC_CutsceneCameraPoint(
                    0.0F,
                    this.prevPoint.posX,
                    this.prevPoint.posY,
                    this.prevPoint.posZ,
                    this.prevPoint.rotYaw,
                    this.prevPoint.rotPitch,
                    AC_CutsceneCameraBlendType.NONE);
            }
        }

        if (point == null) {
            return this.prevPoint;
        }

        if (this.prevPrevPoint == null) {
            this.prevPrevPoint = new AC_CutsceneCameraPoint(
                0.0F,
                2.0F * this.prevPoint.posX - point.posX,
                2.0F * this.prevPoint.posY - point.posY,
                2.0F * this.prevPoint.posZ - point.posZ,
                2.0F * this.prevPoint.rotYaw - point.rotYaw,
                2.0F * this.prevPoint.rotPitch - point.rotPitch,
                AC_CutsceneCameraBlendType.NONE);
        }

        AC_CutsceneCameraPoint nextPoint;
        if (this.cameraPoints.size() > 1) {
            nextPoint = this.cameraPoints.get(1);
            float timeSincePrev = point.time - this.prevPoint.time;
            float timeToNext = nextPoint.time - point.time;
            if (timeToNext > 0.0F) {
                float factor = timeSincePrev / timeToNext;
                nextPoint = new AC_CutsceneCameraPoint(
                    0.0F,
                    point.posX + factor * (nextPoint.posX - point.posX),
                    point.posY + factor * (nextPoint.posY - point.posY),
                    point.posZ + factor * (nextPoint.posZ - point.posZ),
                    point.rotYaw + factor * (nextPoint.rotYaw - point.rotYaw),
                    point.rotPitch + factor * (nextPoint.rotPitch - point.rotPitch),
                    AC_CutsceneCameraBlendType.NONE);
            } else {
                nextPoint = new AC_CutsceneCameraPoint(
                    0.0F, point.posX, point.posY, point.posZ, point.rotYaw, point.rotPitch, AC_CutsceneCameraBlendType.NONE);
            }
        } else {
            nextPoint = new AC_CutsceneCameraPoint(
                0.0F,
                2.0F * point.posX - this.prevPoint.posX,
                2.0F * point.posY - this.prevPoint.posY,
                2.0F * point.posZ - this.prevPoint.posZ,
                2.0F * point.rotYaw - this.prevPoint.rotYaw,
                2.0F * point.rotPitch - this.prevPoint.rotPitch,
                AC_CutsceneCameraBlendType.NONE);
        }

        float amount = (time - this.prevPoint.time) / (point.time - this.prevPoint.time);
        this.curPoint.time = time;

        switch (this.prevPoint.blendType) {
            case LINEAR:
                this.curPoint.posX = this.lerp(amount, this.prevPoint.posX, point.posX);
                this.curPoint.posY = this.lerp(amount, this.prevPoint.posY, point.posY);
                this.curPoint.posZ = this.lerp(amount, this.prevPoint.posZ, point.posZ);
                this.curPoint.rotYaw = this.lerp(amount, this.prevPoint.rotYaw, point.rotYaw);
                this.curPoint.rotPitch = this.lerp(amount, this.prevPoint.rotPitch, point.rotPitch);
                break;

            case QUADRATIC:
                //noinspection SuspiciousNameCombination
                this.curPoint.posX = this.cubicInterpolation(amount, this.prevPrevPoint.posX, this.prevPoint.posX, point.posX, nextPoint.posX);
                this.curPoint.posY = this.cubicInterpolation(amount, this.prevPrevPoint.posY, this.prevPoint.posY, point.posY, nextPoint.posY);
                this.curPoint.posZ = this.cubicInterpolation(amount, this.prevPrevPoint.posZ, this.prevPoint.posZ, point.posZ, nextPoint.posZ);
                this.curPoint.rotYaw = this.cubicInterpolation(amount, this.prevPrevPoint.rotYaw, this.prevPoint.rotYaw, point.rotYaw, nextPoint.rotYaw);
                this.curPoint.rotPitch = this.cubicInterpolation(amount, this.prevPrevPoint.rotPitch, this.prevPoint.rotPitch, point.rotPitch, nextPoint.rotPitch);
                break;

            case NONE:
                this.curPoint.posX = this.prevPoint.posX;
                this.curPoint.posY = this.prevPoint.posY;
                this.curPoint.posZ = this.prevPoint.posZ;
                this.curPoint.rotYaw = this.prevPoint.rotYaw;
                this.curPoint.rotPitch = this.prevPoint.rotPitch;
                break;
        }

        return this.curPoint;
    }

    public void deletePoint(int id) {
        ArrayList<AC_CutsceneCameraPoint> points = this.cameraPoints;
        for (int i = 0; i < points.size(); i++) {
            AC_CutsceneCameraPoint point = points.get(i);
            if (point.cameraID == id) {
                this.cameraPoints.remove(i);
                break;
            }
        }
    }

    public void setPointType(int id, AC_CutsceneCameraBlendType type) {
        for (AC_CutsceneCameraPoint point : this.cameraPoints) {
            if (point.cameraID == id) {
                point.blendType = type;
                this.loadCameraEntities();
                break;
            }
        }
    }

    public void setPointTime(int id, float time) {
        for (AC_CutsceneCameraPoint point : this.cameraPoints) {
            if (point.cameraID == id) {
                point.time = time;
                this.loadCameraEntities();
                break;
            }
        }
    }
}
