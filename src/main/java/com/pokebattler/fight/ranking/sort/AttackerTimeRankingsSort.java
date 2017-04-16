package com.pokebattler.fight.ranking.sort;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.FightOuterClass.FightResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;

@Component
public class AttackerTimeRankingsSort implements RankingsSort {
	@Override
	public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
		// smallest combat time followed by biggest power
		// losses take max time so they arent considered good
		return Comparator.<SubResultTotalOrBuilder>comparingInt(total -> total.getEffectiveCombatTime())
				.thenComparing(Comparator.comparingDouble(total -> -total.getPower()));
	}

	@Override
	public Comparator<FightResultOrBuilder> getFightResultComparator() {
		return Comparator.<FightResultOrBuilder>comparingInt(result -> result.getEffectiveCombatTime())
				.thenComparing(Comparator.comparingDouble(result -> -result.getPower()));
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