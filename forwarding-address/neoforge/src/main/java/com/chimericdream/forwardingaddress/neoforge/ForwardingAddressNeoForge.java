package com.chimericdream.forwardingaddress.neoforge;

import net.neoforged.fml.common.Mod;

import com.chimericdream.forwardingaddress.ForwardingAddressMod;
import com.chimericdream.forwardingaddress.ModInfo;

@Mod(ModInfo.MOD_ID)
public final class ForwardingAddressNeoForge {
    public ForwardingAddressNeoForge() {
        ForwardingAddressMod.init();
    }
}
