package com.pokebattler.fight.ranking.sort;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.proto.Ranking.DefenderSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;

@Component
public class AttackerWinRankingsSort implements RankingsSort{
    @Override
    public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
        // biggest number wins then smallest damage taken then biggest damage dealt
        return Comparator.<SubResultTotalOrBuilder>comparingInt(total -> -total.getNumWins())
                .thenComparing(Comparator.comparingInt(total -> total.getDamageTaken()))
                .thenComparing(Comparator.comparingInt(total -> -total.getDamageDealt()));
    }
    @Override
    public Comparator<DefenderSubResultOrBuilder> getDefenderSubResultComparator() {
        return Comparator.<DefenderSubResultOrBuilder>comparingInt(result -> -(result.getResultOrBuilder().getWin()?1:0))
                .thenComparing(Comparator.comparingInt(result -> result.getResultOrBuilder().getCombatantsOrBuilder(1).getDamageDealt()))
                .thenComparing(Comparator.comparingInt(result -> -result.getResultOrBuilder().getCombatantsOrBuilder(0).getDamageDealt())); 
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