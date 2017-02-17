package com.pokebattler.fight.resources;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.protobuf.util.JsonFormat;
import com.leandronunes85.etag.ETag;
import com.pokebattler.fight.calculator.AttackSimulator;
import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.Fight;
import com.pokebattler.fight.data.proto.FightOuterClass.FightResult;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.jaxrs.CacheControl;

@Component
@Path("/fights")
public class FightResource {

    @Resource
    AttackSimulator simulator;
    @Resource
    Formulas formulas;
    @Resource
    PokemonDataCreator creator;

    JsonFormat.Printer printer = JsonFormat.printer().omittingInsignificantWhitespace();


    public static final String MAX_LEVEL = "40";
    public static final int MAX_INDIVIDUAL_STAT = 15;
    public Logger log = LoggerFactory.getLogger(getClass());

    @POST
    @Produces("application/json")
    public FightResult fight(Fight fight) {
        log.debug("Calculating dps for fight {}", fight);

        return simulator.fight(fight, true).build();

    }


    /*
     * Example url:
     * http://localhost:8080/fights/attackers/SNORLAX/quickMoves/LICK_FAST/
     * cinMoves/BODY_SLAM/levels/20/defenders/VAPOREON/quickMoves/WATER_GUN_FAST
     * /cinMoves/AQUA_TAIL/levels/20/strategies/QUICK_ATTACK_ONLY
     */
    @GET
    @Path("attackers/{attackerId}/quickMoves/{move1}/cinMoves/{move2}/levels/{attackerLevel}/defenders/{defenderId}"
            + "/quickMoves/{dmove1}/cinMoves/{dmove2}/levels/{defenderLevel}/strategies/{strategy}")
    @Produces("application/json")
    @ETag
    @CacheControl("max-age=86000")
    public FightResult fightByLevel(@PathParam("attackerId") PokemonId attackerId, @PathParam("move1") PokemonMove move1,
            @PathParam("move2") PokemonMove move2, @PathParam("attackerLevel") String attackerLevel,
            @PathParam("defenderLevel") String defenderLevel, @PathParam("defenderId") PokemonId defenderId,
            @PathParam("strategy") AttackStrategyType strategy, @PathParam("dmove1") PokemonMove dmove1,
            @PathParam("dmove2") PokemonMove dmove2) {
        log.debug(
                "Calculating dps for attacker {}, defender {}, move1 {}, move2 {}, attackStrategy {}, attackerLevel {}, defenderLevel {}",
                attackerId, defenderId, move1, move2, strategy, attackerLevel, defenderLevel);
        final PokemonData attacker = creator.createMaxStatPokemon(attackerId, attackerLevel, move1, move2);
        final PokemonData defender = creator.createMaxStatPokemon(defenderId, defenderLevel, dmove1, dmove2);
        return simulator.calculateAttackDPS(attacker, defender, strategy);

    }

    /*
     * Example url:
     * http://localhost:8080/fights/attackers/SNORLAX/quickMoves/LICK_FAST/
     * cinMoves/BODY_SLAM/levels/20/defenders/VAPOREON/quickMoves/WATER_GUN_FAST
     * /cinMoves/AQUA_TAIL/levels/20/strategies/QUICK_ATTACK_ONLY/DEFENSE
     */
    @GET
    @Path("attackers/{attackerId}/quickMoves/{move1}/cinMoves/{move2}/levels/{attackerLevel}/defenders/{defenderId}"
            + "/quickMoves/{dmove1}/cinMoves/{dmove2}/levels/{defenderLevel}/strategies/{attackStrategy}/{defenseStrategy}")
    @Produces("application/json")
    @ETag
    public Response fightByLevel2(@PathParam("attackerId") PokemonId attackerId, @PathParam("move1") PokemonMove move1,
            @PathParam("move2") PokemonMove move2, @PathParam("attackerLevel") String attackerLevel,
            @PathParam("defenderLevel") String defenderLevel, @PathParam("defenderId") PokemonId defenderId,
            @PathParam("dmove1") PokemonMove dmove1, @PathParam("dmove2") PokemonMove dmove2,
            @PathParam("attackStrategy") AttackStrategyType attackStrategy, 
            @PathParam("defenseStrategy") AttackStrategyType defenseStrategy) {
        log.debug(
                "Calculating dps for attacker {}, defender {}, move1 {}, move2 {}, attackStrategy {}, defenseStrategy {}, attackerLevel {}, defenderLevel {}",
                attackerId, defenderId, move1, move2, attackStrategy, attackerLevel, defenseStrategy, defenderLevel);
        final PokemonData attacker = creator.createMaxStatPokemon(attackerId, attackerLevel, move1, move2);
        final PokemonData defender = creator.createMaxStatPokemon(defenderId, defenderLevel, dmove1, dmove2);

        final javax.ws.rs.core.CacheControl cacheControl = new javax.ws.rs.core.CacheControl();
        cacheControl.setMaxAge(isRandom(attackStrategy, defenseStrategy) ? 0 : 86000);
        final FightResult fightResult = simulator.calculateAttackDPS(attacker, defender, attackStrategy, defenseStrategy);
        return Response.ok(fightResult).cacheControl(cacheControl).build();
    }

    /*
     * Example url:
     * http://localhost:8080/fights/attackers/SNORLAX/quickMoves/LICK_FAST/cinMoves/BODY_SLAM/levels/20/ivs/ABC/defenders/VAPOREON/quickMoves/WATER_GUN_FAST/cinMoves/AQUA_TAIL/levels/20/ivs/89F/strategies/QUICK_ATTACK_ONLY/DEFENSE
     */
    @GET
    @Path("attackers/{attackerId}/quickMoves/{move1}/cinMoves/{move2}/levels/{attackerLevel}/ivs/{attackerIV}/defenders/{defenderId}"
            + "/quickMoves/{dmove1}/cinMoves/{dmove2}/levels/{defenderLevel}/ivs/{defenderIV}/strategies/{attackStrategy}/{defenseStrategy}")
    @Produces("application/json")
    @ETag
    public Response fightByLevelIV(@PathParam("attackerId") PokemonId attackerId, @PathParam("move1") PokemonMove move1,
            @PathParam("move2") PokemonMove move2, @PathParam("attackerLevel") String attackerLevel, 
            @PathParam("attackerIV") IVWrapper attackerIV, @PathParam("defenderIV") IVWrapper defenderIV,  
            @PathParam("defenderLevel") String defenderLevel, @PathParam("defenderId") PokemonId defenderId,
            @PathParam("dmove1") PokemonMove dmove1, @PathParam("dmove2") PokemonMove dmove2,
            @PathParam("attackStrategy") AttackStrategyType attackStrategy, 
            @PathParam("defenseStrategy") AttackStrategyType defenseStrategy) {
        log.debug(
                "Calculating dps for attacker {}, defender {}, move1 {}, move2 {}, attackStrategy {}, defenseStrategy {}, attackerLevel {}, defenderLevel {}",
                attackerId, defenderId, move1, move2, attackStrategy, attackerLevel, defenseStrategy, defenderLevel);
        final PokemonData attacker = creator.createPokemon(attackerId, attackerLevel, attackerIV.getAttack(), attackerIV.getDefense(),
                attackerIV.getStamina(), move1, move2);
        final PokemonData defender = creator.createPokemon(defenderId, defenderLevel, defenderIV.getAttack(), defenderIV.getDefense(),
                defenderIV.getStamina(), dmove1, dmove2);

        final javax.ws.rs.core.CacheControl cacheControl = new javax.ws.rs.core.CacheControl();
        cacheControl.setMaxAge(isRandom(attackStrategy, defenseStrategy) ? 0 : 86000);
        final FightResult fightResult = simulator.calculateAttackDPS(attacker, defender, attackStrategy, defenseStrategy);
        return Response.ok(fightResult).cacheControl(cacheControl).build();
    }    

    
    /*
     * Example url:
     * http://localhost:8080/fights/attackers/SNORLAX/quickMoves/LICK_FAST/cinMoves/BODY_SLAM/levels/20/ivs/ABC/defenders/VAPOREON/quickMoves/WATER_GUN_FAST/cinMoves/AQUA_TAIL/cp/2089/strategies/QUICK_ATTACK_ONLY/DEFENSE
     */
    @GET
    @Path("attackers/{attackerId}/quickMoves/{move1}/cinMoves/{move2}/levels/{attackerLevel}/ivs/{attackerIV}/defenders/{defenderId}"
            + "/quickMoves/{dmove1}/cinMoves/{dmove2}/cp/{defenderCp}/strategies/{attackStrategy}/{defenseStrategy}")
    @Produces("application/json")
    @ETag
    public Response fightByLevelIV2(@PathParam("attackerId") PokemonId attackerId, @PathParam("move1") PokemonMove move1,
            @PathParam("move2") PokemonMove move2, @PathParam("attackerLevel") String attackerLevel, 
            @PathParam("attackerIV") IVWrapper attackerIV, @PathParam("defenderId") PokemonId defenderId, @PathParam("defenderCp") int defenderCp,
            @PathParam("dmove1") PokemonMove dmove1, @PathParam("dmove2") PokemonMove dmove2,
            @PathParam("attackStrategy") AttackStrategyType attackStrategy, 
            @PathParam("defenseStrategy") AttackStrategyType defenseStrategy) {
        log.debug(
                "Calculating dps for attacker {}, defender {}, move1 {}, move2 {}, attackStrategy {}, defenseStrategy {}, attackerLevel {}, defenderCp {}",
                attackerId, defenderId, move1, move2, attackStrategy, attackerLevel, defenseStrategy, defenderCp);
        final PokemonData attacker = creator.createPokemon(attackerId, attackerLevel, attackerIV.getAttack(), attackerIV.getDefense(),
                attackerIV.getStamina(), move1, move2);
        final PokemonData defender = creator.createPokemon(defenderId, defenderCp, dmove1, dmove2);

        final javax.ws.rs.core.CacheControl cacheControl = new javax.ws.rs.core.CacheControl();
        cacheControl.setMaxAge(isRandom(attackStrategy, defenseStrategy) ? 0 : 86000);
        final FightResult fightResult = simulator.calculateAttackDPS(attacker, defender, attackStrategy, defenseStrategy);
        return Response.ok(fightResult).cacheControl(cacheControl).build();
    }    
    
    /*
     * Example url:
     * http://localhost:8080/fights/attackers/SNORLAX/quickMoves/LICK_FAST/cinMoves/BODY_SLAM/cp/1234/defenders/VAPOREON/quickMoves/WATER_GUN_FAST/cinMoves/AQUA_TAIL/levels/20/ivs/89F/strategies/QUICK_ATTACK_ONLY/DEFENSE
     */
    @GET
    @Path("attackers/{attackerId}/quickMoves/{move1}/cinMoves/{move2}/cp/{attackerCp}/defenders/{defenderId}"
            + "/quickMoves/{dmove1}/cinMoves/{dmove2}/levels/{defenderLevel}/ivs/{defenderIV}/strategies/{attackStrategy}/{defenseStrategy}")
    @Produces("application/json")
    @ETag
    public Response fightByLevelIV3(@PathParam("attackerId") PokemonId attackerId, @PathParam("move1") PokemonMove move1,
            @PathParam("move2") PokemonMove move2, @PathParam("defenderIV") IVWrapper defenderIV,  @PathParam("attackerCp") int attackerCp,
            @PathParam("defenderLevel") String defenderLevel, @PathParam("defenderId") PokemonId defenderId,
            @PathParam("dmove1") PokemonMove dmove1, @PathParam("dmove2") PokemonMove dmove2,
            @PathParam("attackStrategy") AttackStrategyType attackStrategy, 
            @PathParam("defenseStrategy") AttackStrategyType defenseStrategy) {
        log.debug(
                "Calculating dps for attacker {}, defender {}, move1 {}, move2 {}, attackStrategy {}, defenseStrategy {}, attackerCp {}, defenderLevel {}",
                attackerId, defenderId, move1, move2, attackStrategy, attackerCp, defenseStrategy, defenderLevel);
        final PokemonData attacker = creator.createPokemon(attackerId, attackerCp, move1, move2);
        final PokemonData defender = creator.createPokemon(defenderId, defenderLevel, defenderIV.getAttack(), defenderIV.getDefense(),
                defenderIV.getStamina(), dmove1, dmove2);

        final javax.ws.rs.core.CacheControl cacheControl = new javax.ws.rs.core.CacheControl();
        cacheControl.setMaxAge(isRandom(attackStrategy, defenseStrategy) ? 0 : 86000);
        final FightResult fightResult = simulator.calculateAttackDPS(attacker, defender, attackStrategy, defenseStrategy);
        return Response.ok(fightResult).cacheControl(cacheControl).build();
    }    
    
    /*
     * Example url:
     * http://localhost:8080/fights/attackers/SNORLAX/quickMoves/LICK_FAST/
     * cinMoves/BODY_SLAM/cp/1850/defenders/VAPOREON/quickMoves/WATER_GUN_FAST/
     * cinMoves/AQUA_TAIL/cp/1847/strategies/QUICK_ATTACK_ONLY/DEFENSE
     */

    @GET
    @Path("attackers/{attackerId}/quickMoves/{move1}/cinMoves/{move2}/cp/{attackerCp}/defenders/{defenderId}"
            + "/quickMoves/{dmove1}/cinMoves/{dmove2}/cp/{defenderCp}/strategies/{attackStrategy}/{defenseStrategy}")
    @Produces("application/json")
    @ETag
    public Response attackDps3(@PathParam("attackerId") PokemonId attackerId, @PathParam("move1") PokemonMove move1,
            @PathParam("move2") PokemonMove move2, @PathParam("attackerCp") int attackerCp,
            @PathParam("defenderCp") int defenderCp, @PathParam("defenderId") PokemonId defenderId,
            @PathParam("dmove1") PokemonMove dmove1, @PathParam("dmove2") PokemonMove dmove2,
            @PathParam("attackStrategy") AttackStrategyType attackStrategy,
            @PathParam("defenseStrategy") AttackStrategyType defenseStrategy) {
        log.debug(
                "Calculating dps for attacker {}, defender {}, move1 {}, move2 {}, attackStrategy {}, attackerCp {}, defenderCp {}, defenseStrategy {}",
                attackerId, defenderId, move1, move2, attackStrategy, attackerCp, defenderCp, defenseStrategy);
        final PokemonData attacker = creator.createPokemon(attackerId, attackerCp, move1, move2);
        final PokemonData defender = creator.createPokemon(defenderId, defenderCp, dmove1, dmove2);
        // set caching based on wether the result is random
        // TODO: refactor this to strategy pattern or change to a parameter?
        // maybe a query parameter to seed the rng?
        final javax.ws.rs.core.CacheControl cacheControl = new javax.ws.rs.core.CacheControl();
        cacheControl.setMaxAge(isRandom(attackStrategy, defenseStrategy) ? 0 : 86000);
        final FightResult fightResult = simulator.calculateAttackDPS(attacker, defender, attackStrategy, defenseStrategy);
        return Response.ok(fightResult).cacheControl(cacheControl).build();

    }
    public static class IVWrapper {
        private final int attack;
        private final int defense;
        private final int stamina;
        
        public IVWrapper(String iv) {
            attack = Integer.decode("0x" + iv.charAt(0));
            defense= Integer.decode("0x" + iv.charAt(1));
            stamina = Integer.decode("0x" + iv.charAt(2));
        }

        public int getAttack() {
            return attack;
        }

        public int getDefense() {
            return defense;
        }

        public int getStamina() {
            return stamina;
        }

        @Override
        public String toString() {
            return "IVWrapper [attack=" + attack + ", defense=" + defense + ", stamina=" + stamina + "]";
        }
        
    }
    private boolean isRandom(AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy) {
        return defenseStrategy == AttackStrategyType.DEFENSE_RANDOM;

    }

}
