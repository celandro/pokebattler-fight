package com.pokebattler.fight.ranking.sort;

import java.util.Comparator;

import com.pokebattler.fight.data.proto.Ranking.AttackerResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.AttackerSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.DefenderResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.DefenderSubResult.Builder;
import com.pokebattler.fight.data.proto.Ranking.DefenderSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;

public interface RankingsSort {
    default public Comparator<AttackerResultOrBuilder> getAttackerResultComparator() {
        return Comparator.<AttackerResultOrBuilder,AttackerSubResultOrBuilder>comparing(result->result.getByMove(0), getAttackerSubResultComparator());
    }

    default public Comparator<AttackerSubResultOrBuilder> getAttackerSubResultComparator() {
        return Comparator.<AttackerSubResultOrBuilder,SubResultTotalOrBuilder>comparing(result -> result.getTotalOrBuilder(), getSubResultComparator());
    }

    default public Comparator<DefenderResultOrBuilder> getDefenderResultComparator() {
        return Comparator.<DefenderResultOrBuilder,SubResultTotalOrBuilder>comparing(result -> result.getTotalOrBuilder(), getSubResultComparator());
    }
    
    public Comparator<SubResultTotalOrBuilder> getSubResultComparator();
    public SortType getType();
    public Comparator<DefenderSubResultOrBuilder> getDefenderSubResultComparator();


}