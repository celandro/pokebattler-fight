package com.pokebattler.fight.data;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import com.pokebattler.fight.calculator.dodge.DodgeReactionTime;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;

public class MoveRepositoryTest {
	private static MoveRepository repo;
	@BeforeClass
	public static void init() throws Exception{
		// only initialize once
		repo = new MoveRepository();
		
	}
	@Test
	public void testMoves() {
		assertEquals(178, repo.getAll().getMoveList().size());
	}
	
}
