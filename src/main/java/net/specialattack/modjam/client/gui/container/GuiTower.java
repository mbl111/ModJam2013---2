
package net.specialattack.modjam.client.gui.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.specialattack.modjam.Assets;
import net.specialattack.modjam.blocks.BlockTower;
import net.specialattack.modjam.client.gui.GuiButtonTinyOverlay;
import net.specialattack.modjam.inventory.ContainerTower;
import net.specialattack.modjam.tileentity.TileEntityTower;
import net.specialattack.modjam.towers.ITower;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTower extends GuiContainer {

    public TileEntityTower tile;
    private ContainerTower container;

    private List<GuiButtonTinyOverlay> buttons;

    public GuiTower(TileEntityTower tile) {
        super(new ContainerTower(tile));
        this.tile = tile;
        this.container = (ContainerTower) this.inventorySlots;
        this.buttons = new ArrayList<GuiButtonTinyOverlay>();

        Block block = tile.getBlockType();
        if (block instanceof BlockTower) {
            List<ITower> towerTypes = ((BlockTower) block).towerTypes;

            for (int i = 0; i < towerTypes.size(); i++) {
                ITower tower = towerTypes.get(i);
                this.buttons.add(new GuiButtonTinyOverlay(tower.getIdentifier().hashCode(), 0, 0, 80, tower.getIdentifier(), tower.getIconLocation(), tower.getIconU(), tower.getIconV()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.clear();

        if (!this.container.activated) {
            int i = 0;
            for (GuiButtonTinyOverlay button : buttons) {
                button.xPosition = this.guiLeft + (i % 7) * 23 + 8;
                button.yPosition = this.guiTop + (i / 7) * 23 + 32;
                i++;
            }

            this.buttonList.addAll(this.buttons);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.drawButton && button.enabled) {
            this.mc.playerController.sendEnchantPacket(this.container.windowId, button.id);
        }
    }

    @Override
    public void updateScreen() {
        if (this.container.prevActivated != this.container.activated) {
            this.container.prevActivated = this.container.activated;
            this.initGui();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!this.container.activated) {
            for (GuiButtonTinyOverlay button : buttons) {
                if (button.drawButton) {
                    button.drawTooltips(mc, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String title = I18n.getString("container." + Assets.DOMAIN + "-tower");
        this.fontRenderer.drawString(title, this.xSize / 2 - this.fontRenderer.getStringWidth(title) / 2, 6, 0x404040);

        if (!this.container.activated) {
            this.fontRenderer.drawString(I18n.getString("container." + Assets.DOMAIN + "-tower.purchase"), 8, 20, 0x404040);
        }
        else {
            this.fontRenderer.drawString(I18n.getStringParams("container." + Assets.DOMAIN + "-tower.level", container.level), 8, 20, 0x404040);
            float speed = (float) container.speed / 20.0F;
            this.fontRenderer.drawString(I18n.getStringParams("container." + Assets.DOMAIN + "-tower.speed", speed), 8, 30, 0x404040);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Assets.GUI_SPAWNER);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

}
