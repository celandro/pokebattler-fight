package com.pokebattler.fight.calculator;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.CombatResult;
import com.pokebattler.fight.data.proto.FightOuterClass.Combatant;
import com.pokebattler.fight.data.proto.FightOuterClass.CombatantResult;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.strategies.AttackStrategy.PokemonAttack;

public class CombatantState {
    private double attack;
    private double defense;
    private final int startHp;
    private final long id;
    private final Formulas f;
    private final int cp;
    private PokemonId pokemonId;
    private Pokemon pokemon;
    private final boolean defender;
    private int timeSinceLastMove;
    private int currentHp;
    private int currentEnergy;
    private int combatTime;
    private int damageDealt;
    private int numAttacks;
    private PokemonAttack nextAttack;
    private Move nextMove;
    private boolean dodged = false;
    private boolean damageAlreadyOccurred = false;

    public boolean isNextMoveSpecial() {
        return !getNextMove().getMoveId().name().endsWith("FAST");
    }

    public boolean isDodged() {
        return dodged;
    }

    public PokemonId getPokemonId() {
        return pokemonId;
    }
    public Pokemon getPokemon() {
    	return pokemon;
    }
    public int getDamageDealt() {
        return damageDealt;
    }

    public int getTimeSinceLastMove() {
        return timeSinceLastMove;
    }

    public double getAttack() {
        return attack;
    }

    public double getDefense() {
        return defense;
    }

    public int getStartHp() {
        return startHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public int getCombatTime() {
        return combatTime;
    }

    public int getActualCombatTime() {
        return combatTime + timeSinceLastMove;
    }

    public int getNumAttacks() {
        return numAttacks;
    }

    public long getId() {
        return id;
    }

    public PokemonAttack getNextAttack() {
        return nextAttack;
    }

    public Move getNextMove() {
        return nextMove;
    }

    public CombatantState(Pokemon p, PokemonData ind, Formulas f, boolean defender) {
        this.id = ind.getId();
        this.pokemonId = p.getPokemonId();
        this.f = f;
        this.cp = ind.getCp();
        this.attack = f.getCurrentAttack(p.getStats().getBaseAttack(), ind.getIndividualAttack(),
                ind.getCpMultiplier());
        this.defense = f.getCurrentDefense(p.getStats().getBaseDefense(), ind.getIndividualDefense(),
                ind.getCpMultiplier());
        this.defender = defender;
        this.startHp = this.currentHp = defender
                ? f.getDefenderHp(p.getStats().getBaseStamina(), ind.getIndividualStamina(), ind.getCpMultiplier())
                : f.getCurrentHP(p.getStats().getBaseStamina(), ind.getIndividualStamina(), ind.getCpMultiplier());
        // TODO: Fix this when we want to support multiple fights in a row
        this.combatTime = 0;
        this.timeSinceLastMove = 0;
        this.nextAttack = null;
        this.nextMove = null;
        this.pokemon = p;
    }

    boolean isAlive() {
        return currentHp > 0;
    }

    public int getTimeToNextAttack() {
        return nextAttack.getDelay() + nextMove.getDurationMs() - getTimeSinceLastMove();
    }

    public int getTimeToNextDamage() {
        if (damageAlreadyOccurred) {
            return Integer.MAX_VALUE;
        } else {
            // some moves on defense this will return negative on move 2 which causes bad things
            return Math.max(0, nextAttack.getDelay() + nextMove.getDamageWindowStartMs() - getTimeSinceLastMove());
        }
    }

    int applyDefense(CombatResult r, int time) {
        int energyGain = f.energyGain(r.getDamage());
        currentEnergy = Math.max(0, Math.min(defender ? Formulas.MAX_DEFENDER_ENERGY : Formulas.MAX_ENERGY, currentEnergy + energyGain));
        currentHp -= r.getDamage();
        timeSinceLastMove += time;
        combatTime += r.getCombatTime();
        if (r.getAttackMove() == PokemonMove.DODGE
                && getTimeToNextDamage() <= Formulas.DODGE_WINDOW && getTimeToNextDamage() >= 0 ) {
            dodged = true;
        }
        return energyGain;
    }

    int applyAttack(CombatResult r, int time) {
        numAttacks++;

        timeSinceLastMove += time;
        damageAlreadyOccurred = true;
        combatTime += time;
        damageDealt += r.getDamage();
        // apply energy gain here due to server side bug. When this is fixed 
        int energyGain = nextMove.getEnergyDelta();
        currentEnergy = Math.max(0, Math.min(defender ? Formulas.MAX_DEFENDER_ENERGY : Formulas.MAX_ENERGY, currentEnergy + energyGain));
        return energyGain;

    }
    void resetAttack(int time) {
        // reset things that happen inbetween attacks
        combatTime += time;
        timeSinceLastMove = 0; // -1 * delay;
        nextAttack = null;
        nextMove = null;
        dodged = false;
        damageAlreadyOccurred = false;
    }
    void moveTime(int time) {
        combatTime += time;
        timeSinceLastMove += time;
    }

    public CombatantResult toResult(Combatant combatant, AttackStrategyType strategy, int actualCombatTime) {
        return CombatantResult.newBuilder().setStrategy(strategy).setDamageDealt(getDamageDealt()).setCp(cp)
                .setCombatTime(actualCombatTime).setDps(1000.0f * (getDamageDealt()) / actualCombatTime)
                .setEnergy(getCurrentEnergy()).setStartHp(getStartHp()).setEndHp(getCurrentHp()).setPokemon(pokemonId)
                .setCombatant(combatant).setId(getId()).build();
    }

    public int setNextAttack(PokemonAttack nextAttack, Move nextMove) {
        this.nextAttack = nextAttack;
        this.nextMove = nextMove;
        // do not return back the energy now because there is a bug where energy gained is taken away at damage dealt
        return 0;

//        int energyGain = nextMove.getEnergyDelta();
//        currentEnergy = Math.max(0, Math.min(defender ? Formulas.MAX_DEFENDER_ENERGY : Formulas.MAX_ENERGY, currentEnergy + energyGain));
//        return energyGain;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


}
