package com.pokebattler.fight.strategies;

import static com.pokebattler.fight.data.MoveRepository.DODGE_MOVE;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;

public class QuickAttackDodgeAll implements AttackStrategy {
    final PokemonData pokemon;
    final int extraDelay;
    final Move move1;
    public static final int CAST_TIME = 500;
    public static final int DODGE_WINDOW = 700;

    @Override
    public AttackStrategyType getType() {
        return AttackStrategyType.DODGE_ALL3;
    }

    public QuickAttackDodgeAll(PokemonData pokemon, Move move1, int extraDelay) {
        this.pokemon = pokemon;
        this.extraDelay = extraDelay;
        this.move1 = move1;
    }

    @Override
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        // dodge special if we can
        if (defenderState.getNextMove() != null && defenderState.getTimeToNextDamage() > 0
                && !defenderState.isDodged()) {
            if (defenderState.getTimeToNextDamage() < DODGE_WINDOW + extraDelay) {
                return new PokemonAttack(DODGE_MOVE.getMoveId(), extraDelay);
            } else if (defenderState.getTimeToNextDamage() > move1.getDurationMs() + extraDelay) {
                // we can sneak in a normal attack
                return new PokemonAttack(pokemon.getMove1(), extraDelay);
            } else {
                // dodge perfect
                return new PokemonAttack(DODGE_MOVE.getMoveId(),
                        Math.max(0, defenderState.getTimeToNextDamage() - DODGE_WINDOW));
            }
        }
        return new PokemonAttack(pokemon.getMove1(), extraDelay);

    }

    public int getDelay() {
        return getDelay();
    }

    @Component
    public static class QuickAttackDodgeAllBuilder implements AttackStrategy.AttackStrategyBuilder<QuickAttackDodgeAll> {
        @Resource
        private MoveRepository move;

        @Override
        public QuickAttackDodgeAll build(PokemonData pokemon) {
            return new QuickAttackDodgeAll(pokemon, move.getById(pokemon.getMove1()), 
                    0);
        }
    }

}
