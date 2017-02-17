package com.pokebattler.fight.ranking.filter;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.Ranking.FilterType;

@Component
public class NoFilter implements RankingsFilter {
	private FilterType filterType = FilterType.NO_FILTER;

    @Override
    public FilterType getType() {
        return filterType;
    }

    @Override
    public RankingsFilter forValue(String filterValue) {
        return this;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filterType == null) ? 0 : filterType.hashCode());
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
		NoFilter other = (NoFilter) obj;
		if (filterType != other.filterType)
			return false;
		return true;
	}



}
