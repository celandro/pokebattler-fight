package com.pokebattler.fight.data;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.MiniPokemonData;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.StatsAttributesOuterClass.StatsAttributes;

@Service
public class PokemonDataCreator {
    @Resource
    CpMRepository cpmRepository;
    @Resource
    PokemonRepository pokemonRepository;
    @Resource
    Formulas f;
    private final Logger log = LoggerFactory.getLogger(getClass());

    Map<PokemonId, TreeMap<Integer, MiniPokemonData>> cpLookupMap;

    public PokemonDataCreator() {
        
    }
    public PokemonDataCreator( CpMRepository cpmRepository, PokemonRepository pokemonRepository, Formulas f) {
        this.cpmRepository = cpmRepository;
        this.pokemonRepository = pokemonRepository;
        this.f = f;
    }
    
    // High memory version, needs about 1.5g heap and has some serialization
    // cost but not too much
    // perhaps write these out to files and can serve them statically?
    // Map<PokemonId, TreeMap<Integer,byte[]>> cpLookupMultiMap;

    // @PostConstruct
    // public void initCPLookupMap2() {
    // log.info("Building CP lookup map");
    // cpLookupMultiMap = new EnumMap<>(PokemonId.class);
    // for (Pokemon p : pokemonRepository.getAll().getPokemonList()) {
    // ArrayListMultimap<Integer, MiniPokemonData> pokemonMap =
    // ArrayListMultimap
    // .<Integer, MiniPokemonData> create();
    // // find the lowest level with the highest stat for each cp
    // for (int i = Formulas.MIN_LEVEL; i <= Formulas.MAX_LEVEL; i++) {
    // for (int half = 0; half <= 1; half++) {
    // if (i == Formulas.MAX_LEVEL && half == 1) {
    // // skip
    // } else {
    // String level = half == 1 ? Integer.toString(i) + ".5" :
    // Integer.toString(i);
    // for (int attack = Formulas.MAX_INDIVDIUAL_STAT; attack >=
    // Formulas.MIN_INDIVDIUAL_STAT; attack--) {
    // for (int defense = Formulas.MAX_INDIVDIUAL_STAT; defense >=
    // Formulas.MIN_INDIVDIUAL_STAT; defense--) {
    // for (int stam = Formulas.MAX_INDIVDIUAL_STAT; stam >=
    // Formulas.MIN_INDIVDIUAL_STAT; stam--) {
    // int cp = f.calculateCp(level, p.getStats().getBaseAttack(), attack,
    // p.getStats().getBaseDefense(), defense, p.getStats().getBaseStamina(),
    // stam);
    // pokemonMap.put(cp, MiniPokemonData.newBuilder().setLevel(level)
    // .setAttack(attack).setDefense(defense).setStamina(stam).build());
    // }
    //
    // }
    //
    // }
    // }
    // }
    // }
    // TreeMap<Integer,byte[]> protoMap = new TreeMap<>();
    // for (Entry<Integer, Collection<MiniPokemonData>>
    // entry:pokemonMap.asMap().entrySet()) {
    // MiniPokemonDatas data =
    // MiniPokemonDatas.newBuilder().addAllPossible(entry.getValue()).build();
    // protoMap.put(entry.getKey(), data.toByteArray());
    // }
    //
    // cpLookupMultiMap.put(p.getPokemonId(), protoMap);
    // log.info("Finished {} {}", p.getPokemonId().getNumber(),
    // p.getPokemonId());
    // }
    // log.info("Finished Loading CP lookup map for {} pokemons",
    // cpLookupMultiMap.size());
    // }

    @PostConstruct
    public void initCPLookupMap() {
        log.info("Building CP lookup map2");
        cpLookupMap = new EnumMap<>(PokemonId.class);
        for (final Pokemon p : pokemonRepository.getAll().getPokemonList()) {
            final TreeMap<Integer, MiniPokemonData> pokemonMap = new TreeMap<>();
            for (int i = Formulas.MIN_LEVEL; i <= Formulas.MAX_LEVEL; i++) {
                for (int half = 0; half <= 1; half++) {
                    if (i == Formulas.MAX_LEVEL && half == 1) {
                        // skip
                    } else {
                        final String level = half == 1 ? Integer.toString(i) + ".5" : Integer.toString(i);
                        for (int attack = Formulas.MAX_INDIVDIUAL_STAT; attack >= Formulas.MIN_INDIVDIUAL_STAT; attack--) {
                            for (int defense = Formulas.MAX_INDIVDIUAL_STAT; defense >= Formulas.MIN_INDIVDIUAL_STAT; defense--) {
                                for (int stam = Formulas.MAX_INDIVDIUAL_STAT; stam >= Formulas.MIN_INDIVDIUAL_STAT; stam--) {
                                    final int cp = f.calculateCp(level, p.getStats().getBaseAttack(), attack,
                                            p.getStats().getBaseDefense(), defense, p.getStats().getBaseStamina(),
                                            stam);
                                    pokemonMap.putIfAbsent(cp, MiniPokemonData.newBuilder().setLevel(level)
                                            .setAttack(attack).setDefense(defense).setStamina(stam).build());
                                }

                            }

                        }
                    }
                }
            }
            cpLookupMap.put(p.getPokemonId(), pokemonMap);
            log.debug("Finished {} {}, highest cp {}", p.getPokemonId().getNumber(), p.getPokemonId(),
                    pokemonMap.lastKey());
        }
        log.info("Finished Loading CP lookup map for {} pokemons", cpLookupMap.size());
    }

    public PokemonData createMaxStatPokemon(PokemonId id, String level, PokemonMove move1, PokemonMove move2) {
        return createPokemon(id, level, Formulas.MAX_INDIVDIUAL_STAT, Formulas.MAX_INDIVDIUAL_STAT,
                Formulas.MAX_INDIVDIUAL_STAT, move1, move2);
    }

    public PokemonData createPokemon(PokemonId id, String level, int individualAttack, int individualDefense,
            int individualStamina, PokemonMove move1, PokemonMove move2) {
        final double cpm = cpmRepository.getCpM(level).getCpm();
        final Pokemon p = pokemonRepository.getById(id);
        PokemonData.Builder retval =PokemonData.newBuilder().setLevel(level).setPokemonId(id).setIndividualAttack(individualAttack)
                .setIndividualDefense(individualDefense).setIndividualStamina(individualStamina).setCpMultiplier(cpm)
                .setMove1(move1).setMove2(move2);
        retval.setCp(f.calculateCp(retval,p));
        return retval.build();
    }

    public PokemonData createPokemon(PokemonId id, int cp, PokemonMove move1, PokemonMove move2) {
        final TreeMap<Integer, MiniPokemonData> pokemonMap = cpLookupMap.get(id);
        if (cp < pokemonMap.firstKey()) {
            cp = pokemonMap.firstKey();
        }
        final Entry<Integer, MiniPokemonData> cpEntry = pokemonMap.floorEntry(cp);
        final MiniPokemonData mpd = cpEntry.getValue();
        // need more memory!
        // Collection<MiniPokemonData> possiblePokemon =
        // cpLookupMap.get(id).get(cp);
        // if (possiblePokemon.isEmpty()) {
        // throw new IllegalArgumentException("Impossible CP for pokemon " +
        // id);
        // }
        // MiniPokemonData mpd = possiblePokemon.iterator().next();

        final double cpm = cpmRepository.getCpM(mpd.getLevel()).getCpm();
        return PokemonData.newBuilder().setLevel(mpd.getLevel()).setCp(cpEntry.getKey()).setPokemonId(id)
                .setIndividualAttack(mpd.getAttack()).setIndividualDefense(mpd.getDefense())
                .setIndividualStamina(mpd.getStamina()).setCpMultiplier(cpm).setMove1(move1).setMove2(move2).build();
    }

    public PokemonData transform(PokemonData attacker, PokemonData defender) {
        Pokemon p = pokemonRepository.getById(defender.getPokemonId());
        return attacker.toBuilder().setMove1(defender.getMove1()).setMove2(defender.getMove2())
                .setCp( f.calculateCp(attacker,p)).build();
    }

}
