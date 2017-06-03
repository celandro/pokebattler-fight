package com.pokebattler.fight.ranking.sort;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.FightOuterClass.FightResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.DefenderResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.DefenderSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;
import com.pokebattler.fight.ranking.RankingParams;

@Component
public class DefenderTimeRankingsSort implements RankingsSort{
    @Override
    public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
        // biggest combat time first then biggest power
        return Comparator.<SubResultTotalOrBuilder>comparingInt(total -> -total.getEffectiveCombatTime())
               .thenComparing(Comparator.comparingDouble(total -> -total.getPower())); 
    }
    
    @Override
	public Comparator<FightResultOrBuilder> getFightResultComparator() {
    	return Comparator.<FightResultOrBuilder>comparingInt(result -> -result.getEffectiveCombatTime())
                .thenComparing(Comparator.comparingDouble(result -> -result.getPower()));
    }
    
    @Override
    public SortType getType() {
        return SortType.TIME;
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
		return new DefenderTimeRankingsSort();
	}

    
}