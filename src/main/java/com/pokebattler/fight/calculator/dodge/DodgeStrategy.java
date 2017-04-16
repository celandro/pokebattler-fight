package com.pokebattler.fight.calculator.dodge;

import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.strategies.AttackStrategy;

public interface DodgeStrategy {
	
	boolean tryToDodge();
	DodgeStrategyType getType();

    public static interface DodgeStrategyBuilder<S extends DodgeStrategy> {
        public default DodgeStrategyType getType() {
            return build().getType();
        }

        S build();
    }

	
}
