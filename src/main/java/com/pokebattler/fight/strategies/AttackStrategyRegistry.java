package com.pokebattler.fight.strategies;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.pokebattler.fight.calculator.AttackDamage;
import com.pokebattler.fight.calculator.dodge.DodgeStrategy;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;

@Repository
public class AttackStrategyRegistry {
    private Map<AttackStrategyType, AttackStrategy.AttackStrategyBuilder<?>> strategies;
    @Resource
    List<AttackStrategy.AttackStrategyBuilder<?>> builders;

    @PostConstruct
    public void init() {
        strategies = new EnumMap<>(AttackStrategyType.class);
        builders.stream().forEach(builder -> register(builder));
        strategies.put(AttackStrategyType.DEFENSE_RANDOM_MC, strategies.get(AttackStrategyType.DEFENSE_RANDOM));
    }

    public boolean register(AttackStrategy.AttackStrategyBuilder<?> strategyBuilder) {
        return strategies.put(strategyBuilder.getType(), strategyBuilder) != null;
    }

    public AttackStrategy create(AttackStrategyType name, PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage attackDamage, AttackDamage attackDamage2, Random r) {
        if (!strategies.containsKey(name)) {
            throw new IllegalArgumentException("Unimplemented attack strategy " + name);
        }
        return strategies.get(name).build(pokemon, dodgeStrategy, attackDamage, attackDamage2, r);
    }

}
