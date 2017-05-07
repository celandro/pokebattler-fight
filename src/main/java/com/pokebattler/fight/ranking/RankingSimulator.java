package com.pokebattler.fight.ranking;

import com.pokebattler.fight.data.proto.Ranking.RankingResult;

public interface RankingSimulator {

	RankingResult rank(RankingParams params);

}