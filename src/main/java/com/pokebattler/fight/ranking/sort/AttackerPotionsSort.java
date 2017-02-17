package com.pokebattler.fight.ranking.sort;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.Ranking.DefenderSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;

@Component
public class AttackerPotionsSort implements RankingsSort {
	public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
		// we will count a death as 100 damage
		return Comparator
				.<SubResultTotalOrBuilder>comparingInt(total -> total.getDamageTaken() + total.getNumLosses() * 100)
				.thenComparing(Comparator.comparingDouble(total -> -total.getPower()));

	}

	@Override
	public Comparator<DefenderSubResultOrBuilder> getDefenderSubResultComparator() {
		// death counts as 100 damage
		return Comparator
				.<DefenderSubResultOrBuilder>comparingInt(
						result -> result.getResultOrBuilder().getCombatantsOrBuilder(1).getDamageDealt()
								+ (result.getResultOrBuilder().getWin() ? 0 : 100))
				.thenComparing(Comparator.comparingDouble(result -> -result.getResultOrBuilder().getPower()));
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