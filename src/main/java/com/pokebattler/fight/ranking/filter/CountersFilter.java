package com.pokebattler.fight.ranking.filter;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.Ranking.FilterType;

@Component
public class CountersFilter implements RankingsFilter {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final FilterType filterType = FilterType.COUNTERS;
    private final int numForTotal;
    public CountersFilter() {
        this(1);
    }
    public CountersFilter(int numForTotal) {
        this.numForTotal = numForTotal;
    }

    @Override
    public FilterType getType() {
        return filterType;
    }

    @Override
    public RankingsFilter forValue(String filterValue) {
        try {
            return new CountersFilter(Integer.parseInt(filterValue));
        } catch (Exception e) {
            log.error("Could not create filter",e);
            return this;
        }
    }

    @Override
    public int getNumWorstDefenderToKeep() {
        return numForTotal;
    }
    @Override
    public int getNumWorstSubDefenderToKeep() {
        return 1;
    }
    
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filterType == null) ? 0 : filterType.hashCode());
		result = prime * result + numForTotal;
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
		CountersFilter other = (CountersFilter) obj;
		if (filterType != other.filterType)
			return false;
		if (numForTotal != other.numForTotal)
			return false;
		return true;
	}

	@Override
    public Collection<Pokemon> getAttackers(PokemonRepository repository) {
    	return repository.getAllEndGameGoodDefender().getPokemonList();
    }

	@Override
    public Collection<Pokemon> getDefenders(PokemonRepository repository) {
    	return repository.getAllEndGameAttacker().getPokemonList();
    }
	@Override
	public boolean compressResults() {
		return true;
	}
	@Override
	public String getValue() {
		return Integer.toString(numForTotal);
	}
	@Override
	public RankingsFilter getOptimizer() {
		// TODO Auto-generated method stub
		return new CountersFilter(numForTotal) {
			@Override
		    public int getNumWorstDefenderToKeep() {
		        return (int)(numForTotal * 3);
		    }
		    @Override
		    public int getNumWorstSubDefenderToKeep() {
		        return 5;
		    }
			@Override
			public int getNumBestAttackerToKeep() {
				return (int) (RankingsFilter.TRIM_TO * 1.5);
			}

			@Override
			public boolean compressResults() {
				return false;
			}
		
		};
	}

}
