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

public class DodgeAll2 implements AttackStrategy {
    private final PokemonData pokemon;
    private final int extraDelay;
    private final Move move1;
    private final Move move2;
    private boolean dodgedSpecial = false;
    public static final int CAST_TIME = 500;

    @Override
    public AttackStrategyType getType() {
        return AttackStrategyType.DODGE_ALL2;
    }

    public DodgeAll2(PokemonData pokemon, Move move1, Move move2, int extraDelay) {
        this.pokemon = pokemon;
        this.extraDelay = extraDelay;
        this.move1 = move1;
        this.move2 = move2;
    }

    @Override
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        // dodge special if we can
        if (defenderState.getNextMove() != null && defenderState.getTimeToNextDamage() > 0
                && !defenderState.isDodged()) {
            if (defenderState.getTimeToNextDamage() < Formulas.DODGE_WINDOW + extraDelay) {
                dodgedSpecial = defenderState.isNextMoveSpecial();
                return new PokemonAttack(DODGE_MOVE.getMoveId(), extraDelay);
            } else if (defenderState.getTimeToNextDamage() > move1.getDurationMs() + extraDelay) {
                dodgedSpecial = false;
                // we can sneak in a normal attack
                return new PokemonAttack(pokemon.getMove1(), extraDelay);
            } else {
                dodgedSpecial = defenderState.isNextMoveSpecial();
                // dodge perfect
                return new PokemonAttack(DODGE_MOVE.getMoveId(),
                        Math.max(0, defenderState.getTimeToNextDamage() - Formulas.DODGE_WINDOW));
            }
        }
        if (attackerState.getCurrentEnergy() >= -1 * move2.getEnergyDelta() && dodgedSpecial) {
            // use special attack after dodge
            dodgedSpecial = false;
            return new PokemonAttack(pokemon.getMove2(), extraDelay + CAST_TIME);
        } else {
            dodgedSpecial = false;
            return new PokemonAttack(pokemon.getMove1(), extraDelay);
        }

    }

    public int getDelay() {
        return getDelay();
    }

    @Component
    public static class DodgeAll2Builder implements AttackStrategy.AttackStrategyBuilder<DodgeAll2> {
        @Resource
        private MoveRepository move;

        @Override
        public DodgeAll2 build(PokemonData pokemon) {
            return new DodgeAll2(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()),
                    0);
        }
    }

}
