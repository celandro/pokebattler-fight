package com.pokebattler.fight.strategies;

import static com.pokebattler.fight.data.MoveRepository.DODGE_MOVE;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.calculator.dodge.DodgeStrategy;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;

public class QuickAttackDodgeSpecials implements AttackStrategy {
    private final PokemonData pokemon;
    private final int extraDelay;
    private final Move move1;
    private final DodgeStrategy dodgeStrategy;
    private boolean willDodgeNext = false;

    @Override
    public AttackStrategyType getType() {
        return AttackStrategyType.DODGE_SPECIALS3;
    }

    public QuickAttackDodgeSpecials(PokemonData pokemon, Move move1, int extraDelay, DodgeStrategy dodgeStrategy) {
        this.pokemon = pokemon;
        this.extraDelay = extraDelay;
        this.move1 = move1;
        this.dodgeStrategy = dodgeStrategy;
    }

    @Override
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        // dodge special if we can
        if (defenderState.getNextMove() != null && !defenderState.getNextMove().getMoveId().name().endsWith("FAST")
                && defenderState.getTimeToNextDamage() > 0 && !defenderState.isDodged()) {
        	// do we dodge?
        	willDodgeNext = willDodgeNext || dodgeStrategy.tryToDodge();
        	
        	if (willDodgeNext) {
	        	
	            if (defenderState.getTimeToNextDamage() < Formulas.DODGE_WINDOW  + extraDelay) {
	                return new PokemonAttack(DODGE_MOVE.getMoveId(), extraDelay);
	            } else if (defenderState.getTimeToNextDamage() > move1.getDurationMs() + extraDelay) {
	                // we can sneak in a normal attack
	                return new PokemonAttack(pokemon.getMove1(), extraDelay);
	            } else {
	                // dodge perfect
	                return new PokemonAttack(DODGE_MOVE.getMoveId(),
	                        Math.max(0, defenderState.getTimeToNextDamage() - Formulas.DODGE_WINDOW ));
	            }
        	}
        	// else fall through
        }
        return new PokemonAttack(pokemon.getMove1(), extraDelay);

    }

    public int getDelay() {
        return getDelay();
    }

    @Component
    public static class QuickAttackDodgeSpecialsBuilder implements AttackStrategy.AttackStrategyBuilder<QuickAttackDodgeSpecials> {
        @Resource
        private MoveRepository move;

        @Override
        public QuickAttackDodgeSpecials build(PokemonData pokemon, DodgeStrategy dodgeStrategy) {
            return new QuickAttackDodgeSpecials(pokemon, move.getById(pokemon.getMove1()), 
                    0, dodgeStrategy);
        }
    }

}
