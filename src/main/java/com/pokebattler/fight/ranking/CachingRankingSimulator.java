package com.pokebattler.fight.ranking;

import javax.annotation.Resource;
import javax.ws.rs.QueryParam;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.Ranking.FilterType;
import com.pokebattler.fight.data.proto.Ranking.RankingResult;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.ranking.filter.FilterRegistry;
import com.pokebattler.fight.ranking.filter.RankingsFilter;
import com.pokebattler.fight.ranking.sort.SortRegistry;

@Service
public class CachingRankingSimulator {
    @Resource 
    RankingSimulator rankingSimulator;
    @Resource
    SortRegistry sortRegistry;
    @Resource
    FilterRegistry filterRegistry;
    
    LoadingCache<RankingParams, RankingResult> rankCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build(new CacheLoader <RankingParams, RankingResult>() {
                  public RankingResult load(RankingParams key) {
                    return rankingSimulator.rank(key);
                  }
                });    
    public RankingResult rankAttacker(String attackerLevel, String defenderLevel, AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy, SortType sortType,
            FilterType filterType, String filterValue) {
        RankingsFilter filter = filterRegistry.getFilter(filterType, filterValue);
        return rankCache.getUnchecked(new RankingParams(attackerLevel,defenderLevel, attackStrategy,defenseStrategy, sortRegistry.getAttackerSort(sortType), filter));
    }    
    public RankingResult rankDefender(String attackerLevel, String defenderLevel, AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy, SortType sortType,
            FilterType filterType, String filterValue) {
        RankingsFilter filter = filterRegistry.getFilter(filterType, filterValue);
        return rankCache.getUnchecked(new RankingParams(defenderLevel,attackerLevel, defenseStrategy,attackStrategy, sortRegistry.getDefenderSort(sortType), filter));
    }

}
