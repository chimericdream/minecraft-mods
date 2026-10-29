package com.chimericdream.forwardingaddress;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ForwardingSettingsTest {
    @Test
    void theDefaultCapIsAboutTwoHundredFiftySix() {
        assertEquals(256, new ForwardingSettings().maxConversionsPerTick);
    }

    @Test
    void aCapBelowOneWouldStallConversionSoItIsRaised() {
        assertEquals(1, ForwardingSettings.clamp(0));
        assertEquals(1, ForwardingSettings.clamp(-5));
    }

    @Test
    void aSillyHugeCapIsLimited() {
        assertEquals(100_000, ForwardingSettings.clamp(Integer.MAX_VALUE));
    }

    @Test
    void aReasonableCapIsKept() {
        assertEquals(64, ForwardingSettings.clamp(64));
    }
}
