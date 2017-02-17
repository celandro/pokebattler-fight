package com.pokebattler.fight.ranking.sort;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.proto.Ranking.DefenderSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;

@Component
public class AttackerTimeRankingsSort implements RankingsSort{
    @Override
    public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
        // smallest combat time followed by biggest power
        // losses take max time so they arent considered good
        return Comparator.<SubResultTotalOrBuilder>comparingInt(total -> total.getCombatTime() + total.getNumLosses() * Formulas.MAX_COMBAT_TIME_MS)
               .thenComparing(Comparator.comparingDouble(total -> -total.getPower())); 
    }
    @Override
    public Comparator<DefenderSubResultOrBuilder> getDefenderSubResultComparator() {
        return Comparator.<DefenderSubResultOrBuilder>comparingInt(result -> result.getResultOrBuilder().getTotalCombatTime() + (result.getResultOrBuilder().getWin()?0:Formulas.MAX_COMBAT_TIME_MS))
                .thenComparing(Comparator.comparingDouble(result -> -result.getResultOrBuilder().getPower())); 
    }
    
    @Override
    public SortType getType() {
        return SortType.TIME;
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return getClass().equals(obj.getClass());
    }


    
}