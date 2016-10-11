package com.pokebattler.fight.calculator;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.Ranking.RankingResult;

@Service
public class CachingRankingSimulator {
    @Resource 
    RankingSimulator rankingSimulator;
    
    LoadingCache<RankingCacheKey, RankingResult> rankCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build(new CacheLoader <RankingCacheKey, RankingResult>() {
                  public RankingResult load(RankingCacheKey key) {
                    return rankingSimulator.rank(key.getAttackerLevel(), key.getDefenderLevel(),key.getAttackStrategy(), key.getDefenseStrategy());
                  }
                });    
    public RankingResult rank(String attackerLevel, String defenderLevel, AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy) {
        return rankCache.getUnchecked(new RankingCacheKey(attackerLevel,defenderLevel, attackStrategy,defenseStrategy));
    }    
    static class RankingCacheKey {
        final private String attackerLevel;
        final private String defenderLevel;
        final private AttackStrategyType attackStrategy;
        final private AttackStrategyType defenseStrategy;
        public RankingCacheKey(String attackerLevel, String defenderLevel, AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy) {
            super();
            this.attackerLevel = attackerLevel;
            this.defenderLevel = defenderLevel;
            this.attackStrategy = attackStrategy;
            this.defenseStrategy = defenseStrategy;
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
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((attackStrategy == null) ? 0 : attackStrategy.hashCode());
            result = prime * result + ((attackerLevel == null) ? 0 : attackerLevel.hashCode());
            result = prime * result + ((defenderLevel == null) ? 0 : defenderLevel.hashCode());
            result = prime * result + ((defenseStrategy == null) ? 0 : defenseStrategy.hashCode());
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
            RankingCacheKey other = (RankingCacheKey) obj;
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
            return true;
        }
        
        
    }

}
