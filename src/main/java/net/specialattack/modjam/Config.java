
package net.specialattack.modjam;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public final class Config {

    public static int blockTowerId = 1337;
    public static int blockGameLogicId = 1338;
    public static int blockClayAvoidingId = 1339;
    public static int itemGameLogicId = 7895;

    public static void loadConfig(Configuration config) {
        Property property = config.get(Configuration.CATEGORY_BLOCK, "blockTowerId", Config.blockTowerId);
        Config.blockTowerId = property.getInt();

        property = config.get(Configuration.CATEGORY_BLOCK, "blockSpawnerId", Config.blockGameLogicId);
        Config.blockGameLogicId = property.getInt();

        property = config.get(Configuration.CATEGORY_BLOCK, "blockClayAvoidingId", Config.blockClayAvoidingId);
        Config.blockClayAvoidingId = property.getInt();

        property = config.get(Configuration.CATEGORY_ITEM, "itemGameLogicId", Config.itemGameLogicId);
        Config.itemGameLogicId = property.getInt();
    }

}
