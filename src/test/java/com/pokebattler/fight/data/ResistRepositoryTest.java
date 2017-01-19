package com.pokebattler.fight.data;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.pokebattler.fight.data.proto.PokemonTypeOuterClass.PokemonType.*;

public class ResistRepositoryTest {
	ResistRepository resists;
	@Before
	public void setup() {
		resists = new ResistRepository();
	}

	@Test
	@Ignore
	public void testGetResists() {
		// verified from https://pokemongo.gamepress.gg/damage-mechanics
		// unfortunately even with various ordered maps, toString is not consistant across jdk implementations so ignoring
		assertEquals("{POKEMON_TYPE_STEEL=0.8, POKEMON_TYPE_ROCK=0.8, POKEMON_TYPE_GHOST=0.8}", 
				resists.getResists(POKEMON_TYPE_NORMAL).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), LinkedHashMap::new)).toString());

		assertEquals("{POKEMON_TYPE_GRASS=1.25, POKEMON_TYPE_ICE=1.25, POKEMON_TYPE_STEEL=1.25, POKEMON_TYPE_FIRE=0.8, POKEMON_TYPE_BUG=1.25, POKEMON_TYPE_ROCK=0.8, POKEMON_TYPE_DRAGON=0.8, POKEMON_TYPE_WATER=0.8}", 
				resists.getResists(POKEMON_TYPE_FIRE).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());

		assertEquals("{POKEMON_TYPE_GRASS=0.8, POKEMON_TYPE_FIRE=1.25, POKEMON_TYPE_GROUND=1.25, POKEMON_TYPE_ROCK=1.25, POKEMON_TYPE_DRAGON=0.8, POKEMON_TYPE_WATER=0.8}", 
				resists.getResists(POKEMON_TYPE_WATER).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());
		
		assertEquals("{POKEMON_TYPE_GRASS=0.8, POKEMON_TYPE_GROUND=0.8, POKEMON_TYPE_FLYING=1.25, POKEMON_TYPE_ELECTRIC=0.8, POKEMON_TYPE_DRAGON=0.8, POKEMON_TYPE_WATER=1.25}", 
				resists.getResists(POKEMON_TYPE_ELECTRIC).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());

		assertEquals("{POKEMON_TYPE_GRASS=0.8, POKEMON_TYPE_STEEL=0.8, POKEMON_TYPE_POISON=0.8, POKEMON_TYPE_FIRE=0.8, POKEMON_TYPE_GROUND=1.25, POKEMON_TYPE_BUG=0.8, POKEMON_TYPE_ROCK=1.25, POKEMON_TYPE_FLYING=0.8, POKEMON_TYPE_DRAGON=0.8, POKEMON_TYPE_WATER=1.25}", 
				resists.getResists(POKEMON_TYPE_GRASS).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());

		assertEquals("{POKEMON_TYPE_GRASS=1.25, POKEMON_TYPE_ICE=0.8, POKEMON_TYPE_STEEL=0.8, POKEMON_TYPE_FIRE=0.8, POKEMON_TYPE_GROUND=1.25, POKEMON_TYPE_FLYING=1.25, POKEMON_TYPE_DRAGON=1.25, POKEMON_TYPE_WATER=0.8}", 
				resists.getResists(POKEMON_TYPE_ICE).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());

		assertEquals("{POKEMON_TYPE_ICE=1.25, POKEMON_TYPE_STEEL=1.25, POKEMON_TYPE_POISON=0.8, POKEMON_TYPE_BUG=0.8, POKEMON_TYPE_ROCK=1.25, POKEMON_TYPE_FLYING=0.8, POKEMON_TYPE_FAIRY=0.8, POKEMON_TYPE_DARK=1.25, POKEMON_TYPE_PSYCHIC=0.8, POKEMON_TYPE_GHOST=0.8, POKEMON_TYPE_NORMAL=1.25}", 
				resists.getResists(POKEMON_TYPE_FIGHTING).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());

		assertEquals("{POKEMON_TYPE_GRASS=1.25, POKEMON_TYPE_STEEL=0.8, POKEMON_TYPE_POISON=0.8, POKEMON_TYPE_GROUND=0.8, POKEMON_TYPE_ROCK=0.8, POKEMON_TYPE_FAIRY=1.25, POKEMON_TYPE_GHOST=0.8}", 
				resists.getResists(POKEMON_TYPE_POISON).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());

		assertEquals("{POKEMON_TYPE_GRASS=0.8, POKEMON_TYPE_STEEL=1.25, POKEMON_TYPE_POISON=1.25, POKEMON_TYPE_FIRE=1.25, POKEMON_TYPE_BUG=0.8, POKEMON_TYPE_ROCK=1.25, POKEMON_TYPE_FLYING=0.8, POKEMON_TYPE_ELECTRIC=1.25}", 
				resists.getResists(POKEMON_TYPE_GROUND).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());

		assertEquals("{POKEMON_TYPE_GRASS=1.25, POKEMON_TYPE_STEEL=0.8, POKEMON_TYPE_FIGHTING=1.25, POKEMON_TYPE_BUG=1.25, POKEMON_TYPE_ROCK=0.8, POKEMON_TYPE_ELECTRIC=0.8}",
				resists.getResists(POKEMON_TYPE_FLYING).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());

		assertEquals("{POKEMON_TYPE_STEEL=0.8, POKEMON_TYPE_FIGHTING=1.25, POKEMON_TYPE_POISON=1.25, POKEMON_TYPE_DARK=0.8, POKEMON_TYPE_PSYCHIC=0.8}",
				resists.getResists(POKEMON_TYPE_PSYCHIC).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());

		assertEquals("{POKEMON_TYPE_GRASS=1.25, POKEMON_TYPE_STEEL=0.8, POKEMON_TYPE_FIGHTING=0.8, POKEMON_TYPE_POISON=0.8, POKEMON_TYPE_FIRE=0.8, POKEMON_TYPE_FLYING=0.8, POKEMON_TYPE_FAIRY=0.8, POKEMON_TYPE_DARK=1.25, POKEMON_TYPE_PSYCHIC=1.25, POKEMON_TYPE_GHOST=0.8}",
				resists.getResists(POKEMON_TYPE_BUG).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());

		assertEquals("{POKEMON_TYPE_ICE=1.25, POKEMON_TYPE_STEEL=0.8, POKEMON_TYPE_FIGHTING=0.8, POKEMON_TYPE_FIRE=1.25, POKEMON_TYPE_GROUND=0.8, POKEMON_TYPE_BUG=1.25, POKEMON_TYPE_FLYING=1.25}",
				resists.getResists(POKEMON_TYPE_ROCK).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());

		assertEquals("{POKEMON_TYPE_DARK=0.8, POKEMON_TYPE_PSYCHIC=1.25, POKEMON_TYPE_GHOST=1.25, POKEMON_TYPE_NORMAL=0.8}",
				resists.getResists(POKEMON_TYPE_GHOST).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());

		assertEquals("{POKEMON_TYPE_STEEL=0.8, POKEMON_TYPE_FAIRY=0.8, POKEMON_TYPE_DRAGON=1.25}",
				resists.getResists(POKEMON_TYPE_DRAGON).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());
		
		assertEquals("{POKEMON_TYPE_FIGHTING=0.8, POKEMON_TYPE_FAIRY=0.8, POKEMON_TYPE_DARK=0.8, POKEMON_TYPE_PSYCHIC=1.25, POKEMON_TYPE_GHOST=1.25}",
				resists.getResists(POKEMON_TYPE_DARK).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());

		assertEquals("{POKEMON_TYPE_ICE=1.25, POKEMON_TYPE_STEEL=0.8, POKEMON_TYPE_FIRE=0.8, POKEMON_TYPE_ROCK=1.25, POKEMON_TYPE_FAIRY=1.25, POKEMON_TYPE_ELECTRIC=0.8, POKEMON_TYPE_WATER=0.8}",
				resists.getResists(POKEMON_TYPE_STEEL).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());

		assertEquals("{POKEMON_TYPE_STEEL=0.8, POKEMON_TYPE_FIGHTING=1.25, POKEMON_TYPE_POISON=0.8, POKEMON_TYPE_FIRE=0.8, POKEMON_TYPE_DARK=1.25, POKEMON_TYPE_DRAGON=1.25}",
				resists.getResists(POKEMON_TYPE_FAIRY).entrySet().stream().filter(entry -> entry.getValue() != 1.0f)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,throwingMerger(), TreeMap::new)).toString());

	}
	
    private static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }
    
}
