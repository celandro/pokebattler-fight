package com.pokebattler.fight.calculator;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.CpMRepository;
import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.ResistRepository;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.PokemonTypeOuterClass.PokemonType;

public class FormulasTest {
    static Formulas formulas;
    static PokemonRepository pokemonRepo;
    static CpMRepository cpmRepository;
    static PokemonDataCreator creator;
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
        assertEquals(1997, formulas.calculateCp("21.5", p.getStats().getBaseAttack(), 9, p.getStats().getBaseDefense()
                , 15, p.getStats().getBaseStamina(), 14));
        assertEquals(1998, formulas.calculateCp("21.5", p.getStats().getBaseAttack(), 10, p.getStats().getBaseDefense()
                , 13, p.getStats().getBaseStamina(), 14));
        assertEquals(1998, formulas.calculateCp("22", p.getStats().getBaseAttack(), 9, p.getStats().getBaseDefense()
                , 9, p.getStats().getBaseStamina(), 9));
        assertEquals(3355, formulas.calculateCp("40", p.getStats().getBaseAttack(), 15, p.getStats().getBaseDefense()
                , 15, p.getStats().getBaseStamina(), 15));
    }

    @Test
    public void testAttack() {
        assertEquals(139.52287423610696,formulas.getCurrentAttack(186, 15, 0.6941436529159550), 1E-9);
    }
    @Test
    public void testDefense() {
        assertEquals(119.54499283593915,formulas.getCurrentDefense(180, 13, 0.6194041079582340), 1E-9);
    }
    
   
    @Test
    public void testDamageOfMove() {
        Move move = Move.newBuilder().setMoveId(PokemonMove.WATER_PULSE).setType(PokemonType.POKEMON_TYPE_WATER).setPower(45).build();
        Pokemon attacker = Pokemon.newBuilder().setType(PokemonType.POKEMON_TYPE_WATER).build();
        Pokemon defender = Pokemon.newBuilder().setType(PokemonType.POKEMON_TYPE_NORMAL).build();
        assertEquals(33,formulas.damageOfMove(139.522944, 119.545, move, attacker , defender, 1.0f, true));
        assertEquals(33,formulas.damageOfMove(139.52287423610696, 119.54499283593915, move, attacker , defender, 1.0f, true));
        
        // Cloyster vs. Dragonite
        move = Move.newBuilder().setMoveId(PokemonMove.DRAGON_PULSE).setType(PokemonType.POKEMON_TYPE_DRAGON).setPower(65).build();
        attacker = Pokemon.newBuilder().setType(PokemonType.POKEMON_TYPE_DRAGON).setType2(PokemonType.POKEMON_TYPE_FLYING).build();
        defender = Pokemon.newBuilder().setType(PokemonType.POKEMON_TYPE_ICE).setType2(PokemonType.POKEMON_TYPE_WATER).build();
        double attack = formulas.getCurrentAttack(263, 15, 0.731700003147125);
        double defense = formulas.getCurrentDefense(323, 15, 0.731700003147125);
        assertEquals(1.25 * 1.0 * 1.0, formulas.calculateModifier(move, attacker, defender), 1E-9);
        assertEquals(34,formulas.damageOfMove(attack, defense, move, attacker , defender, 1.0f, true));
        
        
    }
    

}
