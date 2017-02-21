package com.pokebattler.fight.data;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
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
import com.pokebattler.fight.data.proto.PokemonOuterClass.PokemonMoveset;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemons;
import com.pokebattler.fight.data.proto.PokemonTypeOuterClass.PokemonType;
import com.pokebattler.fight.data.proto.StatsAttributesOuterClass.StatsAttributes;
import com.pokebattler.fight.data.raw.RawData;
import static com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId.*;

public class IndividualPokemonRepository implements PokemonRepository {
    private static final String POKEMONGO_JSON = "pokemongo.json";
	Logger log = LoggerFactory.getLogger(getClass());
    final Pokemons all;
    final Map<PokemonId, Pokemon> pokemonMap;
    ObjectMapper mapper;
    JsonFormat.Printer printer;
    //ARTICUNO, MOLTRES, ZAPDOS, MEW, MEWTWO, RAIKOU, ENTEI, SUICUNE, LUGIA, HO-OH, CELEBI, SMEARGLE

    // could be different to speed things up
    
    
    public IndividualPokemonRepository() throws Exception {
    	this(POKEMONGO_JSON);
    }
    public IndividualPokemonRepository(String file) throws Exception {
    	String legacy = null;
    	if (!file.equals(POKEMONGO_JSON)) {
    		legacy = file.substring(0, 8);
    	}
        final InputStream is = this.getClass().getResourceAsStream(file);
        if (is == null) {
            throw new IllegalArgumentException("Can not find " + file);
        }
        mapper = new ObjectMapper();
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        printer = JsonFormat.printer().includingDefaultValueFields();
        final RawData rawData = mapper.readValue(is, RawData.class);
        all = createPokemons(rawData, legacy);
        pokemonMap = all.getPokemonList().stream().collect(Collectors.toMap(p -> p.getPokemonId(), p -> p));
        log.info("Loaded {} pokemons", all.getPokemonCount());
    }

	Pokemons createPokemons(RawData rawData, String legacy) {
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
                        pokemon.getQuickMoves().stream().forEach(quickMove -> {
                        	pokemon.getCinematicMoves().stream().forEach(cinematicMove -> {
                        		PokemonMoveset.Builder builder = PokemonMoveset.newBuilder()
                        				.setQuickMove(quickMove).setCinematicMove(cinematicMove);
                        		if (legacy != null) {
                        			builder.setLegacyDate(legacy);
                        		}
                        		b.addMovesets(builder);
                        	});
                        });

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

    @Override
    public Map<PokemonId, Pokemon> getPokemonMap() {
    	return pokemonMap;
    }
    @Override
	public Pokemons getAll() {
		return all;
	}
    
}
