package com.pokebattler.fight.ranking.filter;

import com.pokebattler.fight.data.proto.Ranking.FilterType;

public interface RankingsFilter {

    FilterType getType();

    RankingsFilter forValue(String filterValue);

    int getNumForTotal();

}
