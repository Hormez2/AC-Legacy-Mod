package dev.adventurecraft.awakening.common;

import java.util.Random;

import dev.adventurecraft.awakening.extension.client.ExMinecraft;
import dev.adventurecraft.awakening.extension.world.ExWorld;
import dev.adventurecraft.awakening.extension.world.ExWorldProperties;
import net.minecraft.client.SingleplayerInteractionManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextboxWidget;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class GuiCreateNewMap extends Screen {

    private Screen parent;
    private TextboxWidget textboxMapName;
    private TextboxWidget textboxSeed;
    private String folderName;
    private boolean createClicked;
    private WorldGenProperties worldGenProps = new WorldGenProperties();
    GuiSlider2 sliderMapSize;
    GuiSlider2 sliderWaterLevel;
    GuiSlider2 sliderFracHorizontal;
    GuiSlider2 sliderFracVertical;
    GuiSlider2 sliderMaxAvgDepth;
    GuiSlider2 sliderMaxAvgHeight;
    GuiSlider2 sliderVolatility1;
    GuiSlider2 sliderVolatility2;
    GuiSlider2 sliderVolatilityWeight1;
    GuiSlider2 sliderVolatilityWeight2;

    public GuiCreateNewMap(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void tick() {
        this.textboxMapName.tick();
        this.textboxSeed.tick();
    }

    @Override
    public void initVanillaScreen() {
        TranslationStorage ts = TranslationStorage.getInstance();
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.buttons.add(new ButtonWidget(0, this.width / 2 - 205, 200, "Create Map"));
        this.buttons.add(new ButtonWidget(1, this.width / 2 + 5, 200, ts.translate("gui.cancel")));
        this.textboxMapName = new TextboxWidget(this, this.textRenderer, this.width / 2 - 100, 38, 200, 20, ts.translate("selectWorld.newWorld"));
        this.textboxMapName.selected = true;
        this.textboxMapName.setMaxLength(32);
        this.textboxSeed = new TextboxWidget(this, this.textRenderer, this.width / 2 - 100, 62, 200, 20, "");
        int var2 = this.width / 2 - 4 - 150;
        int var3 = this.width / 2 + 4;
        WorldGenProperties wgp = this.worldGenProps;
        this.sliderMapSize = new GuiSlider2(2, var2, 88, 10, "", (float) (wgp.mapSize - 100.0D) / 650.0F);
        this.sliderWaterLevel = new GuiSlider2(2, var3, 88, 10, "", (float) wgp.waterLevel / 128.0F);
        this.sliderFracHorizontal = new GuiSlider2(2, var2, 109, 10, "", (float) wgp.fractureHorizontal / 2.0F);
        this.sliderFracVertical = new GuiSlider2(2, var3, 109, 10, "", (float) wgp.fractureVertical / 2.0F);
        this.sliderMaxAvgDepth = new GuiSlider2(2, var2, 130, 10, "", (float) (wgp.maxAvgDepth + 5.0D) / 10.0F);
        this.sliderMaxAvgHeight = new GuiSlider2(2, var3, 130, 10, "", (float) (wgp.maxAvgHeight + 5.0D) / 10.0F);
        this.sliderVolatility1 = new GuiSlider2(2, var2, 151, 10, "", (float) wgp.volatility1 / 5.0F);
        this.sliderVolatility2 = new GuiSlider2(2, var3, 151, 10, "", (float) wgp.volatility2 / 5.0F);
        this.sliderVolatilityWeight1 = new GuiSlider2(2, var2, 172, 10, "", (float) (wgp.volatilityWeight1 + 0.5D));
        this.sliderVolatilityWeight2 = new GuiSlider2(2, var3, 172, 10, "", (float) (wgp.volatilityWeight2 - 0.5D));
        this.updateSliders();
        this.buttons.add(this.sliderMapSize);
        this.buttons.add(this.sliderWaterLevel);
        this.buttons.add(this.sliderFracHorizontal);
        this.buttons.add(this.sliderFracVertical);
        this.buttons.add(this.sliderMaxAvgDepth);
        this.buttons.add(this.sliderMaxAvgHeight);
        this.buttons.add(this.sliderVolatility1);
        this.buttons.add(this.sliderVolatility2);
        this.buttons.add(this.sliderVolatilityWeight1);
        this.buttons.add(this.sliderVolatilityWeight2);
    }

    private void updateSliders() {
        WorldGenProperties wgp = this.worldGenProps;
        wgp.mapSize = this.sliderMapSize.sliderValue * 650.0F + 100.0F;
        wgp.waterLevel = (int) (128.0F * this.sliderWaterLevel.sliderValue);
        wgp.fractureHorizontal = (double) this.sliderFracHorizontal.sliderValue * 2.0D;
        wgp.fractureVertical = (double) this.sliderFracVertical.sliderValue * 2.0D;
        wgp.maxAvgDepth = (double) this.sliderMaxAvgDepth.sliderValue * 10.0D - 5.0D;
        wgp.maxAvgHeight = (double) this.sliderMaxAvgHeight.sliderValue * 10.0D - 5.0D;
        wgp.volatility1 = (double) this.sliderVolatility1.sliderValue * 5.0D;
        wgp.volatility2 = (double) this.sliderVolatility2.sliderValue * 5.0D;
        wgp.volatilityWeight1 = (double) this.sliderVolatilityWeight1.sliderValue - 0.5D;
        wgp.volatilityWeight2 = (double) this.sliderVolatilityWeight2.sliderValue + 0.5D;
        this.sliderMapSize.text = String.format("Map Size: %.1f", wgp.mapSize);
        this.sliderWaterLevel.text = String.format("Water Level: %d", wgp.waterLevel);
        this.sliderFracHorizontal.text = String.format("Fracture Horizontal: %.2f", wgp.fractureHorizontal);
        this.sliderFracVertical.text = String.format("Fracture Vertical: %.2f", wgp.fractureVertical);
        this.sliderMaxAvgDepth.text = String.format("Max Avg Depth: %.2f", wgp.maxAvgDepth);
        this.sliderMaxAvgHeight.text = String.format("Max Avg Height: %.2f", wgp.maxAvgHeight);
        this.sliderVolatility1.text = String.format("Volatility 1: %.2f", wgp.volatility1);
        this.sliderVolatility2.text = String.format("Volatility 2: %.2f", wgp.volatility2);
        this.sliderVolatilityWeight1.text = String.format("Volatility Weight 1: %.2f", wgp.volatilityWeight1);
        this.sliderVolatilityWeight2.text = String.format("Volatility Weight 2: %.2f", wgp.volatilityWeight2);
    }

    @Override
    public void onClose() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (!button.active) {
            return;
        }

        if (button.id == 1) {
            this.client.openScreen(this.parent);
        } else if (button.id == 0) {
            this.client.openScreen(null);
            if (this.createClicked) {
                return;
            }

            this.createClicked = true;
            long wSeed = (new Random()).nextLong();
            String wSeedStr = this.textboxSeed.getText();
            if (!MathHelper.isStringEmpty(wSeedStr)) {
                try {
                    long parsedSeed = Long.parseLong(wSeedStr);
                    if (parsedSeed != 0L) {
                        wSeed = parsedSeed;
                    }
                } catch (NumberFormatException var7) {
                    wSeed = wSeedStr.hashCode();
                }
            }

            this.client.interactionManager = new SingleplayerInteractionManager(this.client);
            AC_DebugMode.levelEditing = true;
            String wName = this.textboxMapName.getText().trim();
            ((ExMinecraft) this.client).saveMapUsed(wName, wName);
            World world = ((ExMinecraft) this.client).getWorld(wName, wSeed, wName);
            WorldGenProperties wgpS = this.worldGenProps;
            WorldGenProperties wgpD = ((ExWorldProperties) world.properties).getWorldGenProps();
            wgpD.useImages = wgpS.useImages;
            wgpD.mapSize = wgpS.mapSize;
            wgpD.waterLevel = wgpS.waterLevel;
            wgpD.fractureHorizontal = wgpS.fractureHorizontal;
            wgpD.fractureVertical = wgpS.fractureVertical;
            wgpD.maxAvgDepth = wgpS.maxAvgDepth;
            wgpD.maxAvgHeight = wgpS.maxAvgHeight;
            wgpD.volatility1 = wgpS.volatility1;
            wgpD.volatility2 = wgpS.volatility2;
            wgpD.volatilityWeight1 = wgpS.volatilityWeight1;
            wgpD.volatilityWeight2 = wgpS.volatilityWeight2;
            ((ExWorld) world).updateChunkProvider();
            this.client.notifyStatus(world, "Generating level");
            this.client.openScreen(null);
        }
    }

    @Override
    protected void keyPressed(char var1, int var2) {
        this.textboxMapName.keyPressed(var1, var2);
        this.textboxSeed.keyPressed(var1, var2);
        if (var1 == 13) {
            this.buttonClicked((ButtonWidget) this.buttons.get(0));
        }

        ((ButtonWidget) this.buttons.get(0)).active = this.textboxMapName.getText().length() > 0;
    }

    @Override
    protected void mouseClicked(int var1, int var2, int var3) {
        super.mouseClicked(var1, var2, var3);
        this.textboxMapName.mouseClicked(var1, var2, var3);
        this.textboxSeed.mouseClicked(var1, var2, var3);
    }

    @Override
    public void render(int mouseX, int mouseY, float deltaTime) {
        TranslationStorage ts = TranslationStorage.getInstance();
        this.renderBackground();
        this.drawTextWithShadowCentred(this.textRenderer, "Create Random Map", this.width / 2, 20, 16777215);
        String var5 = "Map Name:";
        String var6 = "Seed:";
        this.drawTextWithShadow(this.textRenderer, var5, this.width / 2 - 110 - this.textRenderer.getTextWidth(var5), 44, 10526880);
        this.drawTextWithShadow(this.textRenderer, var6, this.width / 2 - 110 - this.textRenderer.getTextWidth(var6), 68, 10526880);
        this.textboxMapName.draw();
        this.textboxSeed.draw();
        this.updateSliders();
        super.render(mouseX, mouseY, deltaTime);
    }
}
