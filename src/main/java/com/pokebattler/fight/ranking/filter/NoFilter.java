package com.pokebattler.fight.ranking.filter;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.Ranking.FilterType;

@Component
public class NoFilter implements RankingsFilter {

    @Override
    public FilterType getType() {
        return FilterType.NO_FILTER;
    }

    @Override
    public RankingsFilter forValue(String filterValue) {
        return this;
    }

    @Override
    public int getNumForTotal() {
        return Integer.MAX_VALUE;
    }

}
