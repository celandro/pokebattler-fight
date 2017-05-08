package com.pokebattler.fight.ranking;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.Ranking.RankingResult;

public class CachingRankingSimulatorTest {
	CachingRankingSimulator simulator;
	@Before
	public void setup() {
		simulator = new CachingRankingSimulator();
	}
	@Test
	public void testProtoDeserialize() throws Exception {
		InputStream input = getClass().getResourceAsStream("/rankings.bin");
		RankingResult result = RankingResult.parseFrom(input);
		assertEquals(AttackStrategyType.DODGE_WEAVE_CAUTIOUS,result.getAttackStrategy());
	}

}
