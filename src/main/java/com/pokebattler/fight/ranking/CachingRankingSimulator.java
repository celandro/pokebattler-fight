package com.pokebattler.fight.ranking;

import javax.annotation.Resource;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.Ranking.AttackerSubResult;
import com.pokebattler.fight.data.proto.Ranking.FilterType;
import com.pokebattler.fight.data.proto.Ranking.RankingResult;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.ranking.filter.FilterRegistry;
import com.pokebattler.fight.ranking.filter.RankingsFilter;
import com.pokebattler.fight.ranking.sort.SortRegistry;

@Service
public class CachingRankingSimulator {
	@Resource
	@Qualifier("RankingSimulator")
	RankingSimulator rankingSimulator;
	@Resource
	SortRegistry sortRegistry;
	@Resource
	FilterRegistry filterRegistry;

	// big options cache
	LoadingCache<RankingParams, RankingResult> rankCache = CacheBuilder.newBuilder().maximumSize(100)
			.build(new CacheLoader<RankingParams, RankingResult>() {
				public RankingResult load(RankingParams key) {
					return rankingSimulator.rank(key);
				}
			});
	// small objects cache
	LoadingCache<RankingParams, RankingResult> filteredRankCache = CacheBuilder.newBuilder().maximumSize(100)
			.build(new CacheLoader<RankingParams, RankingResult>() {
				public RankingResult load(RankingParams key) {
					return rankingSimulator.rank(key);
				}
			});

	public RankingResult rankAttacker(AttackStrategyType attackStrategy,
			AttackStrategyType defenseStrategy, SortType sortType, FilterType filterType, String filterValue,
			PokemonCreator attackerCreator, PokemonCreator defenderCreator, DodgeStrategyType dodgeStrategy, long seed) {
		RankingsFilter filter = filterRegistry.getFilter(filterType, filterValue);
		return getCache(filterType).getUnchecked(new RankingParams(attackStrategy, defenseStrategy, 
				sortRegistry.getAttackerSort(sortType), filter, attackerCreator, defenderCreator, dodgeStrategy, seed));
	}

	private LoadingCache<RankingParams, RankingResult> getCache(FilterType filterType) {
		LoadingCache<RankingParams, RankingResult> cache = filterType == FilterType.NO_FILTER ? rankCache
				: filteredRankCache;
		return cache;
	}

	public RankingResult rankDefender(AttackStrategyType attackStrategy,
			AttackStrategyType defenseStrategy, SortType sortType, FilterType filterType, String filterValue,
			PokemonCreator attackerCreator, PokemonCreator defenderCreator, DodgeStrategyType dodgeStrategy, long seed) {
		RankingsFilter filter = filterRegistry.getFilter(filterType, filterValue);
		return getCache(filterType).getUnchecked(new RankingParams(defenseStrategy,
				attackStrategy, sortRegistry.getDefenderSort(sortType), filter, defenderCreator, attackerCreator, dodgeStrategy, seed));
	}

}
