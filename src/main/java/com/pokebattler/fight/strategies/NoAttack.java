package com.pokebattler.fight.strategies;

import static com.pokebattler.fight.data.MoveRepository.DODGE_MOVE;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.strategies.AttackStrategy.PokemonAttack;

public class NoAttack implements AttackStrategy {
    PokemonData pokemon;
    int extraDelay;

    @Override
    public AttackStrategyType getType() {
        return AttackStrategyType.NONE;
    }

    public NoAttack(PokemonData pokemon, int extraDelay) {
        this.pokemon = pokemon;
        this.extraDelay = extraDelay;
    }

    @Override
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        if (defenderState.getNextMove() != null && defenderState.getTimeToNextDamage() > 0
                && !defenderState.isDodged()) {
                // dodge perfect
            return new PokemonAttack(DODGE_MOVE.getMoveId(),
                    Math.max(0, defenderState.getTimeToNextDamage() - Formulas.DODGE_WINDOW));
        }        
        return new PokemonAttack(PokemonMove.DODGE, extraDelay);
    }

    public int getDelay() {
        return getDelay();
    }

    @Component
    public static class NoAttackBuilder implements AttackStrategy.AttackStrategyBuilder<NoAttack> {
        @Override
        public NoAttack build(PokemonData pokemon) {
            return new NoAttack(pokemon, DODGE_COOLDOWN);
        }
    }

}
