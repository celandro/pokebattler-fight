package com.pokebattler.fight.ranking;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;

@Component
public class DefenderTimeRankingsSort implements RankingsSort{
    public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
        // biggest combat time first then biggest power
        return Comparator.<SubResultTotalOrBuilder>comparingInt(total -> -total.getCombatTime())
               .thenComparing(Comparator.<SubResultTotalOrBuilder>comparingDouble(total -> -total.getPower())); 
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

    
}