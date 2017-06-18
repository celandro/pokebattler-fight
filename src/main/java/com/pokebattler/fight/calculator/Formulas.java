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
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
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
    public static final int LOSS_TIME_MS = 2000;
    public static final int MAX_ENERGY = 100;
    public static final int MAX_DEFENDER_ENERGY = MAX_ENERGY;
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
        return Math.max(10, (int) (stam * cpm));
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
		if (attacker != null && (type == attacker.getType() || type == attacker.getType2())) {
            modifier *= 1.25; // stab
        }

        modifier *= resistRepository.getResist(type, defender.getType())
                * resistRepository.getResist(type, defender.getType2());

        return modifier;

    }


    public CombatResult.Builder getCombatResult(double attack, double defense, Move move, Pokemon attacker,
            Pokemon defender, boolean isDodge, double dodgeChance, int currentTime) {
        final int damage = damageOfMove(attack, defense, move, attacker, defender).getDamage();
        return getCombatResult(damage, move, isDodge, dodgeChance, currentTime);
    }
    public AttackDamage getDamageOfMove(double attack, double defense, Move move, Pokemon attacker,
            Pokemon defender) {
        return damageOfMove(attack, defense, move, attacker, defender);
    }
	public CombatResult.Builder getCombatResult(final int damage, Move move, boolean isDodge, double dodgeChance, int currentTime) {
		int dodgeDamage;
		final float dodgePercent;
		int combatTime = move.getDurationMs();
        if (isDodge) {
            // divide by 4 round down but with min of 1
        	
    		dodgeDamage = Math.max(1, damage/DODGE_MODIFIER);
    		if (dodgeChance < 1.0) {
    			// round semi randomly
    			dodgeDamage = (int) (dodgeDamage * dodgeChance + damage * (1.0-dodgeChance) + ((currentTime%137)/137.0));
    		}
    		
            dodgePercent = 1.0f - ((float)dodgeDamage)/damage;
        } else {
        	// such a nasty hack to handle partial dodges
    		if (move.getMoveId() == PokemonMove.DODGE) {
            	dodgeDamage = 0;
                dodgePercent = ((float)damage)/1000;
            } else {
	            dodgeDamage = damage;
	            dodgePercent = 0;
            }
        }
        final CombatResult.Builder builder = CombatResult.newBuilder().setCombatTime(combatTime).setCurrentTime(currentTime)
                .setDamage(dodgeDamage).setDamageTime(move.getDamageWindowEndMs()).setAttackMove(move.getMoveId())
                .setDodgePercent(dodgePercent).setCriticalHit(false);
        return builder;
	}

    public AttackDamage damageOfMove(double attack, double defense, Move move, Pokemon attacker, Pokemon defender) {
        if (move == MoveRepository.DODGE_MOVE) {
            return new AttackDamage(0, MoveRepository.DODGE_MOVE, 1.0);
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
        return new AttackDamage(damage, move, modifier);
    }

    public int defensePrestigeGain(double attackCP, double... defenseCPs) {
        int prestigeGain = 0;
        for (final double defenseCP : defenseCPs) {
            double multiplier = defenseCP / 2000.0;
            prestigeGain += Math.min(1000,  Math.round(multiplier * (500.0 * defenseCP / attackCP)));
        }
        return prestigeGain;
    }
    public int getCPForPrestigeTarget(double defenseCP, int prestige) {
    	// this range does not exist
//    	if ( (prestige >= 255 && prestige <= 499) || prestige > 1000 || prestige < 100) {
//    		throw new IllegalArgumentException (prestige + " is not a valid possible prestige amount");
//    	} 
        double multiplier = defenseCP / 2000.0;
    	
		int cp = (int) Math.round(multiplier * defenseCP * 500.0 / prestige);
		if (cp < 10) return 0;
		return cp;
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

	public double getOverallRating(int effectiveCombatTime, double potions, double powerLog, boolean isDefender) {
		double combatTimeRating = getCombatTimeRating(effectiveCombatTime);
		// cap out at 5 potions
		double potionsRating = getPotionsRating(potions);
		if (isDefender) {
			// combat time is good
			combatTimeRating = 1.0 / combatTimeRating;
			// potions is good
			potionsRating = 1.0 / potionsRating;
		}
		combatTimeRating = Math.log10(combatTimeRating);
		potionsRating = Math.log10(potionsRating);

		return Math.pow(10, (powerLog * 50.0 + combatTimeRating * 30.0 + potionsRating * 20.0) / 100);
	}

	public double getPotionsRating(double potions) {
		return Math.max(1.0, Math.min(10.0, 5.0 / potions));
	}

	public double getCombatTimeRating(int effectiveCombatTime) {
		double combatTimeRating = Math.max(0.1,
				Math.min(10.0, Formulas.MAX_COMBAT_TIME_MS / (2.0 * (double) effectiveCombatTime)));
		if (combatTimeRating <=1.0) {
			// penalize for being worse than 50s 
			combatTimeRating*= (combatTimeRating);
		}
		if (combatTimeRating <=0.4) {
			// penalize for being worse than 79s
			combatTimeRating*= (combatTimeRating / 0.4);
		}
		if (combatTimeRating <=0.25) {
			// penalize for being  worse than 88s
			combatTimeRating*= (combatTimeRating / 0.25);
		}

		if (combatTimeRating <= 0.1) {
			// penalize for death 
			combatTimeRating = 0.1;  
		}
		return combatTimeRating;
	}
}
