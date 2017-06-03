package com.pokebattler.fight.ranking.sort;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.proto.FightOuterClass.FightResultOrBuilder;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.Ranking.AttackerResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.AttackerSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.DefenderResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.FilterType;
import com.pokebattler.fight.data.proto.Ranking.RankingResult;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;
import com.pokebattler.fight.ranking.CachingRankingSimulator;
import com.pokebattler.fight.ranking.RankingParams;

@Component
public class OverallRankingsSort implements RankingsSort{
	@Autowired
	private CachingRankingSimulator cachingSimulator;
	@Autowired
	private PokemonDataCreator creator;
	
	public OverallRankingsSort() {
		
	}
	public OverallRankingsSort(CachingRankingSimulator cachingSimulator, PokemonDataCreator creator) {
		this.cachingSimulator = cachingSimulator;
		this.creator = creator;
	}

    public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
        // biggest power then biggest wins then biggest damage dealt
        return Comparator.<SubResultTotalOrBuilder>comparingDouble(total -> -total.getOverallRating())
                .thenComparing(Comparator.<SubResultTotalOrBuilder>comparingDouble(total -> -total.getPower()));
    }
    @Override
    public Comparator<FightResultOrBuilder> getFightResultComparator() {
    	// for the defender section, try and find the worst case not the best case!
        return Comparator.<FightResultOrBuilder>comparingDouble(result -> -result.getOverallRating())
                .thenComparing(Comparator.<FightResultOrBuilder>comparingDouble(result -> -result.getPower()));
    }
    
    
    @Override
    public SortType getType() {
        return SortType.OVERALL;
    }
    
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return getClass().equals(obj.getClass());
    }

    @Override
    public RankingsSort getRelativeSort(RankingParams params) {
    	final RankingResult rankingResult = cachingSimulator.rankDefender(params.getAttackStrategy(), params.getDefenseStrategy(), 
				params.getSort().getType(), FilterType.COUNTERS, "5", params.getAttackerCreator(), 
				params.getDefenderCreator(), params.getDodgeStrategy(), params.getSeed());
    	
    	final Map<PokemonData, Double> relative = new HashMap<>();
		rankingResult.getAttackersList().forEach(attacker -> {
			attacker.getByMoveList().forEach(moveset -> {
				PokemonData d = creator.createPokemon(attacker.getPokemonId(), attacker.getCp(), moveset.getMove1(), moveset.getMove2());
				relative.put(d, 1.0/moveset.getTotal().getOverallRating());
			});
		});
    	return new OverallRankingsSort(this.cachingSimulator, this.creator) {

    		private final Logger log = LoggerFactory.getLogger(getClass());

    		@Override
    	    public Comparator<DefenderResultOrBuilder> getDefenderResultComparator() {
    	        return Comparator.<DefenderResultOrBuilder>comparingDouble(result->{
    	        	try {
	    				PokemonData key = creator.createPokemon(result.getPokemonId(), result.getCp(), result.getByMoveOrBuilder(0).getMove1(), 
	    						result.getByMoveOrBuilder(0).getMove2());
	    	        	Double bestOverall = relative.get(key);
	    	        	if (bestOverall == null) {
	    	        		return 0;
	    	        	}
	    	        	double retval = result.getTotalOrBuilder().getOverallRating() / bestOverall;
	    	        	return retval;
    	        	} catch (RuntimeException e) {
    	        		log.error("Could not compare WTF", e);
    	        		throw e;
    	        	}
    	        });
    	    }
    		@Override
    	    public RankingsSort getRelativeSort(RankingParams params) {
    	    	return this;
    	    }
    		
    	};
    }
    
}