package com.pokebattler.fight.strategies;

import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.AttackDamage;
import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.calculator.dodge.DodgeStrategy;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;

public class DefenderRandomAttack implements AttackStrategy {
    private final int extraDelay;
    private final PokemonData pokemon;
    private final Move move1;
    private final Move move2;
    private final Random r;
    public static int SECOND_ATTACK_DELAY = 1000;
    public static int FIRST_ATTACK_TIME = 1600 - Formulas.START_COMBAT_TIME;
    private final int randomDelay;
    private final AttackStrategyType type;
    private final double specialRandom;
	private AttackDamage move1Damage;
	private AttackDamage move2Damage;

    @Override
    public AttackStrategyType getType() {
        return type;
    }

    public DefenderRandomAttack(PokemonData pokemon, Move move1, Move move2, int extraDelay, int randomDelay,
            AttackStrategyType type, double specialRandom, AttackDamage move1Damage, AttackDamage move2Damage, Random r) {
        this.pokemon = pokemon;
        this.extraDelay = extraDelay;
        this.randomDelay = randomDelay;
        this.move1 = move1;
        this.move2 = move2;
        this.type = type;
        this.specialRandom = specialRandom;
        this.move1Damage = move1Damage;
        this.move2Damage = move2Damage;
        this.r = r;
        
    }

    @Override
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
    	if (attackerState.getCurrentEnergy() >= -1 * move2.getEnergyDelta() && r.nextDouble() < specialRandom) {
            return getMove2Attack( extraDelay + r.nextInt(randomDelay));
        } else {
            switch (attackerState.getNumAttacks()) {
            case 0:
                return getMove1Attack(FIRST_ATTACK_TIME);
            case 1:
                return getMove1Attack(SECOND_ATTACK_DELAY - move1.getDurationMs());
            case 2:
                return getMove1Attack(extraDelay + r.nextInt(randomDelay) - move1.getDurationMs());
            default:
                return getMove1Attack(extraDelay + r.nextInt(randomDelay));
            }
        }

    }

    @Component
    public static class DefenderRandomAttackBuilder
            implements AttackStrategy.AttackStrategyBuilder<DefenderRandomAttack> {
        public static int RAND_MS_DELAY = 1000;
        public static double RAND_CHANCE_SPECIAL = 0.5;
        public static int DEFENDER_DELAY = 2000;
        @Resource
        private MoveRepository move;

        @Override
        public DefenderRandomAttack build(PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage, Random r) {
        	// defenders never dodge
            return new DefenderRandomAttack(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()),
                    DEFENDER_DELAY - (RAND_MS_DELAY / 2), RAND_MS_DELAY, AttackStrategyType.DEFENSE_RANDOM,
                    RAND_CHANCE_SPECIAL,  move1Damage,  move2Damage, r);
        }
    }

    @Component
    public static class DefenderLuckyAttackBuilder
            implements AttackStrategy.AttackStrategyBuilder<DefenderRandomAttack> {
        public static int RAND_MS_DELAY = 1;
        public static double RAND_CHANCE_SPECIAL = 1.0;
        public static int RAND_LUCKY_DELAY = 50;
        public static int DEFENDER_DELAY = 2000;
        @Resource
        private MoveRepository move;

        @Override
        public DefenderRandomAttack build(PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage, Random r) {
        	// defenders never dodge
            return new DefenderRandomAttack(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()),
                    DEFENDER_DELAY - RAND_LUCKY_DELAY, RAND_MS_DELAY, AttackStrategyType.DEFENSE_LUCKY,
                    RAND_CHANCE_SPECIAL, move1Damage, move2Damage, r);
        }
    }

	public int getExtraDelay() {
		return extraDelay;
	}

	public PokemonData getPokemon() {
		return pokemon;
	}

	public Move getMove1() {
		return move1;
	}

	public Move getMove2() {
		return move2;
	}

	public Random getR() {
		return r;
	}

	public int getRandomDelay() {
		return randomDelay;
	}

	public double getSpecialRandom() {
		return specialRandom;
	}

	public AttackDamage getMove1Damage() {
		return move1Damage;
	}

	public AttackDamage getMove2Damage() {
		return move2Damage;
	}

}
