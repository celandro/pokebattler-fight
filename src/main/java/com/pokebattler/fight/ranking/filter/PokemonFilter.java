package com.pokebattler.fight.ranking.filter;

import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.Ranking.FilterType;

@Component
public class PokemonFilter implements RankingsFilter {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final FilterType filterType = FilterType.POKEMON;
    private final PokemonId pokemonId; 
    public PokemonFilter() {
    	this.pokemonId = PokemonId.UNRECOGNIZED;
    }
    public PokemonFilter(PokemonId pokemonId) {
    	this.pokemonId = pokemonId;
    }

    @Override
    public FilterType getType() {
        return filterType;
    }

    @Override
    public RankingsFilter forValue(String filterValue) {
        try {
            return new PokemonFilter(PokemonId.valueOf(filterValue));
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
    public boolean compressResults() {
    	return false;
    }

    
    
}
