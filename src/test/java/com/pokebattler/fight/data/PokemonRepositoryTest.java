package com.pokebattler.fight.data;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemons;
import com.pokebattler.fight.data.raw.RawData;

public class PokemonRepositoryTest {
	IndividualPokemonRepository p;

	@Before
	public void setUp() throws Exception {
		p = new IndividualPokemonRepository() {

			@Override
			public Pokemons createPokemons(RawData rawData, String legacy) {
				return Pokemons.newBuilder().build();
			}

		};
	}

	@Test
	public void testAddCinematicMoves() throws IOException {
		// hitmonlee
		Pokemon.Builder b = Pokemon.newBuilder();
		p.addCinematicMoves("{\\1778 ", b);
		assertEquals(PokemonMove.BRICK_BREAK, b.getCinematicMoves(0));
		assertEquals(PokemonMove.STOMP, b.getCinematicMoves(1));
		assertEquals(PokemonMove.LOW_SWEEP, b.getCinematicMoves(2));
		assertEquals(PokemonMove.STONE_EDGE, b.getCinematicMoves(3));

		// starmie
		b = Pokemon.newBuilder();
		p.addCinematicMoves("kA\\036\\154" + "", b);
		assertEquals(PokemonMove.HYDRO_PUMP, b.getCinematicMoves(0));
		assertEquals(PokemonMove.POWER_GEM, b.getCinematicMoves(1));
		assertEquals(PokemonMove.PSYBEAM, b.getCinematicMoves(2));
		assertEquals(PokemonMove.PSYCHIC, b.getCinematicMoves(3));
	}

	@Test
	public void testAddQuickMoves() {
		// Omastar
		Pokemon.Builder b = Pokemon.newBuilder();
		p.addQuickMoves("\\343\\001\\346\\001\\330\\001", b);
		assertEquals(PokemonMove.ROCK_THROW_FAST, b.getQuickMoves(0));
		assertEquals(PokemonMove.WATER_GUN_FAST, b.getQuickMoves(1));
		assertEquals(PokemonMove.MUD_SHOT_FAST, b.getQuickMoves(2));
	}

	@Test
	public void testGetAll() throws Exception {
		// Make sure to update the client if these change
		p = new IndividualPokemonRepository();
		CpMRepository cpmRepository = new CpMRepository();
		Formulas f = new Formulas();
		f.setCpmRepository(cpmRepository);
		assertEquals(330, p.getAll().getPokemonList().stream().mapToInt(pokemon -> pokemon.getStats().getBaseAttack())
				.max().getAsInt());
		assertEquals(396, p.getAll().getPokemonList().stream().mapToInt(pokemon -> pokemon.getStats().getBaseDefense())
				.max().getAsInt());
		assertEquals(510, p.getAll().getPokemonList().stream().mapToInt(pokemon -> pokemon.getStats().getBaseStamina())
				.max().getAsInt());
		assertEquals(4760,
				p.getAll().getPokemonList().stream()
						.mapToInt(pokemon -> f.calculateCp("40", pokemon.getStats().getBaseAttack(), 15,
								pokemon.getStats().getBaseDefense(), 15, pokemon.getStats().getBaseStamina(), 15))
						.max().getAsInt());
	}


}
