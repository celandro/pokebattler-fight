package com.pokebattler.fight.ranking.sort;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.FightOuterClass.FightResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;

@Component
public class WinRankingsSort implements RankingsSort {
	@Override
	public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
		// biggest number wins then smallest damage taken then biggest damage
		// dealt
		return Comparator.<SubResultTotalOrBuilder>comparingInt(total -> -total.getNumWins())
				.thenComparing(Comparator.comparingDouble(total -> -total.getPower()));
	}

	@Override
	public Comparator<FightResultOrBuilder> getFightResultComparator() {
		return Comparator.<FightResultOrBuilder>comparingInt(result -> -(result.getWin() ? 1 : 0))
				.thenComparing(Comparator.comparingDouble(result -> result.getPower()));
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