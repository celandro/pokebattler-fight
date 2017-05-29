package com.pokebattler.fight.ranking;

import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;

public class PokemonPair {
	private final  PokemonId attacker;
	private final PokemonMove attackerMove1;
	private final PokemonMove attackerMove2;
	private final PokemonId defender;
	private final PokemonMove defenderMove1;
	private final PokemonMove defenderMove2;
	
	public PokemonPair(PokemonId attacker, PokemonMove attackerMove1, PokemonMove attackerMove2, PokemonId defender,
			PokemonMove defenderMove1, PokemonMove defenderMove2) {
		super();
		this.attacker = attacker;
		this.attackerMove1 = attackerMove1;
		this.attackerMove2 = attackerMove2;
		this.defender = defender;
		this.defenderMove1 = defenderMove1;
		this.defenderMove2 = defenderMove2;
	}
	public PokemonId getAttacker() {
		return attacker;
	}
	public PokemonMove getAttackerMove1() {
		return attackerMove1;
	}
	public PokemonMove getAttackerMove2() {
		return attackerMove2;
	}
	public PokemonId getDefender() {
		return defender;
	}
	public PokemonMove getDefenderMove1() {
		return defenderMove1;
	}
	public PokemonMove getDefenderMove2() {
		return defenderMove2;
	}
	@Override
	public String toString() {
		return "PokemonPair [attacker=" + attacker + ", attackerMove1=" + attackerMove1 + ", attackerMove2="
				+ attackerMove2 + ", defender=" + defender + ", defenderMove1=" + defenderMove1 + ", defenderMove2="
				+ defenderMove2 + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attacker == null) ? 0 : attacker.hashCode());
		result = prime * result + ((attackerMove1 == null) ? 0 : attackerMove1.hashCode());
		result = prime * result + ((attackerMove2 == null) ? 0 : attackerMove2.hashCode());
		result = prime * result + ((defender == null) ? 0 : defender.hashCode());
		result = prime * result + ((defenderMove1 == null) ? 0 : defenderMove1.hashCode());
		result = prime * result + ((defenderMove2 == null) ? 0 : defenderMove2.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PokemonPair other = (PokemonPair) obj;
		if (attacker != other.attacker)
			return false;
		if (attackerMove1 != other.attackerMove1)
			return false;
		if (attackerMove2 != other.attackerMove2)
			return false;
		if (defender != other.defender)
			return false;
		if (defenderMove1 != other.defenderMove1)
			return false;
		if (defenderMove2 != other.defenderMove2)
			return false;
		return true;
	}
	
	
	
}