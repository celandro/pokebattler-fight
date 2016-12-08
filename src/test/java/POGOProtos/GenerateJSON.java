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
                    break;
                case JIGGLYPUFF:
                    pokemon.addCinematicMoves(PokemonMove.PLAY_ROUGH);
                    break;
                case GRAVELER:
                    pokemon.addQuickMoves(PokemonMove.MUD_SHOT_FAST);
                    break;
                case GOLEM:
                    pokemon.addQuickMoves(PokemonMove.MUD_SHOT_FAST);
                    break;
                case SEEL:
                    pokemon.addQuickMoves(PokemonMove.WATER_GUN_FAST);
                    break;
                case GRIMER:
                    pokemon.addQuickMoves(PokemonMove.ACID_FAST);
                    break;
                case MUK:
                    pokemon.addQuickMoves(PokemonMove.ACID_FAST);
                    break;
                case HITMONLEE:
                    pokemon.addCinematicMoves(PokemonMove.STOMP);
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
                    break;
                case GYARADOS:
                    pokemon.addQuickMoves(PokemonMove.DRAGON_BREATH_FAST);
                    break;
                case PORYGON:
                    pokemon.addQuickMoves(PokemonMove.QUICK_ATTACK_FAST);
                    break;
                case OMASTAR:
                    pokemon.addQuickMoves(PokemonMove.ROCK_THROW_FAST);
                    break;
                case MEW:
                    pokemon.addCinematicMoves(PokemonMove.HURRICANE);
                    pokemon.addCinematicMoves(PokemonMove.MOONBLAST);
                    break;
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
