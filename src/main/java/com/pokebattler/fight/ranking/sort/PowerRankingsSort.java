package com.pokebattler.fight.ranking.sort;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.Ranking.DefenderSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;

@Component
public class PowerRankingsSort implements RankingsSort{
    public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
        // biggest power then biggest wins then biggest damage dealt
        return Comparator.<SubResultTotalOrBuilder>comparingDouble(total -> -total.getPower())
                .thenComparing(Comparator.<SubResultTotalOrBuilder>comparingInt(total -> -total.getNumWins())
                .thenComparing(Comparator.<SubResultTotalOrBuilder>comparingInt(total -> -total.getDamageDealt())));
    }
    @Override
    public Comparator<DefenderSubResultOrBuilder> getDefenderSubResultComparator() {
    	// for the defender section, try and find the worst case not the best case!
        return Comparator.<DefenderSubResultOrBuilder>comparingDouble(result -> -result.getResultOrBuilder().getPower())
                .thenComparing(Comparator.<DefenderSubResultOrBuilder>comparingInt(result -> -(result.getResultOrBuilder().getWin()?1:0)))
                .thenComparing(Comparator.<DefenderSubResultOrBuilder>comparingInt(result -> -result.getResultOrBuilder().getCombatantsOrBuilder(0).getDamageDealt())); 
    }
    
    @Override
    public SortType getType() {
        return SortType.POWER;
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