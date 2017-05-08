package com.pokebattler.fight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pokebattler.fight.calculator.AttackSimulator;
import com.pokebattler.fight.ranking.RankingSimulator;

@Configuration
public class AppConfiguration {
	@Autowired
	private ApplicationContext context;

	@Bean
	public AttackSimulator RankingAttackSimulator(@Value("${RANKING_ATTACK_SIMULATOR}") String qualifier) {
		return (AttackSimulator) context.getBean(qualifier);
	}
	@Bean
	public RankingSimulator RankingSimulator(@Value("${RANKING_SIMULATOR}") String qualifier) {
		return (RankingSimulator) context.getBean(qualifier);
	}
	
}
