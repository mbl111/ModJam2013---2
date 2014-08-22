package net.specialattack.towerdefence.client.gui.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.specialattack.towerdefence.Assets;
import net.specialattack.towerdefence.inventory.ContainerMultiplayerController;
import net.specialattack.towerdefence.tileentity.TileEntityMultiplayerController;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiMultiplayerController extends GuiContainer {

    public TileEntityMultiplayerController tile;
    private ContainerMultiplayerController container;

    private GuiButton activate;
    private GuiButton revoke;

    public GuiMultiplayerController(TileEntityMultiplayerController tile) {
        super(new ContainerMultiplayerController(tile));
        this.container = (ContainerMultiplayerController) this.inventorySlots;
        this.tile = tile;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.clear();

        this.buttonList.add(this.activate = new GuiButton(0, this.guiLeft + 10, this.guiTop + 20, this.xSize - 20, 20, ""));
        this.buttonList.add(this.revoke = new GuiButton(1, this.guiLeft + 10, this.guiTop + 45, this.xSize - 20, 20, I18n.getString("container.towerdefence-multiplayerController.revoke")));

        this.setupButtons();
    }

    private void setupButtons() {
        if (this.container.active) {
            this.activate.enabled = true;
            this.activate.displayString = I18n.getString("container.towerdefence-multiplayerController.stop");

            this.revoke.enabled = false;
        } else {
            this.revoke.enabled = this.container.isOp;
            if (this.container.connections <= 0) {
                this.activate.enabled = false;
                this.activate.displayString = I18n.getString("container.towerdefence-multiplayerController.nowork");
            } else if (this.container.activeConnections < 1) {
                this.activate.enabled = false;
                this.activate.displayString = I18n.getStringParams("container.towerdefence-multiplayerController.noplayers", this.container.activeConnections);
            } else {
                this.activate.enabled = true;
                this.activate.displayString = I18n.getStringParams("container.towerdefence-multiplayerController.start", this.container.activeConnections);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String title = I18n.getString("container.towerdefence-multiplayerController");
        this.fontRenderer.drawString(title, this.xSize / 2 - this.fontRenderer.getStringWidth(title) / 2, 6, 0x404040);

        this.fontRenderer.drawString(I18n.getStringParams("container.towerdefence-multiplayerController.spawners", this.container.connections), 8, 74, 0x404040);
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
