package com.pokebattler.fight.ranking.sort;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.FightOuterClass.FightResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;

@Component
public class AttackerPotionsSort implements RankingsSort {
	public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
		// we will count a death as 100 damage
		return Comparator.<SubResultTotalOrBuilder>comparingDouble(total -> total.getPotions())
				.thenComparing(Comparator.comparingDouble(total -> -total.getPower()));

	}

	@Override
	public Comparator<FightResultOrBuilder> getFightResultComparator() {
		return Comparator.<FightResultOrBuilder>comparingDouble(result -> result.getPotions())
				.thenComparing(Comparator.comparingDouble(result -> -result.getPower()));
	}

	@Override
	public SortType getType() {
		return SortType.POTIONS;
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