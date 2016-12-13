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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.util.JsonFormat;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.MoveOuterClass.Moves;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.raw.RawData;

@Repository
public class MoveRepository {
    Logger log = LoggerFactory.getLogger(getClass());
    ObjectMapper mapper;
    final Moves all;
    final Map<PokemonMove, Move> moveMap;
    public final static Move DODGE_MOVE = Move.newBuilder().setMoveId(PokemonMove.DODGE).setAccuracyChance(0)
            .setDurationMs(500).build();

    public MoveRepository() throws Exception {
        final InputStream is = this.getClass().getResourceAsStream("pokemongo.json");
        if (is == null) {
            throw new IllegalArgumentException("Can not find pokemongo.json");
        }
        mapper = new ObjectMapper();
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.setSerializationInclusion(Include.NON_NULL);
        final RawData rawData = mapper.readValue(is, RawData.class);
        all = createMoves(rawData);
        moveMap = all.getMoveList().stream().collect(Collectors.toMap(p -> p.getMoveId(), p -> p));
        log.info("Loaded {} moves", all.getMoveCount());
    }

    public Moves createMoves(RawData rawData) {
        final Moves.Builder allBuilder = Moves.newBuilder();
        final JsonFormat.Parser parser = JsonFormat.parser();
        final List<Move> moves = rawData.items.stream().filter(item -> (item != null && item.getMove() != null))
                .map(item -> item.getMove()).map(move -> {
                    final Move.Builder builder = Move.newBuilder();
                    String moveString = null;
                    try {
                        // different names
                        builder.setMoveId(move.getMovementId());
                        move.setMovementId(null);
                        builder.setType(move.getPokemonType());
                        move.setPokemonType(null);
                        moveString = mapper.writeValueAsString(move);
                        parser.merge(moveString, builder);
                    } catch (final Exception e) {
                        throw new IllegalArgumentException("Could not parse " + move.getMovementId() + ":" + moveString,
                                e);
                    }
                    return builder.build();
                }).collect(Collectors.toList());
        allBuilder.addAllMove(moves);
        allBuilder.addMove(DODGE_MOVE);
        return allBuilder.build();

    }

    public Moves getAll() {
        return all;
    }

    public Map<String, String> getIdToNameMap() {
        // return sorted
        return EnumSet.allOf(PokemonMove.class).stream().filter(key -> moveMap.containsKey(key))
                .collect(Collectors.toMap(e -> Integer.toString(e.getNumber()), e -> e.toString(), String::concat,
                        () -> new LinkedHashMap<>()));
    }

    public Map<String, Integer> getNameToIdMap() {
        return moveMap.keySet().stream().collect(Collectors.toMap(e -> e.toString(),
                e -> Integer.valueOf(e.getNumber()), Integer::sum, () -> new TreeMap<>()));
    }

    public Move getByName(String name) {
        try {
            final PokemonMove id = PokemonMove.valueOf(name.toUpperCase());
            return getById(id);
        } catch (final IllegalArgumentException e) {
            log.warn("Could not find {}", name);
            return null;
        }
    }

    public Move getByNumber(int number) {
        try {
            final PokemonMove id = PokemonMove.forNumber(number);
            return getById(id);
        } catch (final IllegalArgumentException e) {
            log.warn("Could not find {}", number);
            return null;
        }
    }

    public Move getById(PokemonMove id) {
        return moveMap.get(id);
    }

}
