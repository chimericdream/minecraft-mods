package com.chimericdream.lib;

import com.chimericdream.lib.commands.ChimericCommands;
import com.chimericdream.lib.commands.blockstate.BlockPropertiesArgument;
import com.chimericdream.lib.commands.blockstate.BlockStateCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ChimericLib {
    public static final String MOD_ID = "chimericlib";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void init() {
        BlockPropertiesArgument.register();
        ChimericCommands.register(new BlockStateCommand());
    }
}
