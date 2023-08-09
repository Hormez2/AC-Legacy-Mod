package dev.adventurecraft.awakening.common;

import dev.adventurecraft.awakening.extension.block.ExBlock;
import dev.adventurecraft.awakening.extension.client.render.entity.ExItemRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.ItemRenderer;
import net.minecraft.client.render.entity.block.BlockEntityRenderer;
import net.minecraft.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class AC_TileEntityStoreRenderer extends BlockEntityRenderer {

    static ItemStack item = new ItemStack(0, 0, 0);
    static ItemEntity eItem = new ItemEntity(null, 0.0D, 0.0D, 0.0D, item);
    static ItemRenderer renderItem = new ItemRenderer();

    public AC_TileEntityStoreRenderer() {
        renderItem.setDispatcher(EntityRenderDispatcher.INSTANCE);
    }

    public void renderTileEntityStore(AC_TileEntityStore tileEntity, double x, double y, double z, float deltaTime) {
        if (tileEntity.buySupplyLeft != 0 && tileEntity.buyItemID != 0) {
            item.itemId = tileEntity.buyItemID;
            item.count = tileEntity.buyItemAmount;
            item.setMeta(tileEntity.buyItemDamage);
            eItem.world = tileEntity.world;
            eItem.setPosition(tileEntity.x, tileEntity.y, tileEntity.z);
            renderItem.render(eItem, x + 0.5D, y + 0.125D, z + 0.5D, 0.0F, 0.0F);
        }

        if (AC_DebugMode.active && tileEntity.tradeTrigger != null) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
            GL11.glLineWidth(6.0F);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glBegin(GL11.GL_LINES);

            for (int bX = tileEntity.tradeTrigger.minX; bX <= tileEntity.tradeTrigger.maxX; ++bX) {
                for (int bY = tileEntity.tradeTrigger.minY; bY <= tileEntity.tradeTrigger.maxY; ++bY) {
                    for (int bZ = tileEntity.tradeTrigger.minZ; bZ <= tileEntity.tradeTrigger.maxZ; ++bZ) {
                        Block block = Block.BY_ID[tileEntity.world.getBlockId(bX, bY, bZ)];
                        if (block != null && ((ExBlock) block).canBeTriggered()) {
                            GL11.glColor3f(0.0F, 0.0F, 0.0F);
                            GL11.glVertex3f(0.0F, 0.0F, 0.0F);
                            GL11.glColor3f(1.0F, 1.0F, 1.0F);
                            GL11.glVertex3f((float) (bX - tileEntity.x), (float) (bY - tileEntity.y), (float) (bZ - tileEntity.z));
                        }
                    }
                }
            }

            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glLineWidth(1.0F);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        }
    }

    public void render(BlockEntity entity, double x, double y, double z, float deltaTime) {
        this.renderTileEntityStore((AC_TileEntityStore) entity, x, y, z, deltaTime);
    }

    static {
        ((ExItemRenderer) renderItem).setScale(1.5F);
    }
}
