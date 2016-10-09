package com.pokebattler.fight.data.raw;

public class Pokemon {
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

}
