
package net.specialattack.modjam.client.gui.container;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.specialattack.modjam.Assets;
import net.specialattack.modjam.inventory.ContainerSpawner;
import net.specialattack.modjam.tileentity.TileEntitySpawner;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSpawner extends GuiContainer {

    public TileEntitySpawner tile;
    private ContainerSpawner container;

    public boolean prevWaveActive;
    public boolean prevActive;
    public boolean prevCanWork;

    private GuiButton activate;

    public GuiSpawner(TileEntitySpawner tile) {
        super(new ContainerSpawner(tile));
        this.container = (ContainerSpawner) this.inventorySlots;
        this.tile = tile;
    }

    private void setupButtons() {
        if (!this.container.canWork) {
            this.activate.enabled = false;
            this.activate.displayString = I18n.getString("container." + Assets.DOMAIN + "-spawner.nowork");
        }
        else {
            if (!this.tile.active) {
                this.activate.enabled = true;
                this.activate.displayString = I18n.getString("container." + Assets.DOMAIN + "-spawner.bind");
            }
            else {
                if (this.container.isMyName) {
                    this.activate.enabled = true;
                    this.activate.displayString = I18n.getString("container." + Assets.DOMAIN + "-spawner.unbind");
                }
                else {
                    this.activate.enabled = false;
                    this.activate.displayString = I18n.getString("container." + Assets.DOMAIN + "-spawner.nobind");
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.clear();

        this.buttonList.add(this.activate = new GuiButton(0, this.guiLeft + 20, this.guiTop + 20, this.xSize - 40, 20, ""));

        this.setupButtons();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.drawButton && button.enabled) {
            this.mc.playerController.sendEnchantPacket(this.container.windowId, button.id);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (this.prevActive != this.tile.active || this.prevCanWork != this.container.canWork) {
            this.setupButtons();
        }

        this.prevActive = this.tile.active;
        this.prevWaveActive = this.tile.waveActive;
        this.prevCanWork = this.container.canWork;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String title = I18n.getString("container." + Assets.DOMAIN + "-spawner");
        this.fontRenderer.drawString(title, this.xSize / 2 - this.fontRenderer.getStringWidth(title) / 2, 6, 0x404040);
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
