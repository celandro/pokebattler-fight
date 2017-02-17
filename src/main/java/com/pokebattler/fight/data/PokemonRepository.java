package com.pokebattler.fight.data;

import java.io.InputStream;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.EnumSetDeserializer;
import com.google.protobuf.util.JsonFormat;
import com.pokebattler.fight.data.proto.PokemonFamilyIdOuterClass.PokemonFamilyId;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemons;
import com.pokebattler.fight.data.proto.PokemonTypeOuterClass.PokemonType;
import com.pokebattler.fight.data.proto.StatsAttributesOuterClass.StatsAttributes;
import com.pokebattler.fight.data.raw.RawData;
import static com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId.*;

@Repository
public class PokemonRepository {
    Logger log = LoggerFactory.getLogger(getClass());
    final Pokemons all;
    final Map<PokemonId, Pokemon> pokemonMap;
    ObjectMapper mapper;
    JsonFormat.Printer printer;
    EnumSet<PokemonId> endGamePokemons = EnumSet.copyOf(Arrays.asList(VENUSAUR, CHARIZARD, BLASTOISE, BUTTERFREE,
            BEEDRILL, PIDGEOT, RATICATE, FEAROW, ARBOK, RAICHU, SANDSLASH, NIDOQUEEN, NIDOKING, CLEFABLE, NINETALES,
            WIGGLYTUFF, VILEPLUME, PARASECT, VENOMOTH, DUGTRIO, PERSIAN, GOLDUCK, PRIMEAPE, ARCANINE, POLIWRATH,
            ALAKAZAM, MACHAMP, VICTREEBEL, TENTACRUEL, GOLEM, RAPIDASH, SLOWBRO, MAGNETON, FARFETCHD, DODRIO, DEWGONG,
            MUK, CLOYSTER, GENGAR, HYPNO, KINGLER, ELECTRODE, EXEGGUTOR, MAROWAK, HITMONLEE, HITMONCHAN,
            LICKITUNG, WEEZING, RHYDON, BLISSEY, TANGELA, KANGASKHAN, SEAKING, STARMIE, MR_MIME, JYNX,
            ELECTABUZZ, MAGMAR, PINSIR, TAUROS, GYARADOS, LAPRAS, VAPOREON, JOLTEON, FLAREON, OMASTAR,
            KABUTOPS, AERODACTYL, SNORLAX, DRAGONITE, DITTO, MEGANIUM, TYPHLOSION, FERALIGATR, FURRET, NOCTOWL, 
            LEDIAN, ARIADOS, CROBAT, LANTURN, XATU, AMPHAROS, BELLOSSOM, AZUMARILL, SUDOWOODO, POLITOED, JUMPLUFF,
            AIPOM, SUNFLORA, YANMA, QUAGSIRE, ESPEON, UMBREON, MURKROW, SLOWKING, MISDREAVUS,  UNOWN, WOBBUFFET, 
            GIRAFARIG, FORRETRESS, DUNSPARCE, GLIGAR, STEELIX, GRANBULL, QWILFISH, SCIZOR, SHUCKLE, HERACROSS, SNEASEL, 
            URSARING,  MAGCARGO, PILOSWINE, CORSOLA, OCTILLERY, DELIBIRD, MANTINE, SKARMORY, HOUNDOOM, KINGDRA,
            DONPHAN, PORYGON2, STANTLER, SMEARGLE, TYROGUE, HITMONTOP, TYRANITAR));

    // could be different to speed things up
    EnumSet<PokemonId> endGameDefenderPokemons = endGamePokemons;
    
    
    public PokemonRepository() throws Exception {
    	this("pokemongo.json");
    }
    public PokemonRepository(String file) throws Exception {
        final InputStream is = this.getClass().getResourceAsStream(file);
        if (is == null) {
            throw new IllegalArgumentException("Can not find pokemongo.json");
        }
        mapper = new ObjectMapper();
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        printer = JsonFormat.printer().includingDefaultValueFields();
        final RawData rawData = mapper.readValue(is, RawData.class);
        all = createPokemons(rawData);
        pokemonMap = all.getPokemonList().stream().collect(Collectors.toMap(p -> p.getPokemonId(), p -> p));
        log.info("Loaded {} pokemons", all.getPokemonCount());
    }

    public Pokemons createPokemons(RawData rawData) {
        final Pokemons.Builder allBuilder = Pokemons.newBuilder();
        final List<Pokemon> pokes = rawData.items.stream().filter(item -> (item != null && item.getPokemon() != null))
                .map(item -> item.getPokemon()).map(pokemon -> {
                    try {
                        final PokemonId id = pokemon.getPokemonId();
                        PokemonFamilyId familyId = pokemon.getFamilyId();
                        final PokemonId parentId = pokemon.getParentId();

                        final Pokemon.Builder b = Pokemon.newBuilder().setPokemonId(id)
                                .setType(pokemon.getType())
                                .setStats(StatsAttributes.newBuilder().setBaseAttack(pokemon.getStats().getBaseAttack())
                                        .setBaseDefense(pokemon.getStats().getBaseDefense())
                                        .setBaseStamina(pokemon.getStats().getBaseStamina()).build())
                                .setPokedexHeightM(pokemon.getPokedexHeightM())
                                .setPokedexWeightKg(pokemon.getPokedexWeightKg())
                                .setHeightStdDev(pokemon.getHeightStdDev()).setWeightStdDev(pokemon.getWeightStdDev())
                                .setCandyToEvolve(pokemon.getCandyToEvolve());
                        if (pokemon.getType2() != null) {
                            b.setType2(pokemon.getType2());
                        }
                        if (parentId != null) {
                            b.setParentPokemonId(parentId);
                        }
                        if (familyId != null) {
                            b.setFamilyId(familyId);
                        }
                        b.addAllQuickMoves(pokemon.getQuickMoves());
                        b.addAllCinematicMoves(pokemon.getCinematicMoves());

//                        addQuickMoves(pokemon.getQuickMoves(), b);
//                        addCinematicMoves(pokemon.getCinematicMoves(), b);

                        return b.build();
                    } catch (final RuntimeException e) {
                        try {
                            log.info("Could not handle {}",
                                    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pokemon));
                        } catch (final JsonProcessingException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        throw e;
                    }

                }).collect(Collectors.toList());
        allBuilder.addAllPokemon(pokes);
        return allBuilder.build();
    }

    void addCinematicMoves(String cinMoves, final Pokemon.Builder b) {
        OctalParser.parseRepeatedInt32(cinMoves).stream()
                .map(num -> PokemonMove.forNumber(num)).forEach(move -> b.addCinematicMoves(move));
    }

    void addQuickMoves(String quickMoves, final Pokemon.Builder b) {
        OctalParser.parseRepeatedInt32(quickMoves).stream()
                .map(num -> PokemonMove.forNumber(num)).forEach(move -> b.addQuickMoves(move));
    }

    public Map<String, String> getIdToNameMap() {
        // return sorted
        return EnumSet.allOf(PokemonId.class).stream().filter(key -> pokemonMap.containsKey(key))
                .collect(Collectors.toMap(e -> Integer.toString(e.getNumber()), e -> e.toString(), String::concat,
                        () -> new LinkedHashMap<>()));
    }

    public Map<String, Integer> getNameToIdMap() {
        return pokemonMap.keySet().stream().collect(Collectors.toMap(e -> e.toString(),
                e -> Integer.valueOf(e.getNumber()), Integer::sum, () -> new TreeMap<>()));
    }

    public Pokemons getAll() {
        return all;
    }

    public Pokemons getAllEndGame() {
        Pokemons.Builder builder = all.toBuilder().clearPokemon();
        all.getPokemonList().stream().filter(pokemon -> endGamePokemons.contains(pokemon.getPokemonId()))
        .forEach(pokemon -> builder.addPokemon(pokemon));
        return builder.build();
    }
    public Pokemons getAllEndGameDefender() {
        Pokemons.Builder builder = all.toBuilder().clearPokemon();
        all.getPokemonList().stream().filter(pokemon -> endGameDefenderPokemons.contains(pokemon.getPokemonId()))
        .forEach(pokemon -> builder.addPokemon(pokemon));
        return builder.build();
    }

    public Pokemon getByName(String name) {
        try {
            final PokemonId id = PokemonId.valueOf(name.toUpperCase());
            return getById(id);
        } catch (final IllegalArgumentException e) {
            log.warn("Could not find {}", name);
            return null;
        }
    }

    public Pokemon getByNumber(int number) {
        try {
            final PokemonId id = PokemonId.forNumber(number);
            return getById(id);
        } catch (final IllegalArgumentException e) {
            log.warn("Could not find {}", number);
            return null;
        }
    }

    public Pokemon getById(PokemonId id) {
        return pokemonMap.get(id);
    }

    public Pokemon transform(Pokemon a, Pokemon d) {
        //TODO: HACK HP is calculated pre transform but CP uses defender base stamina
        Pokemon.Builder clone = a.toBuilder();
        clone.setType(d.getType()).setType2(d.getType2())
            .getStatsBuilder()
                .setBaseAttack(d.getStats().getBaseAttack())
                .setBaseDefense(d.getStats().getBaseDefense());
        
        return clone.build();    
    }

}
