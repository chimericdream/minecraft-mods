package com.chimericdream.stackitup.util;

public interface IItemMaxCount {
    void setMaxCount(int i);

    void revert();

    int getVanillaMaxCount();

    void setVanillaMaxCount(int vanillaMaxCount);
}
