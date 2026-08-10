package com.chimericdream.lib;

import com.chimericdream.lib.commands.ChimericCommands;
import com.chimericdream.lib.commands.blockstate.BlockPropertiesArgument;
import com.chimericdream.lib.commands.blockstate.BlockStateCommand;

public final class ChimericLib {
    public static final String MOD_ID = "chimericlib";

    public static void init() {
        BlockPropertiesArgument.register();
        ChimericCommands.register(new BlockStateCommand());
    }
}
