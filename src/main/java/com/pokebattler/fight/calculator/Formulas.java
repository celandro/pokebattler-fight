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
import com.pokebattler.fight.data.proto.FightOuterClass.CombatResult;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData.Builder;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonDataOrBuilder;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.PokemonTypeOuterClass.PokemonType;

/*
 * Based on https://drive.google.com/file/d/0B0TeYGBPiuzaenhUNE5UWnRCVlU/view
 */
@Component
public class Formulas {
    private static final int DODGE_MODIFIER = 4;
    @Resource
    CpMRepository cpmRepository;
    @Resource
    ResistRepository resistRepository;
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 40;
    public static final int MIN_INDIVDIUAL_STAT = 0;
    public static final int MAX_INDIVDIUAL_STAT = 15;
    public static final int MAX_COMBAT_TIME_MS = 100000;
    public static final int START_COMBAT_TIME = 700;
    public static final int DODGE_WINDOW = 700;
    Random r = new Random();
    Logger log = LoggerFactory.getLogger(getClass());

    public Formulas() {

    }

    public Formulas(CpMRepository cpmRepository, ResistRepository resistRepository) {
        this.cpmRepository = cpmRepository;
        this.resistRepository = resistRepository;
    }

    public int getCurrentHP(int baseStam, int indStam, double cpm) {
        return getCurrentHP(baseStam + indStam, cpm);
    }
    public int getCurrentHP(int stam, double cpm) {
        return (int) (stam * cpm);
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
    public int getDefenderHp(int stam, double cpm) {
        return 2 * getCurrentHP(stam, cpm);
    }

    public int calculateCp(String level, int baseAttack, int indAttack, int baseDefense, int indDefense, int baseStam,
            int indStam) {
        final CpM cpM = cpmRepository.getCpM(level);
        if (cpM == null) {
            throw new IllegalArgumentException("Could not find cpm for level " + level);
        }
        final double m = cpM.getCpm();
        final double attack = (baseAttack + indAttack) * m;
        final double defense = (baseDefense + indDefense) * m;
        final double stamina = (baseStam + indStam) * m;
        return Math.max(10, (int) (0.1 * Math.sqrt(attack * attack * defense * stamina)));
    }

    public double calculateModifier(Move move, Pokemon attacker, Pokemon defender) {
        double modifier = 1.0;
        PokemonType type = move.getType();
		if (type == attacker.getType() || type == attacker.getType2()) {
            modifier *= 1.25; // stab
        }

        modifier *= resistRepository.getResist(type, defender.getType())
                * resistRepository.getResist(type, defender.getType2());

        return modifier;

    }


    public CombatResult.Builder getCombatResult(double attack, double defense, Move move, Pokemon attacker,
            Pokemon defender, boolean isDodge) {
        final int damage = damageOfMove(attack, defense, move, attacker, defender);
        return getCombatResult(damage, move, isDodge);
    }
    public int getDamageOfMove(double attack, double defense, Move move, Pokemon attacker,
            Pokemon defender) {
        return damageOfMove(attack, defense, move, attacker, defender);
    }
	public CombatResult.Builder getCombatResult(final int damage, Move move, boolean isDodge) {
		final int dodgeDamage;
        if (isDodge) {
            // divide by 4 round down but with min of 1
            dodgeDamage = Math.max(1, damage/DODGE_MODIFIER);
        } else {
            dodgeDamage = damage;
        }
        final CombatResult.Builder builder = CombatResult.newBuilder().setCombatTime(move.getDurationMs())
                .setDamage(dodgeDamage).setDamageTime(move.getDamageWindowEndMs()).setAttackMove(move.getMoveId())
                .setDodgePercent((float)damage/dodgeDamage).setCriticalHit(false);
        return builder;
	}

    public int damageOfMove(double attack, double defense, Move move, Pokemon attacker, Pokemon defender) {
        if (move == MoveRepository.DODGE_MOVE) {
            return 0;
        }
        final double modifier = calculateModifier(move, attacker, defender);

        // critical hits are not implemented
        final double critMultiplier = 1.0;
        // misses are not implemented
        final double missMultiplier = 1.0;

        // int damage = Math.max(1, (int) ((45.0 / 100.0 * attack / defense *
        // move.getPower() + 0.8)
        // * (wasCrit?1.5:1.0) * move.getAccuracyChance() * modifier));
        // rounds up if possible
        int damage = (int) (0.5 * attack / defense * move.getPower() * critMultiplier * missMultiplier * modifier) + 1;
        return damage;
    }

    public int defensePrestigeGain(double attack, double... defenses) {
        int prestigeGain = 0;
        for (final double defense : defenses) {
            if (attack < defense) {
                prestigeGain += Math.min(1000, (int) (500.0 * defense / attack));
            } else {
                prestigeGain += Math.max(100, (int) (310.0 * defense / attack - 55)) ;
            }
        }
        return prestigeGain;
    }

    public int energyGain(int damage) {
        return (damage + 1) / 2;
    }

    public int calculateCp(PokemonDataOrBuilder data, Pokemon p) {
        return calculateCp(data.getLevel(), p.getStats().getBaseAttack(), data.getIndividualAttack(), p.getStats().getBaseDefense(), 
                data.getIndividualDefense(), p.getStats().getBaseStamina(), data.getIndividualStamina());
    }

	public void setCpmRepository(CpMRepository cpmRepository) {
		this.cpmRepository = cpmRepository;
	}

	public void setResistRepository(ResistRepository resistRepository) {
		this.resistRepository = resistRepository;
	}
}
