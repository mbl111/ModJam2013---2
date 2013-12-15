
package net.specialattack.modjam.client.gui.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.specialattack.modjam.Assets;
import net.specialattack.modjam.blocks.BlockTower;
import net.specialattack.modjam.client.gui.GuiButtonOverlay;
import net.specialattack.modjam.client.gui.GuiButtonPriced;
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

    private List<GuiButtonPriced> buyButtons;

    public GuiTower(TileEntityTower tile) {
        super(new ContainerTower(tile));
        this.tile = tile;
        this.container = (ContainerTower) this.inventorySlots;
        this.buyButtons = new ArrayList<GuiButtonPriced>();

        Block block = tile.getBlockType();
        if (block instanceof BlockTower) {
            List<ITower> towerTypes = ((BlockTower) block).towerTypes;

            for (int i = 0; i < towerTypes.size(); i++) {
                ITower tower = towerTypes.get(i);
                this.buyButtons.add(new GuiButtonPriced(tower.getIdentifier().hashCode(), 0, 0, "tower." + tower.getIdentifier(), tower.getBuyPrice(), tower.getIconLocation(), tower.getIconU(), tower.getIconV()));
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
            for (GuiButtonPriced button : this.buyButtons) {
                button.xPosition = this.guiLeft + (i % 7) * 23 + 8;
                button.yPosition = this.guiTop + (i / 7) * 23 + 32;
                button.shouldDisable = !this.container.isMyName;
                i++;
            }

            this.buttonList.addAll(this.buyButtons);
        }
        else {
            this.buttonList.add(new GuiButtonPriced(0, this.guiLeft + 8, this.guiTop + 90, "container." + Assets.DOMAIN + "-tower.upgrade.level", this.container.prices[0], Assets.SHEET_TOWERS, 60, 20));
            this.buttonList.add(new GuiButtonPriced(1, this.guiLeft + 31, this.guiTop + 90, "container." + Assets.DOMAIN + "-tower.upgrade.damage", this.container.prices[1], Assets.SHEET_TOWERS, 40, 20));
            this.buttonList.add(new GuiButtonPriced(2, this.guiLeft + 54, this.guiTop + 90, "container." + Assets.DOMAIN + "-tower.upgrade.speed", this.container.prices[2], Assets.SHEET_TOWERS, 0, 20));
            this.buttonList.add(new GuiButtonPriced(3, this.guiLeft + 77, this.guiTop + 90, "container." + Assets.DOMAIN + "-tower.upgrade.range", this.container.prices[3], Assets.SHEET_TOWERS, 20, 20));
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
        if (this.container.updated) {
            this.container.updated = false;
            this.initGui();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        for (Object obj : this.buttonList) {
            if (obj instanceof GuiButtonOverlay) {
                GuiButtonOverlay button = (GuiButtonOverlay) obj;
                if (button.drawButton) {
                    button.drawTooltips(this.mc, mouseX, mouseY);
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
            this.fontRenderer.drawString(I18n.getStringParams("container." + Assets.DOMAIN + "-tower.level", this.container.level), 8, 20, 0x404040);
            this.fontRenderer.drawString(I18n.getStringParams("container." + Assets.DOMAIN + "-tower.damage", this.container.damage, this.container.damageLevel), 8, 30, 0x404040);
            this.fontRenderer.drawString(I18n.getStringParams("container." + Assets.DOMAIN + "-tower.speed", this.container.speed / 20.0F, this.container.speedLevel), 8, 40, 0x404040);
            this.fontRenderer.drawString(I18n.getStringParams("container." + Assets.DOMAIN + "-tower.range", this.container.range, this.container.rangeLevel), 8, 50, 0x404040);
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
