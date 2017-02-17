package POGOProtos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.google.protobuf.util.JsonFormat;

import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;
import POGOProtos.Networking.Responses.DownloadItemTemplatesResponseOuterClass.DownloadItemTemplatesResponse;
import POGOProtos.Settings.Master.PokemonSettingsOuterClass.PokemonSettings;

public class GenerateJSON {
	public GenerateJSON() {
	}

	public void writeJSON(InputStream is, OutputStream os) throws IOException {
		DownloadItemTemplatesResponse response = DownloadItemTemplatesResponse.parseFrom(is);
		response = addLegacyMoves(response);
		JsonFormat.Printer printer = JsonFormat.printer();
		try (OutputStreamWriter writer = new OutputStreamWriter(os)) {
			printer.appendTo(response, writer);
		}
	}

	private DownloadItemTemplatesResponse addLegacyMoves(DownloadItemTemplatesResponse response) {
		DownloadItemTemplatesResponse.Builder retval = response.toBuilder();
		retval.getItemTemplatesBuilderList().stream().forEach(template -> {
			if (template.hasPokemonSettings()) {
				PokemonSettings.Builder pokemon = template.getPokemonSettingsBuilder();
				switch (pokemon.getPokemonId()) {
				case DIGLETT:
					pokemon.addQuickMoves(PokemonMove.MUD_SHOT_FAST);
					break;
				case DUGTRIO:
					pokemon.addQuickMoves(PokemonMove.MUD_SHOT_FAST);
					break;
				case GENGAR:
					pokemon.addCinematicMoves(PokemonMove.SLUDGE_WAVE);
					// gen2
					pokemon.addCinematicMoves(PokemonMove.DARK_PULSE);
					pokemon.addQuickMoves(PokemonMove.SHADOW_CLAW_FAST);
					break;
				case JIGGLYPUFF:
					pokemon.addCinematicMoves(PokemonMove.PLAY_ROUGH);
					// gen2
					pokemon.addCinematicMoves(PokemonMove.BODY_SLAM);
					break;
				case GRAVELER:
					pokemon.addQuickMoves(PokemonMove.MUD_SHOT_FAST);
					// gen2
					pokemon.addCinematicMoves(PokemonMove.ROCK_SLIDE);
					break;
				case GOLEM:
					pokemon.addQuickMoves(PokemonMove.MUD_SHOT_FAST);
					// gen2
					pokemon.addCinematicMoves(PokemonMove.ANCIENT_POWER);
					break;
				case SEEL:
					pokemon.addQuickMoves(PokemonMove.WATER_GUN_FAST);
					// gen2
					pokemon.addCinematicMoves(PokemonMove.AQUA_JET);
					break;
				case GRIMER:
					pokemon.addQuickMoves(PokemonMove.ACID_FAST);
					break;
				case MUK:
					pokemon.addQuickMoves(PokemonMove.ACID_FAST);
					// gen2
					pokemon.addQuickMoves(PokemonMove.LICK_FAST);
					break;
				case HITMONLEE:
					pokemon.addCinematicMoves(PokemonMove.STOMP);
					// gen2
					pokemon.addCinematicMoves(PokemonMove.BRICK_BREAK);
					break;
				case KOFFING:
					pokemon.addQuickMoves(PokemonMove.ACID_FAST);
					break;
				case WEEZING:
					pokemon.addQuickMoves(PokemonMove.ACID_FAST);
					break;
				case CHANSEY:
					pokemon.addCinematicMoves(PokemonMove.PSYBEAM);
					break;
				case STARYU:
					pokemon.addQuickMoves(PokemonMove.QUICK_ATTACK_FAST);
					break;
				case STARMIE:
					pokemon.addQuickMoves(PokemonMove.QUICK_ATTACK_FAST);
					pokemon.addCinematicMoves(PokemonMove.PSYBEAM);
					// gen2
					pokemon.addQuickMoves(PokemonMove.TACKLE_FAST);
					break;
				case GYARADOS:
					pokemon.addQuickMoves(PokemonMove.DRAGON_BREATH_FAST);
					// gen2
					pokemon.addCinematicMoves(PokemonMove.TWISTER);
					pokemon.addCinematicMoves(PokemonMove.DRAGON_PULSE);
					break;
				case PORYGON:
					pokemon.addQuickMoves(PokemonMove.QUICK_ATTACK_FAST);
					// gen2
					pokemon.addCinematicMoves(PokemonMove.PSYBEAM);
					pokemon.addCinematicMoves(PokemonMove.SIGNAL_BEAM);
					pokemon.addCinematicMoves(PokemonMove.DISCHARGE);
					pokemon.addQuickMoves(PokemonMove.ZEN_HEADBUTT_FAST);
					pokemon.addQuickMoves(PokemonMove.TACKLE_FAST);
					break;
				case OMASTAR:
					pokemon.addQuickMoves(PokemonMove.ROCK_THROW_FAST);
					pokemon.addCinematicMoves(PokemonMove.ROCK_SLIDE);
					break;

				// gen2
				case CHARMELEON:
					pokemon.addQuickMoves(PokemonMove.SCRATCH_FAST);
					break;
				case CHARIZARD:
					pokemon.addCinematicMoves(PokemonMove.FLAMETHROWER);
					pokemon.addQuickMoves(PokemonMove.EMBER_FAST);
					pokemon.addQuickMoves(PokemonMove.WING_ATTACK_FAST);
					break;
				case BUTTERFREE:
					pokemon.addQuickMoves(PokemonMove.BUG_BITE_FAST);
					break;
				case BEEDRILL:
					pokemon.addQuickMoves(PokemonMove.BUG_BITE_FAST);
					break;
				case PIDGEOT:
					pokemon.addCinematicMoves(PokemonMove.AIR_CUTTER);

					pokemon.addQuickMoves(PokemonMove.WING_ATTACK_FAST);
					break;
				case SPEAROW:
					pokemon.addCinematicMoves(PokemonMove.TWISTER);
					break;
				case FEAROW:
					pokemon.addCinematicMoves(PokemonMove.TWISTER);
					break;
				case EKANS:
					pokemon.addCinematicMoves(PokemonMove.GUNK_SHOT);
					break;
				case PIKACHU:
					pokemon.addCinematicMoves(PokemonMove.THUNDER);
					break;
				case RAICHU:
					pokemon.addCinematicMoves(PokemonMove.THUNDER);
					pokemon.addQuickMoves(PokemonMove.THUNDER_SHOCK_FAST);
					break;

				case SANDSHREW:
					pokemon.addCinematicMoves(PokemonMove.ROCK_TOMB);
					break;
				case NIDOKING:
					pokemon.addQuickMoves(PokemonMove.FURY_CUTTER_FAST);
					break;
				case CLEFABLE:
					pokemon.addQuickMoves(PokemonMove.POUND_FAST);
					break;
				case NINETALES:
					pokemon.addCinematicMoves(PokemonMove.FLAMETHROWER);
					pokemon.addCinematicMoves(PokemonMove.FIRE_BLAST);
					pokemon.addQuickMoves(PokemonMove.EMBER_FAST);
					break;
				case ZUBAT:
					pokemon.addCinematicMoves(PokemonMove.SLUDGE_BOMB);
					break;
				case GOLBAT:
					pokemon.addCinematicMoves(PokemonMove.OMINOUS_WIND);
					break;
				case PARASECT:
					pokemon.addQuickMoves(PokemonMove.BUG_BITE_FAST);
					break;
				case VENOMOTH:
					pokemon.addCinematicMoves(PokemonMove.POISON_FANG);
					pokemon.addQuickMoves(PokemonMove.BUG_BITE_FAST);
					break;

				case MEOWTH:
					pokemon.addCinematicMoves(PokemonMove.BODY_SLAM);
					break;
				case PERSIAN:
					pokemon.addCinematicMoves(PokemonMove.NIGHT_SLASH);
					break;
				case PRIMEAPE:
					pokemon.addCinematicMoves(PokemonMove.CROSS_CHOP);
					pokemon.addQuickMoves(PokemonMove.KARATE_CHOP_FAST);
					break;
				case ARCANINE:
					pokemon.addCinematicMoves(PokemonMove.FLAMETHROWER);
					pokemon.addCinematicMoves(PokemonMove.BULLDOZE);
					pokemon.addQuickMoves(PokemonMove.BITE_FAST);
					break;

				case POLIWHIRL:
					pokemon.addCinematicMoves(PokemonMove.SCALD);
					break;
				case POLIWRATH:
					pokemon.addCinematicMoves(PokemonMove.SUBMISSION);
					pokemon.addQuickMoves(PokemonMove.MUD_SHOT_FAST);
					break;

				case ALAKAZAM:
					pokemon.addCinematicMoves(PokemonMove.PSYCHIC);
					pokemon.addCinematicMoves(PokemonMove.DAZZLING_GLEAM);
					break;

				case MACHOP:
					pokemon.addQuickMoves(PokemonMove.LOW_KICK_FAST);
					break;
				case MACHOKE:
					pokemon.addCinematicMoves(PokemonMove.CROSS_CHOP);
					break;
				case MACHAMP:
					pokemon.addCinematicMoves(PokemonMove.STONE_EDGE);
					pokemon.addCinematicMoves(PokemonMove.SUBMISSION);
					pokemon.addCinematicMoves(PokemonMove.CROSS_CHOP);
					pokemon.addQuickMoves(PokemonMove.KARATE_CHOP_FAST);
					break;
				case WEEPINBELL:
					pokemon.addQuickMoves(PokemonMove.RAZOR_LEAF_FAST);
					break;
				case PONYTA:
					pokemon.addCinematicMoves(PokemonMove.FIRE_BLAST);
					break;
				case RAPIDASH:
					pokemon.addQuickMoves(PokemonMove.EMBER_FAST);
					break;
				case MAGNETON:
					pokemon.addCinematicMoves(PokemonMove.DISCHARGE);
					pokemon.addQuickMoves(PokemonMove.THUNDER_SHOCK_FAST);
					break;
				case FARFETCHD:
					pokemon.addQuickMoves(PokemonMove.CUT_FAST);
					break;
				case DODUO:
					pokemon.addCinematicMoves(PokemonMove.SWIFT);
					break;
				case DODRIO:
					pokemon.addCinematicMoves(PokemonMove.AIR_CUTTER);
					break;
				case DEWGONG:
					pokemon.addCinematicMoves(PokemonMove.ICY_WIND);
					pokemon.addCinematicMoves(PokemonMove.AQUA_JET);
					pokemon.addQuickMoves(PokemonMove.ICE_SHARD_FAST);
					break;
				case CLOYSTER:
					pokemon.addCinematicMoves(PokemonMove.ICY_WIND);
					pokemon.addCinematicMoves(PokemonMove.BLIZZARD);
					break;
				case GASTLY:
					pokemon.addCinematicMoves(PokemonMove.OMINOUS_WIND);
					pokemon.addQuickMoves(PokemonMove.SUCKER_PUNCH_FAST);
					break;
				case HAUNTER:
					pokemon.addCinematicMoves(PokemonMove.SHADOW_BALL);
					pokemon.addQuickMoves(PokemonMove.LICK_FAST);
					break;
				case ONIX:
					pokemon.addCinematicMoves(PokemonMove.ROCK_SLIDE);
					pokemon.addCinematicMoves(PokemonMove.IRON_HEAD);
					break;
				case HYPNO:
					pokemon.addCinematicMoves(PokemonMove.PSYSHOCK);
					pokemon.addCinematicMoves(PokemonMove.SHADOW_BALL);
					break;
				case KINGLER:
					pokemon.addQuickMoves(PokemonMove.MUD_SHOT_FAST);
				case VOLTORB:
					pokemon.addCinematicMoves(PokemonMove.SIGNAL_BEAM);
				case ELECTRODE:
					pokemon.addQuickMoves(PokemonMove.TACKLE_FAST);
				case EXEGGUTOR:
					pokemon.addQuickMoves(PokemonMove.CONFUSION_FAST);
					pokemon.addQuickMoves(PokemonMove.ZEN_HEADBUTT_FAST);
					break;
				case HITMONCHAN:
					pokemon.addCinematicMoves(PokemonMove.BRICK_BREAK);
					pokemon.addQuickMoves(PokemonMove.ROCK_SMASH_FAST);
					break;
				case TANGELA:
					pokemon.addCinematicMoves(PokemonMove.POWER_WHIP);
					break;
				case KANGASKHAN:
					pokemon.addCinematicMoves(PokemonMove.BRICK_BREAK);
					pokemon.addCinematicMoves(PokemonMove.STOMP);
					break;
				case SEADRA:
					pokemon.addCinematicMoves(PokemonMove.BLIZZARD);
					break;
				case SEAKING:
					pokemon.addCinematicMoves(PokemonMove.ICY_WIND);
					pokemon.addCinematicMoves(PokemonMove.DRILL_RUN);
					break;
				case SCYTHER:
					pokemon.addCinematicMoves(PokemonMove.BUG_BUZZ);
					pokemon.addQuickMoves(PokemonMove.STEEL_WING_FAST);
					break;
				case JYNX:
					pokemon.addCinematicMoves(PokemonMove.ICE_PUNCH);
					pokemon.addQuickMoves(PokemonMove.POUND_FAST);
					break;
				case PINSIR:
					pokemon.addCinematicMoves(PokemonMove.SUBMISSION);
					pokemon.addQuickMoves(PokemonMove.FURY_CUTTER_FAST);
					break;
				case LAPRAS:
					pokemon.addCinematicMoves(PokemonMove.DRAGON_PULSE);
					pokemon.addQuickMoves(PokemonMove.ICE_SHARD_FAST);
					break;

				case EEVEE:
					pokemon.addCinematicMoves(PokemonMove.BODY_SLAM);
					break;
				case FLAREON:
					pokemon.addCinematicMoves(PokemonMove.HEAT_WAVE);
					break;
				case OMANYTE:
					pokemon.addCinematicMoves(PokemonMove.BRINE);
					pokemon.addCinematicMoves(PokemonMove.ROCK_TOMB);

					break;

				case KABUTOPS:
					pokemon.addQuickMoves(PokemonMove.FURY_CUTTER_FAST);
					break;
				case SNORLAX:
					pokemon.addCinematicMoves(PokemonMove.BODY_SLAM);
					break;
				case ZAPDOS:
					pokemon.addCinematicMoves(PokemonMove.DISCHARGE);
					pokemon.addQuickMoves(PokemonMove.THUNDER_SHOCK_FAST);
					break;
				case MOLTRES:
					pokemon.addCinematicMoves(PokemonMove.FLAMETHROWER);
					pokemon.addQuickMoves(PokemonMove.EMBER_FAST);
					break;
				case DRAGONITE:
					pokemon.addCinematicMoves(PokemonMove.DRAGON_PULSE);
					pokemon.addCinematicMoves(PokemonMove.DRAGON_CLAW);
					pokemon.addQuickMoves(PokemonMove.DRAGON_BREATH_FAST);
					break;
				// not including babies or togeptic
				default:
					break;

				}
			}
		});
		return retval.build();
	}

	public static void main(String[] args) throws Exception {
		GenerateJSON gen = new GenerateJSON();
		gen.writeJSON(gen.getClass().getResourceAsStream("/GAME_MASTER.data"), System.out);

	}

}
