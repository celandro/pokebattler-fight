package com.pokebattler.fight.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemons;
import com.pokebattler.fight.data.raw.RawData;

@Repository
public class CombinedPokemonRepository implements PokemonRepository {
	private final List<PokemonRepository> repositories;
	Logger log = LoggerFactory.getLogger(getClass());
    final Pokemons all;
    final Map<PokemonId, Pokemon> pokemonMap;

	public CombinedPokemonRepository()  throws Exception {
		this("pokemongo.json", "20170216_pokemongo.json");

	}

	public CombinedPokemonRepository(String... files) throws Exception {
		final List<Exception> exceptions = new ArrayList<>();
		repositories = Arrays.asList(files).stream().map(file -> {
			try {
				return new IndividualPokemonRepository(file);
			} catch (Exception e) {
				exceptions.add(e);
				return null;
			}
		}).collect(Collectors.toList());
		if (exceptions.size() > 0) {
			throw exceptions.get(0);
		}
		log.info("Combining pokemon");
		Pokemons.Builder b = Pokemons.newBuilder();
		Map<PokemonId, Pokemon.Builder> pMap = new LinkedHashMap<>();
		repositories.stream().forEach(repository -> {
			repository.getAll().getPokemonList().stream().forEach(pokemon -> {
				Pokemon.Builder p = pMap.get(pokemon.getPokemonId());
				if (p == null) {
					pMap.put(pokemon.getPokemonId(), pokemon.toBuilder());
				} else {
					// this is so ugly
					pokemon.getMovesetsList().stream().filter(legacyMoveset -> {
						return p.getMovesetsOrBuilderList().stream().map(moveset -> {
							if (legacyMoveset.getCinematicMove() == moveset.getCinematicMove()
									&& legacyMoveset.getQuickMove() == moveset.getQuickMove()) {
								return false;
							}
							return true;
						}).reduce(true, (doFilter, val) -> doFilter && val );
					}).forEach(moveset -> {
						p.addMovesets(moveset);
					});
					
					pokemon.getQuickMovesList().stream().filter(legacyMove -> {
						return !p.getQuickMovesList().contains(legacyMove);
					}).forEach(move -> {
						p.addQuickMoves(move);
					});
					pokemon.getCinematicMovesList().stream().filter(legacyMove -> {
						return !p.getCinematicMovesList().contains(legacyMove);
					}).forEach(move -> {
						p.addCinematicMoves(move);
					});
					
					
				}
			});
		});
		b.addAllPokemon(pMap.values().stream().map(p -> p.build()).collect(Collectors.toList()));
		all = b.build();
        pokemonMap = all.getPokemonList().stream().collect(Collectors.toMap(p -> p.getPokemonId(), p -> p));
        log.info("Loaded {} pokemons", all.getPokemonCount());
		
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
