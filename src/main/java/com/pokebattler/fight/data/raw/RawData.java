package com.pokebattler.fight.data.raw;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pokebattler.fight.data.proto.PokemonFamilyIdOuterClass.PokemonFamilyId;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.proto.PokemonTypeOuterClass.PokemonType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RawData {
    @JsonProperty("itemTemplates")
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
        @JsonProperty("pokemonSettings")
        public Pokemon pokemon;
        @JsonProperty("moveSettings")
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
        private PokemonMove movementId;
        private int animationId;
        private PokemonType pokemonType;
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


        public int getAnimationId() {
            return animationId;
        }

        public void setAnimationId(int animationId) {
            this.animationId = animationId;
        }


        public PokemonMove getMovementId() {
            return movementId;
        }


        public void setMovementId(PokemonMove movementId) {
            this.movementId = movementId;
        }


        public PokemonType getPokemonType() {
            return pokemonType;
        }


        public void setPokemonType(PokemonType pokemonType) {
            this.pokemonType = pokemonType;
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
        public PokemonId pokemonId;
        public PokemonType type;
        public PokemonType type2;
        public Stats stats;
        public List<PokemonMove> quickMoves;
        public List<PokemonMove> cinematicMoves;
        public float pokedexHeightM;
        public float pokedexWeightKg;
        public PokemonId parentId;
        public float heightStdDev;
        public float weightStdDev;
        public PokemonFamilyId familyId;
        public int candyToEvolve;

        public PokemonId getPokemonId() {
            return pokemonId;
        }

        public void setPokemonId(PokemonId pokemonId) {
            this.pokemonId = pokemonId;
        }

        public PokemonType getType() {
            return type;
        }

        public void setType(PokemonType type) {
            this.type = type;
        }


        public Stats getStats() {
            return stats;
        }

        public void setStats(Stats stats) {
            this.stats = stats;
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


        public int getCandyToEvolve() {
            return candyToEvolve;
        }

        public void setCandyToEvolve(int candyToEvolve) {
            this.candyToEvolve = candyToEvolve;
        }

        public List<PokemonMove> getQuickMoves() {
            return quickMoves;
        }

        public void setQuickMoves(List<PokemonMove> quickMoves) {
            this.quickMoves = quickMoves;
        }

        public List<PokemonMove> getCinematicMoves() {
            return cinematicMoves;
        }

        public void setCinematicMoves(List<PokemonMove> cinematicMoves) {
            this.cinematicMoves = cinematicMoves;
        }

        public PokemonType getType2() {
            return type2;
        }

        public void setType2(PokemonType type2) {
            this.type2 = type2;
        }

        public PokemonId getParentId() {
            return parentId;
        }

        public void setParentId(PokemonId parentId) {
            this.parentId = parentId;
        }

        public PokemonFamilyId getFamilyId() {
            return familyId;
        }

        public void setFamilyId(PokemonFamilyId familyId) {
            this.familyId = familyId;
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
