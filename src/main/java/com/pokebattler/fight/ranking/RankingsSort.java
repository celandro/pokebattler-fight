package com.pokebattler.fight.ranking;

import java.util.Comparator;

import com.pokebattler.fight.data.proto.Ranking.AttackerResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.AttackerSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.DefenderResultOrBuilder;

public interface RankingsSort {
    public Comparator<AttackerResultOrBuilder> getAttackerResultComparator();
    public Comparator<AttackerSubResultOrBuilder> getAttackerSubResultComparator();
    public Comparator<DefenderResultOrBuilder> getDefenderResultComparator();
    
}