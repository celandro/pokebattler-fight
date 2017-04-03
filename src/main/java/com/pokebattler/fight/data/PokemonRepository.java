package com.pokebattler.fight.data;

import static com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemons;
import com.pokebattler.fight.data.raw.RawData;

public interface PokemonRepository {
	public static final EnumSet<PokemonId> END_GAME_POKEMONS = EnumSet.copyOf(Arrays.asList(VENUSAUR, CHARIZARD,
			BLASTOISE, BUTTERFREE, BEEDRILL, PIDGEOT, RATICATE, FEAROW, ARBOK, RAICHU, SANDSLASH, NIDOQUEEN, NIDOKING,
			CLEFABLE, NINETALES, WIGGLYTUFF, VILEPLUME, PARASECT, VENOMOTH, DUGTRIO, PERSIAN, GOLDUCK, PRIMEAPE,
			ARCANINE, POLIWRATH, ALAKAZAM, MACHAMP, VICTREEBEL, TENTACRUEL, GOLEM, RAPIDASH, SLOWBRO, MAGNETON,
			FARFETCHD, DODRIO, DEWGONG, MUK, CLOYSTER, GENGAR, HYPNO, KINGLER, ELECTRODE, EXEGGUTOR, MAROWAK, HITMONLEE,
			HITMONCHAN, LICKITUNG, WEEZING, RHYDON, BLISSEY, TANGELA, KANGASKHAN, SEAKING, STARMIE, MR_MIME, JYNX,
			ELECTABUZZ, MAGMAR, PINSIR, TAUROS, GYARADOS, LAPRAS, VAPOREON, JOLTEON, FLAREON, OMASTAR, KABUTOPS,
			AERODACTYL, SNORLAX, DRAGONITE, DITTO, MEGANIUM, TYPHLOSION, FERALIGATR, FURRET, NOCTOWL, LEDIAN, ARIADOS,
			CROBAT, LANTURN, TOGETIC, XATU, AMPHAROS, BELLOSSOM, AZUMARILL, SUDOWOODO, POLITOED, JUMPLUFF, AIPOM,
			SUNFLORA, YANMA, QUAGSIRE, ESPEON, UMBREON, MURKROW, SLOWKING, MISDREAVUS, UNOWN, WOBBUFFET, GIRAFARIG,
			FORRETRESS, DUNSPARCE, GLIGAR, STEELIX, GRANBULL, QWILFISH, SCIZOR, SHUCKLE, HERACROSS, SNEASEL, URSARING,
			MAGCARGO, PILOSWINE, CORSOLA, OCTILLERY, DELIBIRD, MANTINE, SKARMORY, HOUNDOOM, KINGDRA, DONPHAN, PORYGON2,
			STANTLER, TYROGUE, HITMONTOP, MILTANK, TYRANITAR));
	//
	// top 30 CP
	public static final EnumSet<PokemonId> END_GAME_DEFENDER_POKEMONS = EnumSet.copyOf(Arrays.asList(TYRANITAR,
			DRAGONITE, SNORLAX, RHYDON, GYARADOS, BLISSEY, VAPOREON, DONPHAN, ESPEON, HERACROSS, GOLEM, EXEGGUTOR,
			FLAREON, MACHAMP, ALAKAZAM, ARCANINE, SCIZOR, PINSIR, URSARING, JOLTEON, FERALIGATR, MUK, AMPHAROS, KINGLER,
			CHARIZARD, TYPHLOSION, OMASTAR, GENGAR, AERODACTYL, LAPRAS));

	public static final EnumSet<PokemonId> UNRELEASED_POKEMONS = EnumSet.copyOf(Arrays.asList(ARTICUNO, MOLTRES, ZAPDOS,
			MEW, MEWTWO, RAIKOU, ENTEI, SUICUNE, LUGIA, HO_OH, CELEBI, SMEARGLE));

	Map<PokemonId, Pokemon> getPokemonMap();

	Pokemons getAll();

	default Map<String, String> getIdToNameMap() {
		// return sorted
		return EnumSet.allOf(PokemonId.class).stream().filter(key -> getPokemonMap().containsKey(key))
				.collect(Collectors.toMap(e -> Integer.toString(e.getNumber()), e -> e.toString(), String::concat,
						() -> new LinkedHashMap<>()));
	}

	default Map<String, Integer> getNameToIdMap() {
		return getPokemonMap().keySet().stream().collect(Collectors.toMap(e -> e.toString(),
				e -> Integer.valueOf(e.getNumber()), Integer::sum, () -> new TreeMap<>()));
	}

	default Pokemons getAllEndGame() {
		Pokemons.Builder builder = getAll().toBuilder().clearPokemon();
		getAll().getPokemonList().stream().filter(pokemon -> END_GAME_POKEMONS.contains(pokemon.getPokemonId()))
				.forEach(pokemon -> builder.addPokemon(pokemon));
		return builder.build();
	}

	default Pokemons getAllEndGameDefender() {
		Pokemons.Builder builder = getAll().toBuilder().clearPokemon();
		getAll().getPokemonList().stream()
				.filter(pokemon -> END_GAME_DEFENDER_POKEMONS.contains(pokemon.getPokemonId()))
				.forEach(pokemon -> builder.addPokemon(pokemon));
		return builder.build();
	}

	default Pokemon getByName(String name) {
		try {
			final PokemonId id = PokemonId.valueOf(name.toUpperCase());
			return getById(id);
		} catch (final IllegalArgumentException e) {
			return null;
		}
	}

	default Pokemon getByNumber(int number) {
		try {
			final PokemonId id = PokemonId.forNumber(number);
			return getById(id);
		} catch (final IllegalArgumentException e) {
			return null;
		}
	}

	default Pokemon getById(PokemonId id) {
		return getPokemonMap().get(id);
	}

	default Pokemon transform(Pokemon a, Pokemon d) {
		// TODO: HACK HP is calculated pre transform but CP uses defender base
		// stamina
		Pokemon.Builder clone = a.toBuilder();
		clone.setType(d.getType()).setType2(d.getType2()).getStatsBuilder().setBaseAttack(d.getStats().getBaseAttack())
				.setBaseDefense(d.getStats().getBaseDefense());

		return clone.build();
	}

}