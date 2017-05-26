package com.pokebattler.fight.calculator.dodge;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;

public class DodgeReactionTimeTest {
	DodgeReactionTime normal;
	DodgeReactionTime pro;
	static MoveRepository moveRepository;
	@BeforeClass
	public static void oneTime() throws Exception {
		moveRepository = new MoveRepository();
	}
	@Before
	public void setup()  {
		normal = (new DodgeReactionTime.Builder()).build(null);
		pro = (new DodgeReactionTime.Builder()).build(null);
	}

	@Test
	public void testChanceToDodge() {
	}

	@Test
	public void testQuickBonus() {
		Move bite = moveRepository.getById(PokemonMove.BITE_FAST);
		Move confusion = moveRepository.getById(PokemonMove.CONFUSION_FAST);
		Move solarBeam = moveRepository.getById(PokemonMove.SOLAR_BEAM);
		assertEquals(0.502,normal.quickBonus(bite, bite, 0.5), 1E-6);
		assertEquals(0.514,normal.quickBonus(bite, confusion, 0.5), 1E-6);
		assertEquals(0.5,normal.quickBonus(confusion, bite, 0.5), 1E-6);

		assertEquals(0.598,normal.quickBonus(bite, solarBeam, 0.5), 1E-6);
		assertEquals(0.52375,normal.quickBonus(confusion, solarBeam, 0.5), 1E-6);
		assertEquals(0.5010204,normal.quickBonus(solarBeam, solarBeam, 0.5), 1E-6);
		
	}

	@Test
	public void testIsSuperEffective() {
	}

}
