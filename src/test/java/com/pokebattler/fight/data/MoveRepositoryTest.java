package com.pokebattler.fight.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MoveRepositoryTest {
	@Test
	public void testMoves() throws Exception {
		MoveRepository repo = new MoveRepository();
		assertEquals(178, repo.getAll().getMoveList().size());
	}
}
