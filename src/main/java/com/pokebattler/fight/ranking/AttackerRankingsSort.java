package com.pokebattler.fight.ranking;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.Ranking.AttackerResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.AttackerSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.DefenderResultOrBuilder;

@Component
public class AttackerRankingsSort implements RankingsSort{
    public Comparator<AttackerResultOrBuilder> getAttackerResultComparator() {
        return Comparator.<AttackerResultOrBuilder,AttackerSubResultOrBuilder>comparing(result->result.getByMove(0), getAttackerSubResultComparator());
    }

    public Comparator<AttackerSubResultOrBuilder> getAttackerSubResultComparator() {
        return Comparator.<AttackerSubResultOrBuilder>comparingInt(result -> -result.getTotalOrBuilder().getNumWins())
                .thenComparing(Comparator.<AttackerSubResultOrBuilder>comparingInt(result -> result.getTotalOrBuilder().getDamageTaken())
                .thenComparing(Comparator.<AttackerSubResultOrBuilder>comparingInt(result -> result.getTotalOrBuilder().getDamageDealt())));
    }

    public Comparator<DefenderResultOrBuilder> getDefenderResultComparator() {
        return Comparator.<DefenderResultOrBuilder>comparingInt(result -> -result.getTotalOrBuilder().getNumWins())
                .thenComparing(Comparator.<DefenderResultOrBuilder>comparingInt(result -> result.getTotalOrBuilder().getDamageTaken())
                .thenComparing(Comparator.<DefenderResultOrBuilder>comparingInt(result -> result.getTotalOrBuilder().getDamageDealt())));
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