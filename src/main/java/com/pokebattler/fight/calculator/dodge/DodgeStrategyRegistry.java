package com.pokebattler.fight.calculator.dodge;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Repository;

import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;

@Repository
public class DodgeStrategyRegistry {
    private Map<DodgeStrategyType, DodgeStrategy.DodgeStrategyBuilder<?>> strategies;
    
    @Inject
    private List<DodgeStrategy.DodgeStrategyBuilder<?>> allBuilders;

    @PostConstruct
    public void init() {
        strategies = new EnumMap<>(DodgeStrategyType.class);
        allBuilders.stream().forEach(builder -> {
        	strategies.put(builder.getType(), builder);
        });
    }

    public DodgeStrategy create(DodgeStrategyType type) {
    	if (type == null) type = DodgeStrategyType.DODGE_100;
    	return strategies.get(type).build();
    }

}
