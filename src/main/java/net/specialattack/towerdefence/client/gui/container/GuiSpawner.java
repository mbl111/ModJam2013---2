package net.specialattack.towerdefence.client.gui.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.specialattack.towerdefence.Assets;
import net.specialattack.towerdefence.inventory.ContainerSpawner;
import net.specialattack.towerdefence.tileentity.TileEntitySpawner;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiSpawner extends GuiContainer {

    public TileEntitySpawner tile;
    private ContainerSpawner container;

    private GuiButton activate;
    private GuiButton startnow;
    private GuiButton revoke;

    public GuiSpawner(TileEntitySpawner tile) {
        super(new ContainerSpawner(tile));
        this.container = (ContainerSpawner) this.inventorySlots;
        this.tile = tile;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.clear();

        this.buttonList.add(this.activate = new GuiButton(0, this.guiLeft + 10, this.guiTop + 20, this.xSize - 20, 20, ""));
        this.buttonList.add(this.startnow = new GuiButton(1, this.guiLeft + 10, this.guiTop + 45, this.xSize - 20, 20, I18n.getString("container.towerdefence-spawner.startnow")));
        this.buttonList.add(this.revoke = new GuiButton(2, this.guiLeft + 10, this.guiTop + 70, this.xSize - 20, 20, I18n.getString("container.towerdefence-spawner.revoke")));

        this.setupButtons();
    }

    private void setupButtons() {
        if (!this.container.canWork) {
            this.activate.enabled = false;
            this.activate.displayString = I18n.getString("container.towerdefence-spawner.nowork");

            this.startnow.enabled = false;
            this.revoke.enabled = false;
        } else {
            this.revoke.enabled = this.container.isOp;
            if (!this.container.active) {
                if (this.container.canIJoin) {
                    this.activate.enabled = true;
                    this.activate.displayString = I18n.getString("container.towerdefence-spawner.bind");

                    this.startnow.enabled = false;
                } else {
                    this.activate.enabled = true;
                    this.activate.displayString = I18n.getString("container.towerdefence-spawner.alreadybound");

                    this.startnow.enabled = false;
                }
            } else {
                if (this.container.isMyName) {
                    this.activate.enabled = true;
                    this.activate.displayString = I18n.getString("container.towerdefence-spawner.unbind");

                    this.startnow.enabled = !this.container.isMultiplayer;
                } else {
                    this.activate.enabled = false;
                    this.activate.displayString = I18n.getString("container.towerdefence-spawner.nobind");

                    this.startnow.enabled = false;
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String title = I18n.getString("container.towerdefence-spawner");
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

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (this.container.updated) {
            this.setupButtons();
            this.container.updated = false;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.drawButton && button.enabled) {
            this.mc.playerController.sendEnchantPacket(this.container.windowId, button.id);
        }
    }

}
