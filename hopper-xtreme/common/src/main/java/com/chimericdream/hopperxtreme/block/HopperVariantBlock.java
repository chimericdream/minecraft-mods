package com.chimericdream.hopperxtreme.block;

/**
 * Implemented by every Hopper X-Treme hopper/hupper block so shared code can inspect a block's
 * registry key and whether it is filter-capable without caring about the concrete block class.
 */
public interface HopperVariantBlock {
    String getBaseKey();

    boolean isWithFilter();
}
