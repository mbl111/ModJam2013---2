
package net.specialattack.towerdefence.client.render.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTowerBase extends ModelBase {
    //This is the base model of a tower, before plates and other things are added

    public ModelRenderer towerBase;

    public ModelTowerBase() {
        this.createModel();
    }

    private void createModel() {
        this.towerBase = new ModelRenderer(this, 0, 0).setTextureSize(0, 0);

        this.towerBase.setTextureOffset(0, 0);

        //Base block
        //this.towerBase.addBox(2, 4, 2, 12, 12, 12);

        this.towerBase.addBox(2, 4, 2, 4, 11, 12);
        this.towerBase.addBox(10, 4, 2, 4, 11, 12);
        this.towerBase.addBox(6, 4, 2, 4, 11, 4);
        this.towerBase.addBox(6, 4, 10, 4, 11, 4);

        //Legs
        this.towerBase.addBox(.5f, 0, .5f, 4, 8, 4);
        this.towerBase.addBox(11.5f, 0, .5f, 4, 8, 4);
        this.towerBase.addBox(11.5f, 0, 11.5f, 4, 8, 4);
        this.towerBase.addBox(.5f, 0, 11.5f, 4, 8, 4);

        //Decoration
        this.towerBase.addBox(1, 8, 1, 1, 8, 1);
        this.towerBase.addBox(1, 8, 14, 1, 8, 1);
        this.towerBase.addBox(14, 8, 14, 1, 8, 1);
        this.towerBase.addBox(14, 8, 1, 1, 8, 1);

        //'pole'
        this.towerBase.addBox(6, 13, 6, 4, 1, 4);

        //Decoration
        this.towerBase.addBox(2, 15, 1, 12, 1, 1);
        this.towerBase.addBox(2, 15, 14, 12, 1, 1);

        this.towerBase.addBox(1, 15, 2, 1, 1, 12);
        this.towerBase.addBox(14, 15, 2, 1, 1, 12);
    }

    public void renderAll() {
        this.towerBase.render(0.0625f);
    }

}
