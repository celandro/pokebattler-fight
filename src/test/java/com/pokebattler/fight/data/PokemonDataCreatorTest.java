package com.pokebattler.fight.data;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.CpMRepository;
import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.ResistRepository;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.PokemonTypeOuterClass.PokemonType;

public class PokemonDataCreatorTest {
    static Formulas formulas;
    static PokemonRepository pokemonRepo;
    static CpMRepository cpmRepository;
    static PokemonDataCreator creator;
    @BeforeClass
    public static void setUp() throws Exception {
        cpmRepository = new CpMRepository();
        formulas = new Formulas(cpmRepository, new ResistRepository());
        pokemonRepo = new PokemonRepository();
        creator = new PokemonDataCreator(cpmRepository, pokemonRepo, formulas);
        
    }
    @Test
    public void testTransformCp() {
        Pokemon a = pokemonRepo.getById(PokemonId.DITTO);
        Pokemon b = pokemonRepo.getById(PokemonId.FLAREON);
        PokemonData attacker = creator.createPokemon(a.getPokemonId(),  "27", 2,  13, 10, a.getQuickMoves(0), b.getCinematicMoves(0));
        PokemonData defender = creator.createPokemon(b.getPokemonId(),  "15.5", 15,  15, 8, a.getQuickMoves(0), b.getCinematicMoves(0));
        assertEquals(470, formulas.calculateCp(attacker,a));
        assertEquals(1255, formulas.calculateCp(defender,b));
        
        attacker = creator.transform(attacker, defender);
        assertEquals(2082, formulas.calculateCp(attacker,b));
        
    }

}
