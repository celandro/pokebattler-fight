package com.pokebattler.fight.strategies;

import static com.pokebattler.fight.data.MoveRepository.DODGE_MOVE;

import java.util.Random;

import com.pokebattler.fight.calculator.AttackDamage;
import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.calculator.dodge.AlwaysDodge;
import com.pokebattler.fight.calculator.dodge.DodgeStrategy;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.strategies.AttackStrategy.PokemonAttack;

public interface AttackStrategy {
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState);

    public AttackStrategyType getType();
    public AttackDamage getMove1Damage();
    public AttackDamage getMove2Damage();
    
    
    default public PokemonAttack getMove1Attack(int extraDelay) {
    	return new PokemonAttack(extraDelay, getMove1Damage());
    }
    default public PokemonAttack getMove2Attack(int extraDelay) {
    	return new PokemonAttack(extraDelay, getMove2Damage());
    }
    default public PokemonAttack getDodge(int extraDelay) {
    	return new PokemonAttack(MoveRepository.DODGE_MOVE, extraDelay, 0);
    }


    public static int DODGE_COOLDOWN = 500;
    // you can charge during your attack animation
    public static final int CAST_TIME = 0;

    public static class PokemonAttack {
        private final Move move;
        private final int delay;
        private final int damage;
        public PokemonAttack(int delay, AttackDamage damage) {
        	this(damage.getMove(), delay, damage.getDamage());
        }

        public PokemonAttack(Move move, int delay, int damage) {
            this.move = move;
            this.delay = delay;
            this.damage = damage;
        }

        public Move getMove() {
            return move;
        }

        public int getDelay() {
            return delay;
        }
        public int getDamage() {
        	return damage;
        }

    }

    public static interface AttackStrategyBuilder<S extends AttackStrategy> {
        public default AttackStrategyType getType() {
            final PokemonData fake = PokemonData.newBuilder().setMove1(PokemonMove.LICK).setMove2(PokemonMove.BODY_SLAM)
                    .setPokemonId(PokemonId.SNORLAX).build();
            final AttackDamage dummyDamage = new AttackDamage(0,  null,  1);
            return build(fake, new AlwaysDodge(),dummyDamage,dummyDamage,new Random()).getType();
        }

        S build(PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage attackDamage, AttackDamage attackDamage2, Random r);
    }
}
