package com.pokebattler.fight.strategies;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;

public class CinematicAttackWhenPossible implements AttackStrategy {
    private PokemonData pokemon;
    private int extraDelay;
    private Move move1;
    private Move move2;
    public static final int CAST_TIME = 500;

    @Override
    public AttackStrategyType getType() {
        return AttackStrategyType.CINEMATIC_ATTACK_WHEN_POSSIBLE;
    }

    public CinematicAttackWhenPossible(PokemonData pokemon, Move move1, Move move2, int extraDelay) {
        this.pokemon = pokemon;
        this.extraDelay = extraDelay;
        this.move1 = move1;
        this.move2 = move2;
    }

    @Override
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        if (attackerState.getCurrentEnergy() >= -1 * move2.getEnergyDelta()) {
            return new PokemonAttack(pokemon.getMove2(), extraDelay + CAST_TIME);
        } else {
            return new PokemonAttack(pokemon.getMove1(), extraDelay);
        }

    }

    public int getDelay() {
        return getDelay();
    }

    @Component
    public static class CinematicAttackWhenPossibleBuilder
            implements AttackStrategy.AttackStrategyBuilder<CinematicAttackWhenPossible> {
        @Resource
        private MoveRepository move;

        @Override
        public CinematicAttackWhenPossible build(PokemonData pokemon) {
            return new CinematicAttackWhenPossible(pokemon, move.getById(pokemon.getMove1()),
                    move.getById(pokemon.getMove2()), 0);
        }
    }

}
