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
                    return rankingSimulator.rank(key.getLevel(), key.getAttackStrategy(), key.getDefenseStrategy());
                  }
                });    
    public RankingResult rank(String level, AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy) {
        return rankCache.getUnchecked(new RankingCacheKey(level,attackStrategy,defenseStrategy));
    }    
    class RankingCacheKey {
        final String level;
        final AttackStrategyType attackStrategy;
        final AttackStrategyType defenseStrategy;
        public RankingCacheKey(String level, AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy) {
            super();
            this.level = level;
            this.attackStrategy = attackStrategy;
            this.defenseStrategy = defenseStrategy;
        }
        public String getLevel() {
            return level;
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
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((attackStrategy == null) ? 0 : attackStrategy.hashCode());
            result = prime * result + ((defenseStrategy == null) ? 0 : defenseStrategy.hashCode());
            result = prime * result + ((level == null) ? 0 : level.hashCode());
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
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (attackStrategy != other.attackStrategy)
                return false;
            if (defenseStrategy != other.defenseStrategy)
                return false;
            if (level == null) {
                if (other.level != null)
                    return false;
            } else if (!level.equals(other.level))
                return false;
            return true;
        }
        private CachingRankingSimulator getOuterType() {
            return CachingRankingSimulator.this;
        }
        
        
    }

}
