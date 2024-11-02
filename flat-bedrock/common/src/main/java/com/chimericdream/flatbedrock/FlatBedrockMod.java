package com.chimericdream.flatbedrock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FlatBedrockMod {
    public static final String MOD_ID = "flatbedrock";
    public static final Logger LOGGER = LoggerFactory.getLogger("flatbedrock");

    public static void init() {
        LOGGER.info("Let's flatten some bedrock!");
    }
}
