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

public class DodgeSpecials2 implements AttackStrategy {
    private final PokemonData pokemon;
    private final int extraDelay;
    private final Move move1;
    private final Move move2;
    private boolean dodgedSpecial;
    public static final int CAST_TIME = 500;

    @Override
    public AttackStrategyType getType() {
        return AttackStrategyType.DODGE_SPECIALS2;
    }

    public DodgeSpecials2(PokemonData pokemon, Move move1, Move move2, int extraDelay) {
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
                dodgedSpecial = true;
                return new PokemonAttack(DODGE_MOVE.getMoveId(), extraDelay);
            } else if (defenderState.getTimeToNextDamage() > move1.getDurationMs() + extraDelay) {
                // we can sneak in a normal attack
                return new PokemonAttack(pokemon.getMove1(), extraDelay);
            } else {
                // dodge perfect
                dodgedSpecial = true;
                return new PokemonAttack(DODGE_MOVE.getMoveId(),
                        Math.max(0, defenderState.getTimeToNextDamage() - Formulas.DODGE_WINDOW));
            }
        }
        if (attackerState.getCurrentEnergy() >= -1 * move2.getEnergyDelta() && dodgedSpecial) {
            dodgedSpecial = false;
            return new PokemonAttack(pokemon.getMove2(), extraDelay + CAST_TIME);
        } else {
            return new PokemonAttack(pokemon.getMove1(), extraDelay);
        }

    }

    public int getDelay() {
        return getDelay();
    }

    @Component
    public static class DodgeSpecials2Builder implements AttackStrategy.AttackStrategyBuilder<DodgeSpecials2> {
        @Resource
        private MoveRepository move;

        @Override
        public DodgeSpecials2 build(PokemonData pokemon) {
            return new DodgeSpecials2(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()),
                    0);
        }
    }

}
