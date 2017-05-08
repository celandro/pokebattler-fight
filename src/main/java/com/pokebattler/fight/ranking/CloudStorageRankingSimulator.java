package com.pokebattler.fight.ranking;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.proto.Ranking.AttackerSubResult;
import com.pokebattler.fight.data.proto.Ranking.FilterType;
import com.pokebattler.fight.data.proto.Ranking.RankingResult;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.ranking.filter.FilterRegistry;
import com.pokebattler.fight.ranking.filter.RankingsFilter;
import com.pokebattler.fight.ranking.sort.SortRegistry;

@Service("CloudStorageRankingSimulator")
public class CloudStorageRankingSimulator implements RankingSimulator {
//	@Resource
//	CachingRankingSimulator cachingRankingSimulator;
	@Resource
	@Value("#{ThreadedRankingSimulator}")
	ThreadedRankingSimulator threadedRankingSimulator;
	@Resource
	SortRegistry sortRegistry;
	@Resource
	FilterRegistry filterRegistry;
	@Value("${gcloud.bucketName}")
	String bucketName;

	Storage storage;
	
	Logger log = LoggerFactory.getLogger(getClass());

	@PostConstruct
	private void init(){
		storage = StorageOptions.getDefaultInstance().getService();
	}

//	public RankingResult rankAttacker(AttackStrategyType attackStrategy,
//			AttackStrategyType defenseStrategy, SortType sortType, FilterType filterType, String filterValue,
//			PokemonCreator attackerCreator, PokemonCreator defenderCreator, DodgeStrategyType dodgeStrategy, boolean noCloud) {
//		if (noCloud == false && defenseStrategy ==  AttackStrategyType.DEFENSE_RANDOM_MC) {
//			// create the url for monte carlo to see if it already exists
//			String url = "rankings/attackers/levels/" + attackerCreator.createPokemon(PokemonId.ABRA, PokemonMove.ACID_FAST, PokemonMove.ABSORB).getLevel()
//		            + "/defenders/levels/" + defenderCreator.createPokemon(PokemonId.ABRA, PokemonMove.ACID_FAST, PokemonMove.ABSORB).getLevel()
//		            + "/strategies/" + attackStrategy + '/' + AttackStrategyType.DEFENSE_RANDOM_MC + '/'
//		            + sortType + "-" + dodgeStrategy + "-" + filterType + "-" + filterValue + ".bin";
//			RankingResult retval = fetchBlob(url);
//			if (retval != null) return retval;
//		}
//		return cachingRankingSimulator.rankAttacker(attackStrategy, defenseStrategy, sortType, filterType, filterValue, attackerCreator, defenderCreator, dodgeStrategy);
//	}
//
	private RankingResult fetchBlob(String url) {
		try {
			BlobInfo blob = BlobInfo.newBuilder(bucketName, url).build();
			byte[] ranking = storage.readAllBytes(bucketName, url);
			log.info("Found in google cloud storage!");
			return RankingResult.parseFrom(ranking);
		} catch (Exception e) {
			log.info("Could not find gs://{}/{}, falling back", bucketName, url);
		}
		return null;
	}

//	public RankingResult rankDefender(AttackStrategyType attackStrategy,
//			AttackStrategyType defenseStrategy, SortType sortType, FilterType filterType, String filterValue,
//			PokemonCreator attackerCreator, PokemonCreator defenderCreator, DodgeStrategyType dodgeStrategy, boolean noCloud) {
//		if (noCloud == false && attackStrategy ==  AttackStrategyType.DEFENSE_RANDOM_MC) {
//			// create the url for monte carlo to see if it already exists
//			String url = "rankings/defenders/levels/" + defenderCreator.createPokemon(PokemonId.ABRA, PokemonMove.ACID_FAST, PokemonMove.ABSORB).getLevel()
//		            + "/attackers/levels/" + attackerCreator.createPokemon(PokemonId.ABRA, PokemonMove.ACID_FAST, PokemonMove.ABSORB).getLevel()
//		            + "/strategies/" + defenseStrategy + '/' + attackStrategy +'/'
//		            + sortType + "-" + dodgeStrategy + "-" + filterType + "-" + filterValue + ".bin";
//			RankingResult retval = fetchBlob(url);
//			if (retval != null) return retval;
//
//		}
//		return cachingRankingSimulator.rankDefender(attackStrategy, defenseStrategy, sortType, filterType, filterValue, attackerCreator, defenderCreator, dodgeStrategy);
//	}

	@Override
	public RankingResult rank(RankingParams params) {
		if (params.getAttackStrategy() == AttackStrategyType.DEFENSE_RANDOM_MC) {
			String url = "rankings/defenders/levels/" + params.getAttackerCreator().createPokemon(PokemonId.ABRA, PokemonMove.ACID_FAST, PokemonMove.ABSORB).getLevel()
		            + "/attackers/levels/" + params.getDefenderCreator().createPokemon(PokemonId.ABRA, PokemonMove.ACID_FAST, PokemonMove.ABSORB).getLevel()
		            + "/strategies/" + params.getAttackStrategy() + '/' + params.getDefenseStrategy() +'/'
		            + params.getSort().getType() + "-" + params.getDodgeStrategy() + "-" + params.getFilter().getType() + "-" + params.getFilter().getValue() + ".bin";
			RankingResult retval = fetchBlob(url);
			if (retval != null) return retval;
		}
		if (params.getDefenseStrategy() ==  AttackStrategyType.DEFENSE_RANDOM_MC) {
			// create the url for monte carlo to see if it already exists
			String url = "rankings/attackers/levels/" + params.getAttackerCreator().createPokemon(PokemonId.ABRA, PokemonMove.ACID_FAST, PokemonMove.ABSORB).getLevel()
		            + "/defenders/levels/" + params.getDefenderCreator().createPokemon(PokemonId.ABRA, PokemonMove.ACID_FAST, PokemonMove.ABSORB).getLevel()
		            + "/strategies/" + params.getAttackStrategy() + '/' +  params.getDefenseStrategy() + '/'
		            +  params.getSort().getType() + "-" + params.getDodgeStrategy() + "-" +  params.getFilter().getType() + "-" +  params.getFilter().getValue() + ".bin";
			RankingResult retval = fetchBlob(url);
			if (retval != null) return retval;
		}
		
		return threadedRankingSimulator.rank(params);
	}

}
