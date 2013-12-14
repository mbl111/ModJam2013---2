
package net.specialattack.modjam;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public final class Config {

    public static int blockTowerId = 1337;
    public static int blockSpawnerId = 1338;
    public static int blockClayAvoidingId = 1339;

    public static void loadConfig(Configuration config) {
        Property property = config.get(Configuration.CATEGORY_BLOCK, "blockTowerId", blockTowerId);
        blockTowerId = property.getInt();

        property = config.get(Configuration.CATEGORY_BLOCK, "blockSpawnerId", blockSpawnerId);
        blockSpawnerId = property.getInt();

        property = config.get(Configuration.CATEGORY_BLOCK, "blockClayAvoidingId", blockClayAvoidingId);
        blockClayAvoidingId = property.getInt();
    }

}
