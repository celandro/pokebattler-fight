package com.pokebattler.fight.ranking.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.Ranking.FilterType;

@Component
public class CountersFilter implements RankingsFilter {
    private final Logger log = LoggerFactory.getLogger(getClass());
    final int numForTotal;
    public CountersFilter() {
        this(1);
    }
    public CountersFilter(int numForTotal) {
        this.numForTotal = numForTotal;
    }

    @Override
    public FilterType getType() {
        return FilterType.COUNTERS;
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
    public int getNumForTotal() {
        return Integer.MAX_VALUE;
    }

}
