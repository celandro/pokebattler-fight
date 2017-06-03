package com.pokebattler.fight.ranking.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.Ranking.FilterType;
import com.pokebattler.fight.data.proto.Ranking.RankingResult;
import com.pokebattler.fight.ranking.CachingRankingSimulator;
import com.pokebattler.fight.ranking.RankingParams;

@Component
public class TopDefenderFilter implements RankingsFilter {

	@Autowired
	private CachingRankingSimulator cachingSimulator;
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final FilterType filterType = FilterType.TOP_DEFENDER;
	private final PokemonId pokemonId;

	public TopDefenderFilter() {
		this.pokemonId = PokemonId.UNRECOGNIZED;
	}

	public TopDefenderFilter(PokemonId pokemonId) {
		this.pokemonId = pokemonId;
	}

	@Override
	public FilterType getType() {
		return filterType;
	}

	@Override
	public RankingsFilter forValue(String filterValue) {
		try {
			TopDefenderFilter result = new TopDefenderFilter(PokemonId.valueOf(filterValue));
			result.cachingSimulator = cachingSimulator;
			return result;
		} catch (Exception e) {
			log.error("Could not create filter", e);
			return this;
		}
	}

	@Override
	public Collection<Pokemon> getAttackers(PokemonRepository repository) {
		return Collections.singletonList(repository.getById(pokemonId));
	}

	@Override
	public Collection<Pokemon> getDefenders(PokemonRepository repository) {
		return repository.getAllEndGame().getPokemonList();
	}

	@Override
	public boolean compressResults() {
		return false;
	}

	@Override
	public int getNumWorstDefenderToKeep() {
		return RankingsFilter.TRIM_TO;
	}

	@Override
	public int getNumWorstSubDefenderToKeep() {
		return 1;
	}

	@Override
	public String getValue() {
		return pokemonId.name();
	}

	@Override
	public RankingsFilter getOptimizer(RankingParams params) {
		// lets use the best defenders
		RankingResult result = cachingSimulator.rankDefender(params.getAttackStrategy(), params.getDefenseStrategy(), 
				params.getSort().getType(), FilterType.COUNTERS, "5", params.getAttackerCreator(), 
				params.getDefenderCreator(), params.getDodgeStrategy(), params.getSeed());
		final List<PokemonId> defenders = result.getAttackersList().stream().map(attacker -> {
			return attacker.getPokemonId();
		}).collect(Collectors.toList());
		return new PokemonFilter(pokemonId) {
			@Override
			public int getNumWorstSubDefenderToKeep() {
		        return 5;
			}
			@Override
			public int getNumWorstDefenderToKeep() {
				return RankingsFilter.TRIM_TO;
			}

			@Override
			public Collection<Pokemon> getDefenders(PokemonRepository repository) {
				return defenders.stream().map(pokemonId -> {
					return repository.getById(pokemonId);
				}).collect(Collectors.toList());
			}


			@Override
			public boolean compressResults() {
				return false;
			}
			

		};
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pokemonId == null) ? 0 : pokemonId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopDefenderFilter other = (TopDefenderFilter) obj;
		if (pokemonId != other.pokemonId)
			return false;
		return true;
	}


}
