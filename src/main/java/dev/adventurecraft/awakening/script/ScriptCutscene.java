package dev.adventurecraft.awakening.script;

import dev.adventurecraft.awakening.common.AC_CutsceneCamera;
import dev.adventurecraft.awakening.common.AC_CutsceneCameraBlendType;
import dev.adventurecraft.awakening.extension.client.ExMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public class ScriptCutscene {

    private final AC_CutsceneCamera cutsceneCamera;
    public boolean pauseGame = true;

    public ScriptCutscene(Level level) {
        this.cutsceneCamera = new AC_CutsceneCamera(level);
    }

    public void start() {
        ExMinecraft mc = (ExMinecraft) Minecraft.instance;
        mc.setCutsceneCamera(this.cutsceneCamera);
        mc.getCutsceneCamera().startCamera();
        mc.setCameraActive(true);
        mc.setCameraPause(pauseGame);
    }

    public void clear() {
        cutsceneCamera.clearPoints();
    }

    public void addCameraPoint(float time, float x, float y, float z, float yaw, float pitch, int blendId) {
        if (blendId < 0 || blendId > 2) {
            Minecraft.instance.gui.addMessage(String.format(
                "(JS) Cutscene.addCameraPoint: Wrong Blend Id '%s'",
                blendId
            ));
            return;
        }
        AC_CutsceneCameraBlendType cutsceneCameraBlendType = AC_CutsceneCameraBlendType.get(blendId);
        cutsceneCamera.addCameraPoint(time, x, y, z, yaw, pitch, cutsceneCameraBlendType);
    }

    public void addCameraPoint(float time, ScriptVec3 vec3, float yaw, float pitch, int blendId) {
        if (blendId < 0 || blendId > 2) {
            Minecraft.instance.gui.addMessage(String.format(
                "(JS) Cutscene.addCameraPoint: Wrong Blend Id '%s'",
                blendId
            ));
            return;
        }
        AC_CutsceneCameraBlendType cutsceneCameraBlendType = AC_CutsceneCameraBlendType.get(blendId);
        cutsceneCamera.addCameraPoint(
            time,
            (float) vec3.x,
            (float) vec3.y,
            (float) vec3.z,
            yaw,
            pitch,
            cutsceneCameraBlendType
        );
    }

    public void addCameraPoint(float time, ScriptVec3 vec3, ScriptVecRot rot, int blendId) {
        if (blendId < 0 || blendId > 2) {
            Minecraft.instance.gui.addMessage(String.format(
                "(JS) Cutscene.addCameraPoint: Wrong Blend Id '%s'",
                blendId
            ));
            return;
        }
        AC_CutsceneCameraBlendType cutsceneCameraBlendType = AC_CutsceneCameraBlendType.get(blendId);
        cutsceneCamera.addCameraPoint(
            time,
            (float) vec3.x,
            (float) vec3.y,
            (float) vec3.z,
            (float) rot.yaw,
            (float) rot.pitch,
            cutsceneCameraBlendType
        );
    }
}