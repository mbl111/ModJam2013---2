
package net.specialattack.modjam.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.specialattack.modjam.tileentity.TileEntityTower;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockRendererTower implements ISimpleBlockRenderingHandler {

    private int renderId;

    public BlockRendererTower(int renderId) {
        this.renderId = renderId;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        block.setBlockBoundsForItemRender();
        this.renderBox(block, metadata, renderer);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 0) {
            block.setBlockBoundsForItemRender();
            this.renderBox(block, x, y, z, renderer);
        }
        else if (metadata == 1) {
            TileEntity tile = world.getBlockTileEntity(x, y - 1, z);

            if (tile instanceof TileEntityTower && ((TileEntityTower) tile).active) {
                float pixel = 0.0625F;

                block.setBlockBounds(pixel * 2F, pixel * 4F, pixel * 2F, pixel * 6F, pixel * 15F, pixel * 12F);
                this.renderBox(block, x, y, z, renderer);
                //this.towerBase.addBox(2, 4, 2, 4, 11, 12);
                //this.towerBase.addBox(10, 4, 2, 4, 11, 12);
                //this.towerBase.addBox(6, 4, 2, 4, 11, 4);
                //this.towerBase.addBox(6, 4, 10, 4, 11, 4);

                //Legs
                //this.towerBase.addBox(.5f, 0, .5f, 4, 8, 4);
                //this.towerBase.addBox(11.5f, 0, .5f, 4, 8, 4);
                //this.towerBase.addBox(11.5f, 0, 11.5f, 4, 8, 4);
                //this.towerBase.addBox(.5f, 0, 11.5f, 4, 8, 4);

                //Decoration
                //this.towerBase.addBox(1, 8, 1, 1, 8, 1);
                //this.towerBase.addBox(1, 8, 14, 1, 8, 1);
                //this.towerBase.addBox(14, 8, 14, 1, 8, 1);
                //this.towerBase.addBox(14, 8, 1, 1, 8, 1);

                //'pole'
                //this.towerBase.addBox(6, 13, 6, 4, 1, 4);

                //Decoration
                //this.towerBase.addBox(2, 15, 1, 12, 1, 1);
                //this.towerBase.addBox(2, 15, 14, 12, 1, 1);

                //this.towerBase.addBox(1, 15, 2, 1, 1, 12);
                //this.towerBase.addBox(14, 15, 2, 1, 1, 12);
            }
        }
        else {
            return false;
        }

        return true;
    }

    private void renderBox(Block block, int x, int y, int z, RenderBlocks renderer) {
        renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, 1.0F, 1.0F, 1.0F);
    }

    private void renderBox(Block block, int metadata, RenderBlocks renderer) {
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

    @Override
    public boolean shouldRender3DInInventory() {
        return true;
    }

    @Override
    public int getRenderId() {
        return this.renderId;
    }

}
