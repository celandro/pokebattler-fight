package com.pokebattler.fight.data;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;

public class FormulasTest {
    static Formulas formulas;
    static PokemonRepository pokemonRepo;
    static CpMRepository cpmRepository;
    @BeforeClass
    public static void setUp() throws Exception {
        cpmRepository = new CpMRepository();
        formulas = new Formulas(cpmRepository, new ResistRepository());
        pokemonRepo = new PokemonRepository();
    }

    @Test
    public void testHp() {
        assertEquals(85, formulas.getCurrentHP(pokemonRepo.getById(PokemonId.BLASTOISE).getStats().getBaseStamina(), 7, cpmRepository.getCpM("15").getCpm()));
        assertEquals(111, formulas.getCurrentHP(pokemonRepo.getById(PokemonId.DRAGONITE).getStats().getBaseStamina(), 3, cpmRepository.getCpM("20.5").getCpm()));
        assertEquals(114, formulas.getCurrentHP(pokemonRepo.getById(PokemonId.EXEGGUTOR).getStats().getBaseStamina(), 7, cpmRepository.getCpM("19").getCpm()));
        assertEquals(42, formulas.getCurrentHP(pokemonRepo.getById(PokemonId.ELECTABUZZ).getStats().getBaseStamina(), 8, cpmRepository.getCpM("5.5").getCpm()));
    }
    @Test
    public void testCp() {
        Pokemon p = pokemonRepo.getById(PokemonId.SNORLAX);
        assertEquals(1850, formulas.calculateCp("21.5", p.getStats().getBaseAttack(), 9, p.getStats().getBaseDefense()
                , 15, p.getStats().getBaseStamina(), 14));
        assertEquals(1850, formulas.calculateCp("21.5", p.getStats().getBaseAttack(), 10, p.getStats().getBaseDefense()
                , 13, p.getStats().getBaseStamina(), 14));
        assertEquals(1850, formulas.calculateCp("22", p.getStats().getBaseAttack(), 9, p.getStats().getBaseDefense()
                , 9, p.getStats().getBaseStamina(), 9));
    }

}
