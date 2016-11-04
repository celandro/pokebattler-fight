package com.pokebattler.fight.ranking;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.pokebattler.fight.data.proto.FightOuterClass.CombatantResult;
import com.pokebattler.fight.data.proto.FightOuterClass.FightResult;
import com.pokebattler.fight.data.proto.Ranking.DefenderSubResult;
import com.pokebattler.fight.data.proto.Ranking.DefenderSubResultOrBuilder;

public class RankingSimulatorTest {
    RankingSimulator simulator;
    @Before
    public void setUp() throws Exception {
        simulator = new RankingSimulator();
    }

    @Test
    public void testGetPower() {
        CombatantResult attackerResult = CombatantResult.newBuilder().setStartHp(100).setEndHp(50).build();
        CombatantResult defenderResult = CombatantResult.newBuilder().setStartHp(200).setEndHp(0).build();

        FightResult.Builder fightResult = FightResult.newBuilder().addCombatants(attackerResult).addCombatants(defenderResult);
        DefenderSubResult.Builder subResult = DefenderSubResult.newBuilder().setResult(fightResult );
        assertEquals(Math.log10(2.0),simulator.getPower(subResult), 1E-6);
        
        attackerResult = CombatantResult.newBuilder().setStartHp(100).setEndHp(0).build();
        defenderResult = CombatantResult.newBuilder().setStartHp(200).setEndHp(25).build();
        fightResult.clearCombatants().addCombatants(attackerResult).addCombatants(defenderResult);
        subResult.setResult(fightResult);
        assertEquals(Math.log10(0.875),simulator.getPower(subResult), 1E-6);

        
        attackerResult = CombatantResult.newBuilder().setStartHp(100).setEndHp(100).build();
        defenderResult = CombatantResult.newBuilder().setStartHp(200).setEndHp(0).build();
        fightResult.clearCombatants().addCombatants(attackerResult).addCombatants(defenderResult);
        subResult.setResult(fightResult);
        assertEquals(1,simulator.getPower(subResult), 1E-6);

        attackerResult = CombatantResult.newBuilder().setStartHp(100).setEndHp(0).build();
        defenderResult = CombatantResult.newBuilder().setStartHp(200).setEndHp(200).build();
        fightResult.clearCombatants().addCombatants(attackerResult).addCombatants(defenderResult);
        subResult.setResult(fightResult);
        assertEquals(-1,simulator.getPower(subResult), 1E-6);
        
    }

}
