package net.specialattack.towerdefence.client.renderer;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.specialattack.towerdefence.tileentity.TileEntityTower;
import net.specialattack.towerdefence.towers.ITower;
import net.specialattack.towerdefence.towers.ITowerRenderHandler;
import net.specialattack.towerdefence.towers.TowerAoE;
import org.lwjgl.opengl.GL11;

public class BlockRendererTower implements ISimpleBlockRenderingHandler {

    private int renderId;

    public BlockRendererTower(int renderId) {
        this.renderId = renderId;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        block.setBlockBoundsForItemRender();
        BlockRendererTower.renderBox(block, metadata, renderer);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 0) {
            block.setBlockBoundsForItemRender();
            BlockRendererTower.renderBox(block, x, y, z, renderer);
        } else if (metadata == 1) {
            TileEntity tile = world.getBlockTileEntity(x, y - 1, z);

            if (tile != null && tile instanceof TileEntityTower && ((TileEntityTower) tile).towerInstance != null) {
                // Base
                BlockRendererTower.renderBox(block, x, y, z, 2, 4, 2, 12, 11, 12, renderer);

                //Legs
                BlockRendererTower.renderBox(block, x, y, z, 0.5F, 0, 0.5F, 4, 8, 4, renderer);
                BlockRendererTower.renderBox(block, x, y, z, 11.5F, 0, 0.5F, 4, 8, 4, renderer);
                BlockRendererTower.renderBox(block, x, y, z, 11.5F, 0, 11.5F, 4, 8, 4, renderer);
                BlockRendererTower.renderBox(block, x, y, z, 0.5F, 0, 11.5F, 4, 8, 4, renderer);

                //Decoration
                BlockRendererTower.renderBox(block, x, y, z, 1, 8, 1, 1, 8, 1, renderer);
                BlockRendererTower.renderBox(block, x, y, z, 1, 8, 14, 1, 8, 1, renderer);
                BlockRendererTower.renderBox(block, x, y, z, 14, 8, 14, 1, 8, 1, renderer);
                BlockRendererTower.renderBox(block, x, y, z, 14, 8, 1, 1, 8, 1, renderer);

                //Decoration
                BlockRendererTower.renderBox(block, x, y, z, 2, 15, 1, 12, 1, 1, renderer);
                BlockRendererTower.renderBox(block, x, y, z, 2, 15, 14, 12, 1, 1, renderer);
                BlockRendererTower.renderBox(block, x, y, z, 1, 15, 2, 1, 1, 12, renderer);
                BlockRendererTower.renderBox(block, x, y, z, 14, 15, 2, 1, 1, 12, renderer);

                ITower tower = ((TileEntityTower) tile).towerInstance.getTowerType();

                if (tower != null && tower instanceof TowerAoE) {
                    ITowerRenderHandler renderHandler = ((TowerAoE) tower).getRenderHandler();
                    if (renderHandler != null) {
                        renderHandler.renderStatic(tile, false, world, x, y, z, renderer);
                    }
                }

            }
        } else if (metadata == 2) {
            TileEntity tile = world.getBlockTileEntity(x, y - 2, z);

            if (tile != null && tile instanceof TileEntityTower && ((TileEntityTower) tile).towerInstance != null) {
                BlockRendererTower.renderBox(block, x, y, z, 6, 0, 6, 5, 1, 5, renderer);

                ITower tower = ((TileEntityTower) tile).towerInstance.getTowerType();

                if (tower != null && tower instanceof TowerAoE) {
                    ITowerRenderHandler renderHandler = ((TowerAoE) tower).getRenderHandler();
                    if (renderHandler != null) {
                        renderHandler.renderStatic(tile, true, world, x, y, z, renderer);
                    }
                }
            }
        } else {
            return false;
        }

        return true;
    }

    public static void renderBox(Block block, int x, int y, int z, RenderBlocks renderer) {
        renderer.setRenderBoundsFromBlock(block);
        renderer.renderStandardBlock(block, x, y, z);
    }

    public static void renderBox(Block block, int x, int y, int z, float startX, float startY, float startZ, float sizeX, float sizeY, float sizeZ, RenderBlocks renderer) {
        float pixel = 0.0625F;

        block.setBlockBounds(pixel * startX, pixel * startY, pixel * startZ, pixel * (startX + sizeX), pixel * (startY + sizeY), pixel * (startZ + sizeZ));
        BlockRendererTower.renderBox(block, x, y, z, renderer);
    }

    @Override
    public boolean shouldRender3DInInventory() {
        return true;
    }

    @Override
    public int getRenderId() {
        return this.renderId;
    }

    public static void renderBox(Block block, int metadata, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;

        renderer.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, metadata));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

}
