package com.pokebattler.fight.strategies;

import static com.pokebattler.fight.data.MoveRepository.DODGE_MOVE;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;

public class DodgeSpecials implements AttackStrategy {
    PokemonData pokemon;
    int extraDelay;
    Move move1;
    Move move2;
    public static final int CAST_TIME = 500;

    @Override
    public AttackStrategyType getType() {
        return AttackStrategyType.DODGE_SPECIALS;
    }

    public DodgeSpecials(PokemonData pokemon, Move move1, Move move2, int extraDelay) {
        this.pokemon = pokemon;
        this.extraDelay = extraDelay;
        this.move1 = move1;
        this.move2 = move2;
    }

    @Override
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        // dodge special if we can
        if (defenderState.getNextMove() != null && defenderState.isNextMoveSpecial()
                && defenderState.getTimeToNextDamage() > 0 && !defenderState.isDodged()) {
            if (defenderState.getTimeToNextDamage() <= Formulas.DODGE_WINDOW + extraDelay) {
                return new PokemonAttack(DODGE_MOVE.getMoveId(), extraDelay);
            } else if (defenderState.getTimeToNextDamage() > move2.getDurationMs() + extraDelay + CAST_TIME
                    && attackerState.getCurrentEnergy() >= -1 * move2.getEnergyDelta()) {
                // we can sneak in a special attack
                return new PokemonAttack(pokemon.getMove2(), extraDelay);
            } else if (defenderState.getTimeToNextDamage() > move1.getDurationMs() + extraDelay) {
                // we can sneak in a normal attack
                return new PokemonAttack(pokemon.getMove1(), extraDelay);
            } else {
                // dodge perfect
                return new PokemonAttack(DODGE_MOVE.getMoveId(),
                        Math.max(0, defenderState.getTimeToNextDamage() - Formulas.DODGE_WINDOW));
            }
        }
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
    public static class DodgeSpecialsBuilder implements AttackStrategy.AttackStrategyBuilder<DodgeSpecials> {
        @Resource
        private MoveRepository move;

        @Override
        public DodgeSpecials build(PokemonData pokemon) {
            return new DodgeSpecials(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()),
                    0);
        }
    }

}
