package com.pokebattler.fight.ranking;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;

@Component
public class AttackerTimeRankingsSort implements RankingsSort{
    public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
        // smallest combat time followed by biggest power
        // losses take max time so they arent considered good
        return Comparator.<SubResultTotalOrBuilder>comparingInt(total -> total.getCombatTime() + total.getNumLosses() * Formulas.MAX_COMBAT_TIME_MS)
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