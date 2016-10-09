package com.pokebattler.fight.data.raw;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class RawData {
    public List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        public String templateId;
        public Pokemon pokemon;
        public Move move;

        public Move getMove() {
            return move;
        }

        public void setMove(Move move) {
            this.move = move;
        }

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }

        public Pokemon getPokemon() {
            return pokemon;
        }

        public void setPokemon(Pokemon pokemon) {
            this.pokemon = pokemon;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Move {
        private String uniqueId;
        private String moveId;
        private int animationId;
        private String type;
        private int power;
        private float criticalChance;
        private float accuracyChance;
        private float staminaLossScalar;
        private int trainerLevelMin;
        private int trainerLevelMax;
        private String vfxName;
        private int durationMs;
        private int damageWindowStartMs;
        private int damageWindowEndMs;
        private int energyDelta;

        public float getCriticalChance() {
            return criticalChance;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        public String getMoveId() {
            return moveId;
        }

        public void setMoveId(String moveId) {
            this.moveId = moveId;
        }

        public int getAnimationId() {
            return animationId;
        }

        public void setAnimationId(int animationId) {
            this.animationId = animationId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getPower() {
            return power;
        }

        public void setPower(int power) {
            this.power = power;
        }

        public float getAccuracyChance() {
            return accuracyChance;
        }

        public void setCriticalChance(float criticalChance) {
            this.criticalChance = criticalChance;
        }

        public void setAccuracyChance(float accuracyChance) {
            this.accuracyChance = accuracyChance;
        }

        public float getStaminaLossScalar() {
            return staminaLossScalar;
        }

        public void setStaminaLossScalar(float staminaLossScalar) {
            this.staminaLossScalar = staminaLossScalar;
        }

        public int getTrainerLevelMin() {
            return trainerLevelMin;
        }

        public void setTrainerLevelMin(int trainerLevelMin) {
            this.trainerLevelMin = trainerLevelMin;
        }

        public int getTrainerLevelMax() {
            return trainerLevelMax;
        }

        public void setTrainerLevelMax(int trainerLevelMax) {
            this.trainerLevelMax = trainerLevelMax;
        }

        public String getVfxName() {
            return vfxName;
        }

        public void setVfxName(String vfxName) {
            this.vfxName = vfxName;
        }

        public int getDurationMs() {
            return durationMs;
        }

        public void setDurationMs(int durationMs) {
            this.durationMs = durationMs;
        }

        public int getDamageWindowStartMs() {
            return damageWindowStartMs;
        }

        public void setDamageWindowStartMs(int damageWindowStartMs) {
            this.damageWindowStartMs = damageWindowStartMs;
        }

        public int getDamageWindowEndMs() {
            return damageWindowEndMs;
        }

        public void setDamageWindowEndMs(int damageWindowEndsMs) {
            this.damageWindowEndMs = damageWindowEndsMs;
        }

        public int getEnergyDelta() {
            return energyDelta;
        }

        public void setEnergyDelta(int energyDelta) {
            this.energyDelta = energyDelta;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pokemon {
        public String uniqueId;
        public String type1;
        public String type2;
        public Stats stats;
        public String quickMoves;
        public String cinematicMoves;
        public float pokedexHeightM;
        public float pokedexWeightKg;
        public String parentId;
        public float heightStdDev;
        public float weightStdDev;
        public String familyId;
        public int candyToEvolve;

        public String getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        public String getType1() {
            return type1;
        }

        public void setType1(String type1) {
            this.type1 = type1;
        }

        public String getType2() {
            return type2;
        }

        public void setType2(String type2) {
            this.type2 = type2;
        }

        public Stats getStats() {
            return stats;
        }

        public void setStats(Stats stats) {
            this.stats = stats;
        }

        public String getQuickMoves() {
            return quickMoves;
        }

        public void setQuickMoves(String quickMoves) {
            this.quickMoves = quickMoves;
        }

        public String getCinematicMoves() {
            return cinematicMoves;
        }

        public void setCinematicMoves(String cinematicMoves) {
            this.cinematicMoves = cinematicMoves;
        }

        public float getPokedexHeightM() {
            return pokedexHeightM;
        }

        public void setPokedexHeightM(float pokedexHeightM) {
            this.pokedexHeightM = pokedexHeightM;
        }

        public float getPokedexWeightKg() {
            return pokedexWeightKg;
        }

        public void setPokedexWeightKg(float pokedexWeightKg) {
            this.pokedexWeightKg = pokedexWeightKg;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public float getHeightStdDev() {
            return heightStdDev;
        }

        public void setHeightStdDev(float heightStdDev) {
            this.heightStdDev = heightStdDev;
        }

        public float getWeightStdDev() {
            return weightStdDev;
        }

        public void setWeightStdDev(float weightStdDev) {
            this.weightStdDev = weightStdDev;
        }

        public String getFamilyId() {
            return familyId;
        }

        public void setFamilyId(String familyId) {
            this.familyId = familyId;
        }

        public int getCandyToEvolve() {
            return candyToEvolve;
        }

        public void setCandyToEvolve(int candyToEvolve) {
            this.candyToEvolve = candyToEvolve;
        }

    }

    public static class Stats {
        public int baseStamina;
        public int baseAttack;
        public int baseDefense;

        public int getBaseStamina() {
            return baseStamina;
        }

        public void setBaseStamina(int baseStamina) {
            this.baseStamina = baseStamina;
        }

        public int getBaseAttack() {
            return baseAttack;
        }

        public void setBaseAttack(int baseAttack) {
            this.baseAttack = baseAttack;
        }

        public int getBaseDefense() {
            return baseDefense;
        }

        public void setBaseDefense(int baseDefense) {
            this.baseDefense = baseDefense;
        }

    }

}
