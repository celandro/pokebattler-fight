package com.pokebattler.fight.ranking.filter;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.pokebattler.fight.data.proto.Ranking.FilterType;

@Repository
public class FilterRegistry {
    @Resource
    NoFilter noFilter;
    @Resource
    CountersFilter countersFilter;
    @Resource
    PokemonFilter pokemonFilter;
    @Resource
    PrestigeFilter prestigeFilter;
    
    
    private final static Map<FilterType,RankingsFilter> filters = new EnumMap<>(FilterType.class);
    
    @PostConstruct
    public void init() {
        registerFilter(noFilter);
        registerFilter(countersFilter);
        registerFilter(pokemonFilter);
        registerFilter(prestigeFilter);
    }    
    public boolean registerFilter(RankingsFilter filter) {
        return filters.put(filter.getType(), filter) != null;
    }
    public RankingsFilter getFilter(FilterType filterType, String filterValue) {
        return filters.get(filterType).forValue(filterValue);
    }

}
