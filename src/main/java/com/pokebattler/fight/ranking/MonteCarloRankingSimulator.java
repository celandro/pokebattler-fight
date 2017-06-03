package com.pokebattler.fight.ranking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pokebattler.fight.calculator.AttackSimulator;
import com.pokebattler.fight.calculator.DelegatingSimulator;
import com.pokebattler.fight.calculator.IndividualSimulator;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.Fight;
import com.pokebattler.fight.data.proto.FightOuterClass.FightResult;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.Ranking.AttackerResult;
import com.pokebattler.fight.data.proto.Ranking.AttackerResult.Builder;
import com.pokebattler.fight.data.proto.Ranking.AttackerSubResult;
import com.pokebattler.fight.data.proto.Ranking.DefenderResult;
import com.pokebattler.fight.data.proto.Ranking.DefenderSubResult;
import com.pokebattler.fight.data.proto.Ranking.RankingResult;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotal;

@Service("MonteCarloRankingSimulator")
public class MonteCarloRankingSimulator implements RankingSimulator {
	@Autowired
	@Qualifier("ThreadedRankingSimulator")
	private ThreadedRankingSimulator simulator;

	Logger log = LoggerFactory.getLogger(getClass());

	/* (non-Javadoc)
	 * @see com.pokebattler.fight.ranking.RankingSimulator#rank(com.pokebattler.fight.ranking.RankingParams)
	 */
	@Override
	public RankingResult rank(final RankingParams params) {
		if (params.getDefenseStrategy() == AttackStrategyType.DEFENSE_RANDOM_MC) {
			RankingParams estimateParams = new RankingParams(params.getAttackStrategy(), AttackStrategyType.DEFENSE, 
					params.getSort(), params.getFilter().getOptimizer(params), params.getAttackerCreator(), params.getDefenderCreator(), 
					params.getDodgeStrategy(), -1);
			params.setOptimizedFightSet(calculateOptimizedFightSet(estimateParams));
			
			return simulator.rank(params);

		} else if (params.getAttackStrategy() == AttackStrategyType.DEFENSE_RANDOM_MC) {
			RankingParams estimateParams = new RankingParams(AttackStrategyType.DEFENSE, params.getDefenseStrategy(),  
					params.getSort(), params.getFilter().getOptimizer(params), params.getAttackerCreator(), params.getDefenderCreator(), 
					params.getDodgeStrategy(), -1);
			params.setOptimizedFightSet(calculateOptimizedFightSet(estimateParams));
			return simulator.rank(params);
			
		} else {
			return simulator.rank(params);
		}

	}
	private HashSet<PokemonPair> calculateOptimizedFightSet(RankingParams estimateParams) {
		final RankingResult estimate = simulator.rank(estimateParams);
		final HashSet<PokemonPair> allFights = new HashSet<PokemonPair>();
		for (AttackerResult attacker: estimate.getAttackersList()) {
			for (AttackerSubResult attackerMove: attacker.getByMoveList()) {
				for (DefenderResult defender: attackerMove.getDefendersList()) {
					for (DefenderSubResult subDefender: defender.getByMoveList()) {
						allFights.add(new PokemonPair(attacker.getPokemonId(), attackerMove.getMove1(), 
								attackerMove.getMove2(), defender.getPokemonId(), subDefender.getMove1(),
								subDefender.getMove2()));
					}
				}
			}
		}
		return allFights;
	}
}
