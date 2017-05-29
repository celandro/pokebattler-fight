package com.pokebattler.fight.ranking.filter;

import java.util.Collection;

import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.Ranking.FilterType;

public interface RankingsFilter {

    FilterType getType();

    RankingsFilter forValue(String filterValue);

    default int getNumWorstSubDefenderToKeep() {
        return Integer.MAX_VALUE;
    }
    default int getNumWorstDefenderToKeep() {
        return Integer.MAX_VALUE;
    }
    default int getNumBestAttackerToKeep() {
        return RankingsFilter.TRIM_TO;
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
    public static final int TRIM_TO = 24;
	String getValue();

	RankingsFilter getOptimizer();

}
