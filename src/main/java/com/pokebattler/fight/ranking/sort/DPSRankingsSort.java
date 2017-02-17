package com.pokebattler.fight.ranking.sort;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.Ranking.DefenderSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;

@Component
public class DPSRankingsSort implements RankingsSort{
    public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
        return Comparator.<SubResultTotalOrBuilder>comparingDouble(total -> -total.getDamageDealt()/ (double)total.getCombatTime())
        		.thenComparing(Comparator.comparingDouble(total -> -total.getPower()));
    }
    @Override
    public Comparator<DefenderSubResultOrBuilder> getDefenderSubResultComparator() {
        return Comparator.<DefenderSubResultOrBuilder>comparingDouble(result -> -result.getResultOrBuilder().getCombatantsOrBuilder(0).getDps())
        		.thenComparing(Comparator.comparingDouble(result -> -result.getResultOrBuilder().getPower()));
    }
    
    @Override
    public SortType getType() {
        return SortType.DPS;
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

    
}