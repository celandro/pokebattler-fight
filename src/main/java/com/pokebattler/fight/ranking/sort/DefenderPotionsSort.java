package com.pokebattler.fight.ranking.sort;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.Ranking.DefenderResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.DefenderSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotalOrBuilder;

@Component
public class DefenderPotionsSort implements RankingsSort {
	public Comparator<SubResultTotalOrBuilder> getSubResultComparator() {
		return Comparator.<SubResultTotalOrBuilder>comparingInt(total -> -total.getDamageDealt())
				.thenComparing(Comparator.comparingDouble(total -> -total.getPower()));

	}


	@Override
	public Comparator<DefenderResultOrBuilder> getDefenderResultComparator() {
		// just sort by power to find the worst
		return Comparator.<DefenderResultOrBuilder>comparingDouble(total -> -total.getTotal().getPower());
	}
	
	
	@Override
	public Comparator<DefenderSubResultOrBuilder> getDefenderSubResultComparator() {
		// just sort by power to find the worst
		return Comparator.<DefenderSubResultOrBuilder>comparingDouble(result -> -result.getResultOrBuilder().getPower());
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