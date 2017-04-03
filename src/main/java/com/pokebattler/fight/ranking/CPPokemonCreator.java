package com.pokebattler.fight.ranking;

import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;

public class CPPokemonCreator implements PokemonCreator {
	private final int cp;
	private final PokemonDataCreator creator;
	private final boolean capLevel;
	public CPPokemonCreator(PokemonDataCreator creator, int cp) {
		this(creator, cp, true);
	}
	public CPPokemonCreator(PokemonDataCreator creator, int cp, boolean capLevel) {
		this.cp = cp;
		this.creator = creator;
		this.capLevel = capLevel;
	}

	@Override
	public PokemonData createPokemon(PokemonId pokemon, PokemonMove quickMove, PokemonMove cinematicMove) {
		PokemonData data = creator.createPokemon(pokemon, cp, quickMove, cinematicMove);
		if (capLevel) {
			if (data.getCp() > cp) {
				// can not be found
				data = null;
			} else 
			// turn 30-40 into 30. Noone powers up a prestiger
			if (Double.parseDouble(data.getLevel()) > 30.0) {
				data = creator.createMaxStatPokemon(pokemon, "30", quickMove, cinematicMove);
			}
		}
		return data;
		
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
