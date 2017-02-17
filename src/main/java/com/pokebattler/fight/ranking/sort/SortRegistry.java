package com.pokebattler.fight.ranking.sort;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.pokebattler.fight.data.proto.Ranking.SortType;

@Repository
public class SortRegistry {
    @Resource
    AttackerWinRankingsSort attackerWin;
    @Resource
    DefenderWinRankingsSort defenderWin;
    @Resource
    PowerRankingsSort power;
    @Resource
    AttackerTimeRankingsSort attackerTime;
    @Resource
    DefenderTimeRankingsSort defenderTime;
    @Resource
    DPSRankingsSort dps;
    @Resource
    AttackerPotionsSort attackerPotions;
    @Resource
    DefenderPotionsSort defenderPotions;
    
    private final static Map<SortType,RankingsSort> attackerSorts = new EnumMap<>(SortType.class);
    private final static Map<SortType,RankingsSort> defenderSorts = new EnumMap<>(SortType.class);
    
    @PostConstruct
    public void init() {
        registerAttackerSort(power);
        registerDefenderSort(power);
        registerAttackerSort(attackerWin);
        registerDefenderSort(defenderWin);
        registerAttackerSort(attackerTime);
        registerDefenderSort(defenderTime);
        registerAttackerSort(dps);
        registerDefenderSort(dps);
        registerAttackerSort(attackerPotions);
        registerDefenderSort(defenderPotions);
    }    
    public boolean registerAttackerSort(RankingsSort sort) {
        return attackerSorts.put(sort.getType(), sort) != null;
    }
    public boolean registerDefenderSort(RankingsSort sort) {
        return defenderSorts.put(sort.getType(), sort) != null;
    }
    public RankingsSort getAttackerSort(SortType sortType) {
        return attackerSorts.get(sortType);
    }
    public RankingsSort getDefenderSort(SortType sortType) {
        return defenderSorts.get(sortType);
    }

}
