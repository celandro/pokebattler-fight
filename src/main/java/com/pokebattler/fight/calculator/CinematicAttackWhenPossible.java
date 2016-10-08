package com.pokebattler.fight.calculator;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.*;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.strategies.AttackStrategy;
import com.pokebattler.fight.strategies.AttackStrategy.PokemonAttack;

public class CinematicAttackWhenPossible implements AttackStrategy {
    PokemonData pokemon;
    int extraDelay;
    Move move1;
    Move move2;
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
    public static class CinematicAttackWhenPossibleBuilder implements AttackStrategy.AttackStrategyBuilder<CinematicAttackWhenPossible>{
        @Resource
        private MoveRepository move;
                @Override
        public CinematicAttackWhenPossible build(PokemonData pokemon, int extraDelay) {
            return new CinematicAttackWhenPossible(pokemon,move.getById(pokemon.getMove1()), 
                    move.getById(pokemon.getMove2()),extraDelay);
        }
    }

}
