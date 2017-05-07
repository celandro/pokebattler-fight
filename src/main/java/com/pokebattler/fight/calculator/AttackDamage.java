package com.pokebattler.fight.calculator;

import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;

public class AttackDamage {
	private final int damage;
	private final Move move;
	private final double modifier;
	public AttackDamage(int damage, Move move, double modifier) {
		super();
		this.damage = damage;
		this.move = move;
		this.modifier = modifier;
	}
	public int getDamage() {
		return damage;
	}
	public Move getMove() {
		return move;
	}
	public double getModifier() {
		return modifier;
	}
	
}