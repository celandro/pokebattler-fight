package com.pokebattler.fight.ranking;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;

@Component
public class DefenderWinRankingsSort implements RankingsSort{
    public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
        return Comparator.<SubResultTotalOrBuilder>comparingInt(total -> -total.getNumWins())
                .thenComparing(Comparator.<SubResultTotalOrBuilder>comparingInt(total -> -total.getDamageDealt())
                .thenComparing(Comparator.<SubResultTotalOrBuilder>comparingInt(total -> total.getDamageTaken())));
    }
    
    @Override
    public SortType getType() {
        return SortType.WIN;
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