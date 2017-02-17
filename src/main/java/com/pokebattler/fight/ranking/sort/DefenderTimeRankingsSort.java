package com.pokebattler.fight.ranking.sort;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.proto.Ranking.DefenderSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;

@Component
public class DefenderTimeRankingsSort implements RankingsSort{
    @Override
    public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
        // biggest combat time first then biggest power
        return Comparator.<SubResultTotalOrBuilder>comparingInt(total -> -(total.getCombatTime()+ total.getNumWins() * Formulas.MAX_COMBAT_TIME_MS))
               .thenComparing(Comparator.comparingDouble(total -> -total.getPower())); 
    }
    @Override
    public Comparator<DefenderSubResultOrBuilder> getDefenderSubResultComparator() {
        // a win counts as a big delay
        return Comparator.<DefenderSubResultOrBuilder>comparingInt(result -> -(result.getResultOrBuilder().getTotalCombatTime() + (result.getResultOrBuilder().getWin()?Formulas.MAX_COMBAT_TIME_MS:0)))
                .thenComparing(Comparator.comparingDouble(result -> -result.getResultOrBuilder().getPower()));
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