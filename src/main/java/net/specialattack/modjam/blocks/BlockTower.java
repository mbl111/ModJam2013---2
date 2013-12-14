
package net.specialattack.modjam.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.specialattack.modjam.pathfinding.IAvoided;

public class BlockTower extends Block implements IAvoided {

    // Empty multi-block until activated
    // Tower part or fake block (meta = 1)
    // Tower or fake block (meta = 1)
    // Base (in-ground) (meta = 0)

    // Metadata map:
    // 0 base
    // 1 fake block

    public BlockTower(int blockId) {
        super(blockId, Material.anvil);
    }

}
