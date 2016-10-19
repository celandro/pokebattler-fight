package com.pokebattler.fight.data;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.CodedInputStream;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemons;
import com.pokebattler.fight.data.raw.RawData;

public class PokemonRepositoryTest {
    PokemonRepository p;
    
    @Before
    public void setUp() throws Exception {
        p = new PokemonRepository() {

            @Override
            public Pokemons createPokemons(RawData rawData) {
                return Pokemons.newBuilder().build();
            }
            
        };
    }

    @Test
    public void testAddCinematicMoves() throws IOException{
        //hitmonlee
        Pokemon.Builder b = Pokemon.newBuilder();
        p.addCinematicMoves("{\\1778 ", b);        
        assertEquals(PokemonMove.BRICK_BREAK,b.getCinematicMoves(0));
        assertEquals(PokemonMove.STOMP,b.getCinematicMoves(1));
        assertEquals(PokemonMove.LOW_SWEEP,b.getCinematicMoves(2));
        assertEquals(PokemonMove.STONE_EDGE,b.getCinematicMoves(3));
    
    }

    @Test
    public void testAddQuickMoves() {
        // Omastar
        Pokemon.Builder b = Pokemon.newBuilder();
        p.addQuickMoves("\\343\\001\\346\\001\\330\\001", b);        
        assertEquals(PokemonMove.ROCK_THROW_FAST,b.getQuickMoves(0));
        assertEquals(PokemonMove.WATER_GUN_FAST,b.getQuickMoves(1));
        assertEquals(PokemonMove.MUD_SHOT_FAST,b.getQuickMoves(2));
    }

}
