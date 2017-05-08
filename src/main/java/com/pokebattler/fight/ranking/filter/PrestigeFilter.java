package com.pokebattler.fight.ranking.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.Ranking.FilterType;

@Component
public class PrestigeFilter implements RankingsFilter {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final FilterType filterType = FilterType.PRESTIGE;
    private final PokemonId pokemonId; 
    public PrestigeFilter() {
    	this.pokemonId = PokemonId.UNRECOGNIZED;
    }
    public PrestigeFilter(PokemonId pokemonId) {
    	this.pokemonId = pokemonId;
    }

    @Override
    public FilterType getType() {
        return filterType;
    }

    @Override
    public RankingsFilter forValue(String filterValue) {
        try {
            return new PrestigeFilter(PokemonId.valueOf(filterValue));
        } catch (Exception e) {
            log.error("Could not create filter",e);
            return this;
        }
    }

	@Override
	public Collection<Pokemon> getAttackers(PokemonRepository repository) {
		return  Collections.singletonList(repository.getById(pokemonId));
	}
	@Override
	public Collection<Pokemon> getDefenders(PokemonRepository repository) {
		// Ditto is never a good prestiger and I dont want to figure out how to fix him
		return  repository.getAll().getPokemonList().stream().filter(p -> {
			return p.getPokemonId() !=PokemonId.DITTO && !repository.UNRELEASED_POKEMONS.contains(p.getPokemonId());
		}).collect(Collectors.toList());
	}
	
	
	@Override
    public boolean compressResults() {
    	return false;
    }
    @Override
    public int getNumWorstDefenderToKeep() {
        return 20;
    }

	@Override
    public int getNumWorstSubDefenderToKeep() {
        return 1;
    }
	@Override
	public String getValue() {
		return pokemonId.name();
	}

    
    
}
