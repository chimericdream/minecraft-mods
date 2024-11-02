package com.chimericdream.athenaeum.config;

import com.chimericdream.athenaeum.AthenaeumMod;

public class AthenaeumConfig {
    public double firstEditionChance = Defaults.FIRST_EDITION_CHANCE;
    public double secondEditionChance = Defaults.SECOND_EDITION_CHANCE;
    public double thirdEditionChance = Defaults.THIRD_EDITION_CHANCE;

    public void validatePostLoad() {
        if (this.firstEditionChance < 0) {
            AthenaeumMod.LOGGER.info("[config] Invalid value found for 'firstEditionChance'! Resetting to default.");
            this.firstEditionChance = Defaults.FIRST_EDITION_CHANCE;
        }

        if (this.secondEditionChance < 0) {
            AthenaeumMod.LOGGER.info("[config] Invalid value found for 'secondEditionChance'! Resetting to default.");
            this.secondEditionChance = Defaults.SECOND_EDITION_CHANCE;
        }

        if (this.thirdEditionChance < 0) {
            AthenaeumMod.LOGGER.info("[config] Invalid value found for 'thirdEditionChance'! Resetting to default.");
            this.thirdEditionChance = Defaults.THIRD_EDITION_CHANCE;
        }

        if (this.firstEditionChance + this.secondEditionChance + this.thirdEditionChance > 1) {
            AthenaeumMod.LOGGER.info("[config] The total chance must not exceed 1.0! Resetting to default.");

            this.firstEditionChance = Defaults.FIRST_EDITION_CHANCE;
            this.secondEditionChance = Defaults.SECOND_EDITION_CHANCE;
            this.thirdEditionChance = Defaults.THIRD_EDITION_CHANCE;
        }
    }

    public static class Defaults {
        public static double FIRST_EDITION_CHANCE = 0.05;
        public static double SECOND_EDITION_CHANCE = 0.2;
        public static double THIRD_EDITION_CHANCE = 0.5;
    }
}
