package com.pokebattler.fight.ranking;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;
import com.pokebattler.fight.ranking.filter.RankingsFilter;
import com.pokebattler.fight.ranking.sort.RankingsSort;

public class RankingParams {
    
    final private AttackStrategyType attackStrategy;
    final private AttackStrategyType defenseStrategy;
    final private RankingsSort sort;
    final private RankingsFilter filter;
    final private PokemonCreator attackerCreator;
    final private PokemonCreator defenderCreator;
    final private DodgeStrategyType dodgeStrategy;
    final private long seed;
    private Set<PokemonPair> optimizedFightSet;
    
    public RankingParams(AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy, RankingsSort sort, RankingsFilter filter,
    		PokemonCreator attackerCreator, PokemonCreator defenderCreator, DodgeStrategyType dodgeStrategy, long seed) {
        super();
        this.attackStrategy = attackStrategy;
        this.defenseStrategy = defenseStrategy;
        this.sort = sort;
        this.filter = filter;
        this.attackerCreator = attackerCreator;
        this.defenderCreator = defenderCreator;
        this.dodgeStrategy = dodgeStrategy;
        this.seed = seed;
        this.optimizedFightSet = null;
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
    
	
	public PokemonCreator getAttackerCreator() {
		return attackerCreator;
	}
	public PokemonCreator getDefenderCreator() {
		return defenderCreator;
	}
	public DodgeStrategyType getDodgeStrategy() {
		return dodgeStrategy;
	}
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attackStrategy == null) ? 0 : attackStrategy.hashCode());
		result = prime * result + ((attackerCreator == null) ? 0 : attackerCreator.hashCode());
		result = prime * result + ((defenderCreator == null) ? 0 : defenderCreator.hashCode());
		result = prime * result + ((defenseStrategy == null) ? 0 : defenseStrategy.hashCode());
		result = prime * result + ((dodgeStrategy == null) ? 0 : dodgeStrategy.hashCode());
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + (int)seed;
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
		if (attackerCreator == null) {
			if (other.attackerCreator != null)
				return false;
		} else if (!attackerCreator.equals(other.attackerCreator))
			return false;
		if (defenderCreator == null) {
			if (other.defenderCreator != null)
				return false;
		} else if (!defenderCreator.equals(other.defenderCreator))
			return false;
		if (defenseStrategy != other.defenseStrategy)
			return false;
		if (dodgeStrategy != other.dodgeStrategy)
			return false;
		if (filter == null) {
			if (other.filter != null)
				return false;
		} else if (!filter.equals(other.filter))
			return false;
		if (seed != other.seed)
			return false;
		if (sort == null) {
			if (other.sort != null)
				return false;
		} else if (!sort.equals(other.sort))
			return false;
		return true;
	}
	public long getSeed() {
		return seed;
	}
	public void setOptimizedFightSet(Set<PokemonPair> optimizedFightSet) {
		this.optimizedFightSet = optimizedFightSet;
		
	}
	public Set<PokemonPair> getOptimizedFightSet() {
		return optimizedFightSet;
	}
	@Override
	public String toString() {
		return "RankingParams [attackStrategy=" + attackStrategy + ", defenseStrategy=" + defenseStrategy + ", sort="
				+ sort.getType() + ", filter=" + filter.getType() + "=" + filter.getValue() + ", dodgeStrategy=" + dodgeStrategy + ", seed=" + seed + ", optimizedFightSet="
				+ optimizedFightSet + "]";
	}
    


    
    
}