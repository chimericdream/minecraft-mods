package com.chimericdream.flatbedrock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FlatBedrockMod {
    public static final String MOD_ID = "flatbedrock";
    public static final Logger LOGGER = LogManager.getLogger("flatbedrock");

    public static void init() {
        LOGGER.info("Let's flatten some bedrock!");
    }
}
