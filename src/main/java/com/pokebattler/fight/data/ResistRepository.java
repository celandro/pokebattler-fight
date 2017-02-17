package com.pokebattler.fight.data;

import java.io.InputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.pokebattler.fight.data.proto.PokemonTypeOuterClass.PokemonType;

@Repository
public class ResistRepository {
    final Map<PokemonType, Map<PokemonType, Float>> resistMap;
    Logger log = LoggerFactory.getLogger(getClass());

    public ResistRepository() {
        try (InputStream is = this.getClass().getResourceAsStream("resists.csv");
                Scanner lineScanner = new Scanner(is)) {
            final Map<PokemonType, Map<PokemonType, Float>> tempMap = new EnumMap<>(PokemonType.class);
            // skip line 1
            lineScanner.next();
            for (int i = 0; lineScanner.hasNext(); i++) {
                final String s = lineScanner.next();
                try (Scanner r = new Scanner(s)) {
                    final Scanner rowScanner = r.useDelimiter(",");
                    final PokemonType attacker = PokemonType.forNumber(i);
                    final Map<PokemonType, Float> defenseMap = new EnumMap<>(PokemonType.class);
                    // Ignore Unrecognized
                    // skip column 1
                    rowScanner.next();
                    for (int j = 0; j < PokemonType.values().length - 1; j++) {
                        final PokemonType defender = PokemonType.forNumber(j);
                        if (!rowScanner.hasNextFloat()) {
                            log.error("could not read float at pos{}!", j);
                        }
                        defenseMap.put(defender, rowScanner.nextFloat());
                    }
                    tempMap.put(attacker, defenseMap);
                }
            }
            resistMap = tempMap;
        } catch (final Exception e) {
            log.error("Could not initialize resists", e);
            throw new IllegalArgumentException("resists.csv is not valid");
        }
        log.info("Loaded {} resist types", resistMap.size());
    }

    /**
     * @param move
     * @param defender
     * @return
     */
    public Float getResist(PokemonType move, PokemonType defender) {
        return resistMap.get(move).get(defender);
    }

    public Map<PokemonType, Float> getResists(PokemonType move) {
        return resistMap.get(move);
    }

    public Map<PokemonType, Map<PokemonType, Float>> getAll() {
        return resistMap;
    }

}
