package com.pokebattler.fight.calculator;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.pokebattler.fight.data.proto.FightOuterClass.CombatantResult;
import com.pokebattler.fight.data.proto.FightOuterClass.FightResult;
import com.pokebattler.fight.data.proto.Ranking.DefenderSubResult;
import com.pokebattler.fight.data.proto.Ranking.DefenderSubResultOrBuilder;
import com.pokebattler.fight.ranking.RankingSimulator;

public class AttackSimulatorTest {
    AttackSimulator simulator;
    @Before
    public void setUp() throws Exception {
        simulator = new AttackSimulator();
    }

    @Test
    public void testGetPower() {
        CombatantResult attackerResult = CombatantResult.newBuilder().setStartHp(100).setEndHp(50).build();
        CombatantResult defenderResult = CombatantResult.newBuilder().setStartHp(200).setEndHp(0).build();

        FightResult.Builder fightResult = FightResult.newBuilder().addCombatants(attackerResult).addCombatants(defenderResult);
        assertEquals(Math.log10(2.0),simulator.getPower(fightResult), 1E-6);
        
        attackerResult = CombatantResult.newBuilder().setStartHp(100).setEndHp(0).build();
        defenderResult = CombatantResult.newBuilder().setStartHp(200).setEndHp(25).build();
        fightResult.clearCombatants().addCombatants(attackerResult).addCombatants(defenderResult);
        assertEquals(Math.log10(0.875),simulator.getPower(fightResult), 1E-6);

        
        attackerResult = CombatantResult.newBuilder().setStartHp(100).setEndHp(100).build();
        defenderResult = CombatantResult.newBuilder().setStartHp(200).setEndHp(0).build();
        fightResult.clearCombatants().addCombatants(attackerResult).addCombatants(defenderResult);
        assertEquals(1,simulator.getPower(fightResult), 1E-6);

        attackerResult = CombatantResult.newBuilder().setStartHp(100).setEndHp(0).build();
        defenderResult = CombatantResult.newBuilder().setStartHp(200).setEndHp(200).build();
        fightResult.clearCombatants().addCombatants(attackerResult).addCombatants(defenderResult);
        assertEquals(-1,simulator.getPower(fightResult), 1E-6);
        
    }
    @Test
    public void testPowerMath() {
        
        assertEquals(4.0,Math.pow(4.0 * 4.0 * 4.0, 1.0/3.0),1E-6);
        assertEquals(4.0, Math.pow(10, (Math.log10(4.0) + Math.log10(4.0) + Math.log10(4.0))/3.0),1E-6);
    }

}
