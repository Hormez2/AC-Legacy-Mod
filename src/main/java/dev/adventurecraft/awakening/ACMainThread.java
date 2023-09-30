package dev.adventurecraft.awakening;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.GameStartupError;
import net.minecraft.client.Minecraft;

import java.io.File;

@Environment(value = EnvType.CLIENT)
public final class ACMainThread extends Minecraft {

    public static File mapsDirectory;
    public static boolean glDebugLogs;
    public static boolean glDebugContext;
    public static boolean glDebugTrace;

    public ACMainThread(int width, int height, boolean fullScreen) {
        super(null, null, null, width, height, fullScreen);
    }

    @Override
    public void showGameStartupError(GameStartupError arg) {
        // TODO:
    }

    public static File getMapsDirectory() {
        if (mapsDirectory == null) {
            mapsDirectory = new File(getGameDirectory(), "../maps");
        }
        return mapsDirectory;
    }
}