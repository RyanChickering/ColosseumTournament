import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

class Simulator {
    private final AbilityModule ABILITY_MODULE = new AbilityModule();
    private final int CRIT_MODIFIER = 2;
    private StringBuilder simulationText;
    private final int[] BASE = {0,0,0,0,0,0,0,0,0,0,0};


    Simulator(){

    }

    String runSim(Fighter fighter1, Fighter fighter2) {
        BattleStats f1 = new BattleStats(fighter1, fighter2);
        BattleStats f2 = new BattleStats(fighter2, fighter1);
        simulationText = new StringBuilder();
        boolean doubles = false;
        int first = 0;
        if (f1.as > 0) {
            if (f1.as >= 5) {
                doubles = true;
                first = 2;
            }
        } else if (f2.as > 0) {
            first = 1;
            if (f2.as >= 5) {
                first = 3;
            }
        } else {
            Random rand = new Random();
            first = rand.nextInt(2);
        }
        BattleState f1state = new BattleState(fighter1, fighter2, doubles);
        BattleState f2state = new BattleState(fighter2, fighter1, doubles);
        if (first == 0) {
            return noDoubles(f1state, f2state);
        } else if(first == 2){
            return doubles(f1state, f2state);
        } else if(first == 1){
            return noDoubles(f2state, f1state);
        } else {
            return doubles(f2state, f1state);
        }
    }

    private String noDoubles(BattleState f1state, BattleState f2state){
        int roundCnt = 1;
        while(f1state.fighterHP > 0 && f2state.fighterHP > 0){
            roundHealing(f1state);
            roundHealing(f2state);
            simulationText.append(String.format("%s%d%s", "\nRound: ", roundCnt, "\n\n"));
            roundCnt++;
            attack(f1state, f2state);
            if(f1state.fighterHP <= 0 ||f2state.fighterHP <= 0){
                break;
            }
            attack(f2state, f1state);
            damageOverTime(f1state, f2state);
            damageOverTime(f2state, f1state);
        }
        if(f1state.fighterHP > 0){
            simulationText.append(f1state.fighter.name);
        } else {
            simulationText.append(f2state.fighter.name);
        }
        simulationText.append(" wins!");
        return simulationText.toString();
    }
    private String doubles(BattleState f1state, BattleState f2state){
        int roundCnt = 1;
        while(f1state.fighterHP > 0 && f2state.fighterHP > 0){
            roundHealing(f1state);
            roundHealing(f2state);
            simulationText.append(String.format("%s%d%s", "\nRound: ", roundCnt, "\n\n"));
            roundCnt++;
            attack(f1state, f2state);
            if(f1state.fighterHP <= 0 ||f2state.fighterHP <= 0){
                break;
            }
            attack(f2state, f1state);
            if(f1state.fighterHP <= 0 ||f2state.fighterHP <= 0){
                break;
            }
            attack(f1state, f2state);
            damageOverTime(f1state, f2state);
            damageOverTime(f2state, f1state);
        }
        if(f1state.fighterHP > 0){
            simulationText.append(f1state.fighter.name);
        } else {
            simulationText.append(f2state.fighter.name);
        }
        simulationText.append(" wins!");
        return simulationText.toString();
    }

    private void attack(BattleState attacker, BattleState defender){
        /* need to check hit, crit, and active skill.
         * Need to make sure that passive skill modifiers exist on the units
         * Some active skills are defensive active skills, need to check for those.
         * Some active skills create effects that last for an entire round, need to make sure that those
         * effects persist.
         */
        attacker.passive = attacker.abilities.passiveCall();
        defender.passive = defender.abilities.passiveCall();
        attacker.abilities.atkHP = attacker.fighterHP;
        attacker.abilities.defHP = defender.fighterHP;
        defender.abilities.atkHP = defender.fighterHP;
        defender.abilities.defHP = attacker.fighterHP;

        Random rand = new Random();
        int hit = rand.nextInt(99);
        int crit = rand.nextInt(99);
        int activation = rand.nextInt(99);
        String offensiveSkill = "";
        String defensiveSkill = "";
        String critical = "";
        String connection = "Miss!";
        String counter = "";
        String recovery = "";
        String selfDamage = "";
        boolean defense = false;
        boolean offense = false;
        int damage = 0;

        if(attacker.abilities.onAttack()){
            if(activation < attacker.abilities.getActivation()){
                if(!(attacker.active[ABILITY_MODULE.EXTRA_ATTACK] > 0)) {
                    attacker.active = attacker.abilities.activeCall();
                    attacker.duration = attacker.abilities.getDuration();
                    if (attacker.doubles) {
                        attacker.duration *= 2;
                    }
                    offense = true;
                    offensiveSkill = attacker.fighter.abilities[ABILITY_MODULE.ACTIVE].toUpperCase() + " ";
                }
            }
        }
        if(!defender.abilities.onAttack()){
            activation = rand.nextInt(99);
            if(activation < defender.abilities.getActivation()){
                defender.active = defender.abilities.activeCall();
                defender.duration = defender.abilities.getDuration();
                defense = true;
                if(attacker.doubles){
                    defender.duration *= 2;
                }
                defensiveSkill = "(" + defender.fighter.name + " " +
                        defender.fighter.abilities[ABILITY_MODULE.ACTIVE].toUpperCase() + ") ";
            }
        }
        int hitThresh = attacker.stats.hit + attacker.active[ABILITY_MODULE.HITUP]
                + attacker.passive[ABILITY_MODULE.HITUP] - defender.active[ABILITY_MODULE.AVOIDUP] -
                defender.passive[ABILITY_MODULE.AVOIDUP];
        if(hit < hitThresh){
            connection = "Hit! ";
            damage = attacker.stats.power + attacker.active[ABILITY_MODULE.DAMAGEUP]
                    + attacker.passive[ABILITY_MODULE.DAMAGEUP] - defender.active[ABILITY_MODULE.DAMAGEREDUCTION] -
                    defender.passive[ABILITY_MODULE.DAMAGEREDUCTION];
            int critThresh = attacker.stats.crit + attacker.active[ABILITY_MODULE.CRITUP]
                    + attacker.passive[ABILITY_MODULE.CRITUP] - defender.active[ABILITY_MODULE.DDGUP] -
                    defender.passive[ABILITY_MODULE.DDGUP];
            if((!offense || attacker.abilities.canCrit()) && crit < critThresh){
                if(attacker.fighter.abilities[ABILITY_MODULE.PASSIVE].toLowerCase().equals("precision")){
                    damage *= 3;
                }
                damage *= CRIT_MODIFIER;
                critical = "CRITICAL ";
            }
            if(damage < 0){
                damage = 0;
            }
            defender.fighterHP -= damage;
            if(defense &&
                    (defender.fighterHP > 0 &&
                            defender.fighter.abilities[ABILITY_MODULE.ACTIVE].toLowerCase().equals("counter"))){
                attacker.fighterHP -= damage/2;
                counter = defender.fighter.name +
                        " COUNTERS! (" + damage/2 + "/" + attacker.fighterHP + ")\n\n";
            }
        }
        if(attacker.active[ABILITY_MODULE.BONUSHEALING] != 0){
            int healing = (damage*attacker.active[ABILITY_MODULE.BONUSHEALING])/100;
            attacker.fighterHP += healing;
            recovery = String.format("%s%s%d%s%d%s", attacker.fighter.name,
                    " recovers ", healing, "HP! (", attacker.fighterHP, ")\n");
        }
        if(attacker.passive[ABILITY_MODULE.SELFDAMAGE] != 0){
            attacker.fighterHP -= attacker.passive[ABILITY_MODULE.SELFDAMAGE];
            selfDamage = String.format("%s%s%s%d%s%d%s", "\n", attacker.fighter.name, " hurts themselves for ",
                    attacker.passive[ABILITY_MODULE.SELFDAMAGE], " damage. (", attacker.fighterHP, ")");
        }

        if(defender.fighter.abilities[ABILITY_MODULE.ACTIVE].toLowerCase().equals("guts") && defense){
            if(defender.fighterHP <= 0) {
                defender.fighterHP = defender.active[ABILITY_MODULE.BONUSHP];
            } else {
                defensiveSkill = "";
            }
        }

        simulationText.append(String.format("%s%s%s%s%s%s%s%d%s%d%s%s%s\n\n%s", attacker.fighter.name, ": ",
                defensiveSkill, offensiveSkill, critical,
                connection, "(", damage, "/", defender.fighterHP, ")", selfDamage, recovery, counter));

        if(!defender.abilities.onAttack()){
            if(defender.duration == 0) {
                Arrays.fill(defender.active, 0);
            } else {
                defender.duration--;
            }
        }

        if(attacker.active[ABILITY_MODULE.EXTRA_ATTACK] > 0){
            attacker.active[ABILITY_MODULE.EXTRA_ATTACK]--;
            this.attack(attacker, defender);
        }

        if(attacker.duration > 0 && attacker.abilities.onAttack()){
            attacker.duration--;
        } else {
            Arrays.fill(attacker.active, 0);
        }


    }

    private void roundHealing(BattleState attacker){
        if(attacker.passive[ABILITY_MODULE.BONUSHEALING] != 0){
            if(attacker.fighterHP < attacker.fighter.hp()){
                attacker.fighterHP += attacker.passive[ABILITY_MODULE.BONUSHEALING];
                if(attacker.fighterHP > attacker.fighter.hp()){
                    attacker.fighterHP = attacker.fighter.hp();
                }
                simulationText.append(String.format("%s%s%s%d%s%d%s", "\n", attacker.fighter.name, " recovers ",
                        attacker.passive[ABILITY_MODULE.BONUSHEALING], "HP (", attacker.fighterHP, ")\n"));
            }
        }
    }

    private void damageOverTime(BattleState attacker, BattleState defender){
        int damage = 0;
        if(attacker.active[ABILITY_MODULE.DOT] != 0){
            for(int i = 0; i < defender.damageOverTime.length; i++){
                if(defender.damageOverTime[i] == 0){
                    defender.damageOverTime[i] = 1;
                    defender.damageOverTime[i]--;
                }
            }
        }
        for(int i = 0; i < defender.damageOverTime.length; i++){
            if(defender.damageOverTime[i] > 0){
                damage += attacker.active[ABILITY_MODULE.DOT];
            }
        }
        defender.fighterHP -= damage;
        if( damage > 0) {
            simulationText.append(String.format("%s%s%s%d%s", "\n", defender.fighter.name, " takes ", damage,
                    "from damage over time.\n"));
        }
    }

    private class BattleState{
        int fighterHP;
        int duration;
        int[] damageOverTime;
        Fighter fighter;
        boolean doubles;
        int attackNum;
        AbilityModule abilities;
        BattleStats stats;
        int[] active;
        int[] passive;
        BattleState(Fighter fighter1, Fighter fighter2, boolean doubles){
            this.fighterHP = fighter1.hp();
            this.doubles = doubles;
            this.fighter = fighter1;
            this.attackNum = 0;
            this.abilities = new AbilityModule(fighter1, fighter2, fighterHP, fighter2.hp());
            this.stats = new BattleStats(fighter1, fighter2);
            active = Arrays.copyOf(BASE, BASE.length);
            passive = this.abilities.passiveCall();
            damageOverTime = new int[] {0,0,0,0,0};
        }
    }
}
