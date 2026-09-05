package com.chimericdream.stackitup.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.SerializationUtils;

public final class StackItUpClient {
    // Network receiver registration lives in StackItUpMod.init() (common init) - see the comment
    // there for why it can't also be registered from here.
    public static void handleConfigPayload(byte[] configPayload) {
        ArrayList<LinkedHashMap<String, Integer>> configList = SerializationUtils.deserialize(configPayload);
        ConfigSync.syncConfig(configList);
    }
}
