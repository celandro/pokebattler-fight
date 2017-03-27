package com.pokebattler.fight.ranking;

import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;

public class CPPokemonCreator implements PokemonCreator {
	private final int cp;
	private final PokemonDataCreator creator;
	public CPPokemonCreator(PokemonDataCreator creator, int cp) {
		this.cp = cp;
		this.creator = creator;
	}

	@Override
	public PokemonData createPokemon(PokemonId pokemon, PokemonMove quickMove, PokemonMove cinematicMove) {
		return creator.createPokemon(pokemon, cp, quickMove, cinematicMove);
	}

	@Override
	public int hashCode() {
		return cp;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CPPokemonCreator other = (CPPokemonCreator) obj;
		if (cp != other.cp)
			return false;
		return true;
	}

}
