package com.pokebattler.fight.ranking.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
    public int getNumWorstToKeep() {
        return numForTotal;
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

    
}
