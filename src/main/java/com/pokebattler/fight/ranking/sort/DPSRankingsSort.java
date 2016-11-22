package com.pokebattler.fight.ranking.sort;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.Ranking.DefenderSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;

@Component
public class DPSRankingsSort implements RankingsSort{
    public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
        // biggest power then biggest wins then biggest damage dealt
        return Comparator.<SubResultTotalOrBuilder>comparingDouble(total -> -total.getDamageDealt()/ (double)total.getCombatTime());
    }
    @Override
    public Comparator<DefenderSubResultOrBuilder> getDefenderSubResultComparator() {
        return Comparator.<DefenderSubResultOrBuilder>comparingDouble(result -> -result.getResultOrBuilder().getCombatantsOrBuilder(0).getDps());
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