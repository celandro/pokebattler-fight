package com.pokebattler.fight.ranking.filter;

import java.util.Collection;

import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.Ranking.FilterType;

public interface RankingsFilter {

    FilterType getType();

    RankingsFilter forValue(String filterValue);

    default int getNumWorstToKeep() {
        return Integer.MAX_VALUE;
    }
    
    default Collection<Pokemon> getAttackers(PokemonRepository repository) {
    	return repository.getAllEndGame().getPokemonList();
    }
    default Collection<Pokemon> getDefenders(PokemonRepository repository) {
    	return repository.getAllEndGameDefender().getPokemonList();
    }
    default boolean compressResults() {
    	return true;
    }

}
