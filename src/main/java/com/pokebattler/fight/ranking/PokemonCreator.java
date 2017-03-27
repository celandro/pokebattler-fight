package com.pokebattler.fight.ranking;

import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;

public interface PokemonCreator {
	public PokemonData createPokemon(PokemonId pokemon, PokemonMove quickMove, PokemonMove cinematicMove);
	
}
