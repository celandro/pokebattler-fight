package com.pokebattler.fight.ranking;

import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.ranking.filter.RankingsFilter;
import com.pokebattler.fight.ranking.sort.RankingsSort;

class RankingParams {
    
    final private String attackerLevel;
    final private String defenderLevel;
    final private AttackStrategyType attackStrategy;
    final private AttackStrategyType defenseStrategy;
    final private RankingsSort sort;
    final private RankingsFilter filter;
    public RankingParams(String attackerLevel, String defenderLevel, AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy, RankingsSort sort, RankingsFilter filter) {
        super();
        this.attackerLevel = attackerLevel;
        this.defenderLevel = defenderLevel;
        this.attackStrategy = attackStrategy;
        this.defenseStrategy = defenseStrategy;
        this.sort = sort;
        this.filter = filter;
    }
    public String getAttackerLevel() {
        return attackerLevel;
    }
    public String getDefenderLevel() {
        return defenderLevel;
    }
    public AttackStrategyType getAttackStrategy() {
        return attackStrategy;
    }
    public AttackStrategyType getDefenseStrategy() {
        return defenseStrategy;
    }
    public RankingsSort getSort() {
        return sort;
    }
    public RankingsFilter getFilter() {
        return filter;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attackStrategy == null) ? 0 : attackStrategy.hashCode());
		result = prime * result + ((attackerLevel == null) ? 0 : attackerLevel.hashCode());
		result = prime * result + ((defenderLevel == null) ? 0 : defenderLevel.hashCode());
		result = prime * result + ((defenseStrategy == null) ? 0 : defenseStrategy.hashCode());
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + ((sort == null) ? 0 : sort.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RankingParams other = (RankingParams) obj;
		if (attackStrategy != other.attackStrategy)
			return false;
		if (attackerLevel == null) {
			if (other.attackerLevel != null)
				return false;
		} else if (!attackerLevel.equals(other.attackerLevel))
			return false;
		if (defenderLevel == null) {
			if (other.defenderLevel != null)
				return false;
		} else if (!defenderLevel.equals(other.defenderLevel))
			return false;
		if (defenseStrategy != other.defenseStrategy)
			return false;
		if (filter == null) {
			if (other.filter != null)
				return false;
		} else if (!filter.equals(other.filter))
			return false;
		if (sort == null) {
			if (other.sort != null)
				return false;
		} else if (!sort.equals(other.sort))
			return false;
		return true;
	}
    


    
    
}