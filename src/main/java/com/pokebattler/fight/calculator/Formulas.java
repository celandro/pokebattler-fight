package com.pokebattler.fight.calculator;

import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.CpMRepository;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.ResistRepository;
import com.pokebattler.fight.data.proto.Cpm.CpM;
import com.pokebattler.fight.data.proto.FightOuterClass.*;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;

/*
 * Based on https://drive.google.com/file/d/0B0TeYGBPiuzaenhUNE5UWnRCVlU/view
 */
@Component
public class Formulas {
    private static final double DODGE_MODIFIER = 0.25;
    @Resource
    CpMRepository cpmRepository;
    @Resource
    ResistRepository resistRepository;
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 40;
    public static final int MIN_INDIVDIUAL_STAT = 0;
    public static final int MAX_INDIVDIUAL_STAT = 15;
    public static final int MAX_COMBAT_TIME_MS = 99000;
    Random r = new Random();
    Logger log = LoggerFactory.getLogger(getClass());

    public Formulas() {

    }

    public Formulas(CpMRepository cpmRepository, ResistRepository resistRepository) {
        this.cpmRepository = cpmRepository;
        this.resistRepository = resistRepository;
    }

    public int getCurrentHP(int baseStam, int indStam, double cpm) {
        return (int) ((baseStam + indStam) * cpm);
    }

    public double getCurrentAttack(int baseAttack, int indAttack, double cpm) {
        return (baseAttack + indAttack) * cpm;
    }

    public double getCurrentDefense(int baseDefense, int indDefense, double cpm) {
        return (baseDefense + indDefense) * cpm;
    }

    public int getDefenderHp(int baseStam, int indStam, double cpm) {
        return 2 * getCurrentHP(baseStam, indStam, cpm);
    }

    public int calculateCp(String level, int baseAttack, int indAttack, int baseDefense, int indDefense, int baseStam,
            int indStam) {
        CpM cpM = cpmRepository.getCpM(level);
        if (cpM == null) {
            throw new IllegalArgumentException("Could not find cpm for level " + level);
        }
        double m = cpM.getCpm();
        double attack = (baseAttack + indAttack) * m;
        double defense = (baseDefense + indDefense) * m;
        double stamina = (baseStam + indStam) * m;
        return Math.max(10, (int) (0.1 * Math.sqrt( attack * attack * defense * stamina)));
    }

    public double calculateModifier(Move move, Pokemon attacker, Pokemon defender, boolean isDodge) {
        double modifier = 1.0;
        if (move.getType() == attacker.getType() || move.getType() == attacker.getType2()) {
            modifier *= 1.25; // stab
        }

        modifier *= resistRepository.getResist(move.getType(), defender.getType()) *
                resistRepository.getResist(move.getType(), defender.getType2());
        if (isDodge) {
            modifier *=DODGE_MODIFIER;
        }
        
        return modifier;

    }

    public CombatResult.Builder attackerCombat(double attack, double defense, Move move, Pokemon attacker,
            Pokemon defender, int timeToNextAttack) {
        int damage = damageOfMove(attack, defense, move, attacker, defender, move.getCriticalChance(), false, false);
        CombatResult.Builder builder = CombatResult.newBuilder().setCombatTime(move.getDurationMs()).setDamage(damage)
                .setDamageTime(move.getDamageWindowEndMs()).setAttackMove(move.getMoveId()).setDodgePercent(0.0)
                .setCriticalHit(false);
        return builder;
    }

    public CombatResult.Builder defenderCombat(double attack, double defense, Move move, Pokemon attacker,
            Pokemon defender, int timeToNextAttack, boolean isDodge) {
        int damage = damageOfMove(attack, defense, move, attacker, defender, move.getCriticalChance(), true, isDodge);
        CombatResult.Builder builder = CombatResult.newBuilder().setCombatTime(move.getDurationMs()).setDamage(damage)
                .setDamageTime(move.getDamageWindowEndMs()).setAttackMove(move.getMoveId())
                .setDodgePercent(isDodge?DODGE_MODIFIER:0.0)
                .setCriticalHit(false);
        return builder;
    }

    private int damageOfMove(double attack, double defense, Move move, Pokemon attacker, Pokemon defender,
            float critPercent, boolean isDefender, boolean isDodge) {
        if (move == MoveRepository.DODGE_MOVE) {
            return 0;
        }
        double modifier = calculateModifier(move, attacker, defender, isDodge);

        // critical hits are not implemented
        double critMultiplier = 1.0;
        // misses are not implemented
        double missMultiplier = 1.0;
        
        // int damage = Math.max(1, (int) ((45.0 / 100.0 * attack / defense *
        // move.getPower() + 0.8)
        // * (wasCrit?1.5:1.0) * move.getAccuracyChance() * modifier));
        // rounds up if possible
        return  (int) (0.5 * attack / defense * move.getPower() 
                * critMultiplier * missMultiplier  * modifier) + 1;
    }

    public int defensePrestigeGain(double attack, double... defenses) {
        int prestigeGain = 0;
        for (double defense : defenses) {
            if (attack < defense) {
                prestigeGain += (int) (500.0 * defense / attack);
            } else {
                // unknown a guess
                prestigeGain += (int) (500.0 * defense / attack) / 3;

            }
        }
        return Math.min(1000, prestigeGain);
    }

    public int energyGain(int damage) {
        return (damage + 1) / 2;
    }
}
