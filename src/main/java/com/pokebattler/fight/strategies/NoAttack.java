package com.pokebattler.fight.strategies;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.data.proto.FightOuterClass.*;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;

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
        // pick any
        return new PokemonAttack(PokemonMove.DODGE,  extraDelay);
    }
    public int getDelay() {
        return getDelay();
    }
    @Component
    public static class NoAttackBuilder implements AttackStrategy.AttackStrategyBuilder<NoAttack>{
        @Override
        public NoAttack build(PokemonData pokemon, int extraDelay) {
            return new NoAttack(pokemon,DODGE_COOLDOWN);
        }
    }

}
