package com.pokebattler.fight.strategies;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;

public interface AttackStrategy {
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState);

    public AttackStrategyType getType();

    public static int DODGE_COOLDOWN = 500;

    public static class PokemonAttack {
        private final PokemonMove move;
        private final int delay;

        public PokemonAttack(PokemonMove move, int delay) {
            this.move = move;
            this.delay = delay;
        }

        public PokemonMove getMove() {
            return move;
        }

        public int getDelay() {
            return delay;
        }

    }

    public static interface AttackStrategyBuilder<S extends AttackStrategy> {
        public default AttackStrategyType getType() {
            final PokemonData fake = PokemonData.newBuilder().setMove1(PokemonMove.LICK).setMove2(PokemonMove.BODY_SLAM)
                    .setPokemonId(PokemonId.SNORLAX).build();
            return build(fake).getType();
        }

        S build(PokemonData pokemon);
    }
}
