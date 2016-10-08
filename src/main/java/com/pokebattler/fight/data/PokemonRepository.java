package com.pokebattler.fight.data;

import java.io.InputStream;
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
import com.google.protobuf.util.JsonFormat;
import com.pokebattler.fight.data.proto.PokemonFamilyIdOuterClass.PokemonFamilyId;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemons;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.proto.PokemonTypeOuterClass.PokemonType;
import com.pokebattler.fight.data.proto.StatsAttributesOuterClass.StatsAttributes;
import com.pokebattler.fight.data.raw.RawData;

@Repository
public class PokemonRepository {
    Logger log = LoggerFactory.getLogger(getClass());
    final Pokemons all;
    final Map<PokemonId, Pokemon> pokemonMap;
    ObjectMapper mapper;
    JsonFormat.Printer printer;

    public PokemonRepository() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("pokemongo.json");
        if (is == null) {
            throw new IllegalArgumentException("Can not find pokemongo.json");
        }
        mapper = new ObjectMapper();
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        printer = JsonFormat.printer().includingDefaultValueFields();
        RawData rawData = mapper.readValue(is, RawData.class);
        all = createPokemons(rawData);
        pokemonMap = all.getPokemonList().stream().collect(Collectors.toMap(p -> p.getPokemonId(), p -> p));
        log.info("Loaded {} pokemons", all.getPokemonCount());
    }

    public Pokemons createPokemons(RawData rawData) {
        final Pokemons.Builder allBuilder = Pokemons.newBuilder();
        List<Pokemon> pokes = rawData.items.stream().filter(item -> (item != null && item.getPokemon() != null))
                .map(item -> item.getPokemon()).map(pokemon -> {
                    try {
                        PokemonId id = PokemonId.forNumber(Integer.parseInt(pokemon.getUniqueId().substring(1, 5)));
                        PokemonFamilyId familyId = null;
                        if (pokemon.getFamilyId() != null) {
                            try {
                                familyId = PokemonFamilyId.forNumber(Integer.parseInt(pokemon.getFamilyId()));
                            } catch (NumberFormatException e) {
                                // this one is strangely formatted in the file
                                // with 2 formats..
                                familyId = PokemonFamilyId
                                        .forNumber(Integer.parseInt(pokemon.getFamilyId().substring(1, 5)));
                            }
                        }
                        PokemonId parentId = pokemon.getParentId() == null ? null
                                : PokemonId.forNumber(Integer.parseInt(pokemon.getParentId().substring(1, 5)));

                        Pokemon.Builder b = Pokemon.newBuilder().setPokemonId(id)
                                .setType(PokemonType.valueOf(pokemon.getType1()))
                                .setStats(StatsAttributes.newBuilder().setBaseAttack(pokemon.getStats().getBaseAttack())
                                        .setBaseDefense(pokemon.getStats().getBaseDefense())
                                        .setBaseStamina(pokemon.getStats().getBaseStamina()).build())
                                .setPokedexHeightM(pokemon.getPokedexHeightM())
                                .setPokedexWeightKg(pokemon.getPokedexWeightKg())
                                .setHeightStdDev(pokemon.getHeightStdDev()).setWeightStdDev(pokemon.getWeightStdDev())
                                .setCandyToEvolve(pokemon.getCandyToEvolve());
                        if (pokemon.getType2() != null) {
                            b.setType2(PokemonType.valueOf(pokemon.getType2()));
                        }
                        if (parentId != null) {
                            b.setParentPokemonId(parentId);
                        }
                        if (familyId != null) {
                            b.setFamilyId(familyId);
                        }
                        OctalParser.parseRepeatedInt32(pokemon.getQuickMoves()).stream()
                                .map(num -> PokemonMove.forNumber(num)).forEach(move -> b.addQuickMoves(move));
                        OctalParser.parseRepeatedInt32(pokemon.getCinematicMoves()).stream()
                                .map(num -> PokemonMove.forNumber(num)).forEach(move -> b.addCinematicMoves(move));

                        return b.build();
                    } catch (RuntimeException e) {
                        try {
                            log.info("Could not handle {}",
                                    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pokemon));
                        } catch (JsonProcessingException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        throw e;
                    }

                }).collect(Collectors.toList());
        allBuilder.addAllPokemon(pokes);
        return allBuilder.build();
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

    public Pokemon getByName(String name) {
        try {
            PokemonId id = PokemonId.valueOf(name.toUpperCase());
            return getById(id);
        } catch (IllegalArgumentException e) {
            log.warn("Could not find {}", name);
            return null;
        }
    }

    public Pokemon getByNumber(int number) {
        try {
            PokemonId id = PokemonId.forNumber(number);
            return getById(id);
        } catch (IllegalArgumentException e) {
            log.warn("Could not find {}", number);
            return null;
        }
    }

    public Pokemon getById(PokemonId id) {
        return pokemonMap.get(id);
    }

}
