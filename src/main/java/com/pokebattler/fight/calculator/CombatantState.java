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
    private PokemonId pokemon;
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

    public PokemonId getPokemon() {
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
        this.pokemon = p.getPokemonId();
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
        currentEnergy = Math.max(0, Math.min(defender ? 200 : 100, currentEnergy + energyGain));
        currentHp -= r.getDamage();
        timeSinceLastMove += time;
        combatTime += r.getCombatTime();
        if (r.getAttackMove() == PokemonMove.DODGE
                && getTimeToNextDamage() <= Formulas.DODGE_WINDOW && getTimeToNextDamage() >= 0 ) {
            dodged = true;
        }
        return energyGain;
    }

    void applyAttack(CombatResult r, int time) {
        numAttacks++;

        timeSinceLastMove += time;
        damageAlreadyOccurred = true;
        combatTime += time;
        damageDealt += r.getDamage();

    }
    int resetAttack(int time) {
        // energy gets subtracted at the very end, no energy gain
        int energyGain = nextMove.getEnergyDelta();
        currentEnergy = Math.max(0, Math.min(defender ? 200 : 100, currentEnergy + energyGain));
        // reset things that happen inbetween attacks
        combatTime += time;
        timeSinceLastMove = 0; // -1 * delay;
        nextAttack = null;
        nextMove = null;
        dodged = false;
        damageAlreadyOccurred = false;
        return energyGain;
    }
    void moveTime(int time) {
        combatTime += time;
        timeSinceLastMove += time;
    }

    public CombatantResult toResult(Combatant combatant, AttackStrategyType strategy, int actualCombatTime) {
        return CombatantResult.newBuilder().setStrategy(strategy).setDamageDealt(getDamageDealt()).setCp(cp)
                .setCombatTime(actualCombatTime).setDps(1000.0f * (getDamageDealt()) / actualCombatTime)
                .setEnergy(getCurrentEnergy()).setStartHp(getStartHp()).setEndHp(getCurrentHp()).setPokemon(pokemon)
                .setCombatant(combatant).setId(getId()).build();
    }

    public void setNextAttack(PokemonAttack nextAttack, Move nextMove) {
        this.nextAttack = nextAttack;
        this.nextMove = nextMove;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


}
