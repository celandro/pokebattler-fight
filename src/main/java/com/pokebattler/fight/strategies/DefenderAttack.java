package com.pokebattler.fight.strategies;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;

public class DefenderAttack implements AttackStrategy {
    private int extraDelay;
    private PokemonData pokemon;
    private Move move1;
    private Move move2;
    private int nextSpecialMove = -1;
    public static int SECOND_ATTACK_DELAY = 1000;
    public static int FIRST_ATTACK_TIME = 1600 - Formulas.START_COMBAT_TIME;

    @Override
    public AttackStrategyType getType() {
        return AttackStrategyType.DEFENSE;
    }

    public DefenderAttack(PokemonData pokemon, Move move1, Move move2, int extraDelay) {
        this.pokemon = pokemon;
        this.extraDelay = extraDelay;
        this.move1 = move1;
        this.move2 = move2;
    }

    @Override
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        // Statistically speaking with a 50% chance of winning a coin flip, you
        // average out at at attack coming out 1 attack later
        if (nextSpecialMove == -1 && attackerState.getCurrentEnergy() >= -1 * move2.getEnergyDelta()) {
            nextSpecialMove = attackerState.getNumAttacks() + 1;
        }
        if (nextSpecialMove == attackerState.getNumAttacks()) {
            nextSpecialMove = -1;
            return new PokemonAttack(pokemon.getMove2(), extraDelay);
        }
        switch (attackerState.getNumAttacks()) {
        case 0:
            return new PokemonAttack(pokemon.getMove1(), FIRST_ATTACK_TIME);
        case 1:
            return new PokemonAttack(pokemon.getMove1(), SECOND_ATTACK_DELAY - move1.getDurationMs());
        case 2:
            return new PokemonAttack(pokemon.getMove1(), extraDelay - move1.getDurationMs());
        default:
            return new PokemonAttack(pokemon.getMove1(), extraDelay);
        }

    }

    @Component
    public static class DefenderAttackBuilder implements AttackStrategy.AttackStrategyBuilder<DefenderAttack> {
        @Resource
        private MoveRepository move;
        public static int DEFENDER_DELAY = 2000;

        @Override
        public DefenderAttack build(PokemonData pokemon) {
            return new DefenderAttack(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()),
                    DEFENDER_DELAY);
        }
    }

}
