package com.pokebattler.fight.calculator;

import java.util.Arrays;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.*;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.strategies.AttackStrategy;
import com.pokebattler.fight.strategies.AttackStrategy.PokemonAttack;

public class DefenderRandomAttack implements AttackStrategy {
    int extraDelay;
    PokemonData pokemon;
    Move move1;
    Move move2;
    Random r = new Random();
    public static int SECOND_ATTACK_DELAY = 1000;
    int randomDelay;
    AttackStrategyType type;
    double specialRandom;

    @Override
    public AttackStrategyType getType() {
        return type;
    }

    public DefenderRandomAttack(PokemonData pokemon, Move move1, Move move2, int extraDelay, int randomDelay,
            AttackStrategyType type, double specialRandom) {
        this.pokemon = pokemon;
        this.extraDelay = extraDelay;
        this.randomDelay = randomDelay;
        this.move1 = move1;
        this.move2 = move2;
        this.type = type;
        this.specialRandom = specialRandom;
    }

    @Override
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        if (r.nextDouble() < specialRandom && attackerState.getCurrentEnergy() >= -1 * move2.getEnergyDelta()) {
            return new PokemonAttack(pokemon.getMove2(), extraDelay + r.nextInt(randomDelay));
        } else {
            switch (attackerState.getNumAttacks()) {
            case 0:
                return new PokemonAttack(pokemon.getMove1(), SECOND_ATTACK_DELAY/2);
            case 1:
                return new PokemonAttack(pokemon.getMove1(), SECOND_ATTACK_DELAY - move1.getDurationMs());
            case 2:
                return new PokemonAttack(pokemon.getMove1(), extraDelay + r.nextInt(randomDelay) - SECOND_ATTACK_DELAY);
            default:
                return new PokemonAttack(pokemon.getMove1(), extraDelay + r.nextInt(randomDelay));
            }
        }

    }

    @Component
    public static class DefenderRandomAttackBuilder
            implements AttackStrategy.AttackStrategyBuilder<DefenderRandomAttack> {
        public static int RAND_MS_DELAY = 1000;
        public static double RAND_CHANCE_SPECIAL = 0.5;
        @Resource
        private MoveRepository move;

        @Override
        public DefenderRandomAttack build(PokemonData pokemon, int extraDelay) {
            return new DefenderRandomAttack(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()),
                    extraDelay - ((int) RAND_MS_DELAY / 2), RAND_MS_DELAY, AttackStrategyType.DEFENSE_RANDOM, RAND_CHANCE_SPECIAL);
        }
    }

    @Component
    public static class DefenderLuckyAttackBuilder implements AttackStrategy.AttackStrategyBuilder<DefenderRandomAttack> {
        public static int RAND_MS_DELAY = 1;
        public static double RAND_CHANCE_SPECIAL = 1.0;
        public static int RAND_LUCKY_DELAY = 50;
        @Resource
        private MoveRepository move;

        @Override
        public DefenderRandomAttack build(PokemonData pokemon, int extraDelay) {
            return new DefenderRandomAttack(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()),
                    extraDelay - RAND_LUCKY_DELAY, RAND_MS_DELAY, AttackStrategyType.DEFENSE_LUCKY, RAND_CHANCE_SPECIAL);
        }
    }

}
