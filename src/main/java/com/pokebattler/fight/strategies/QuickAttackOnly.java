package com.pokebattler.fight.strategies;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;

public class QuickAttackOnly implements AttackStrategy {
    PokemonData pokemon;
    int extraDelay;

    @Override
    public AttackStrategyType getType() {
        return AttackStrategyType.QUICK_ATTACK_ONLY;
    }

    public QuickAttackOnly(PokemonData pokemon, int extraDelay) {
        this.pokemon = pokemon;
        this.extraDelay = extraDelay;
    }

    @Override
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        return new PokemonAttack(pokemon.getMove1(), extraDelay);
    }

    public int getDelay() {
        return getDelay();
    }

    @Component
    public static class QuickAttackOnlyBuilder implements AttackStrategy.AttackStrategyBuilder<QuickAttackOnly> {
        @Override
        public QuickAttackOnly build(PokemonData pokemon) {
            return new QuickAttackOnly(pokemon, 0);
        }
    }

}
