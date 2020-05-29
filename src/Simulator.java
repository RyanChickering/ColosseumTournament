/* Class that contains the methods to run the actual battle simulation between two
 * fighters. Split off from FightSim.java to make code more manageable.
 */

import java.util.Arrays;
import java.util.Random;

class Simulator {
    private final AbilityModule ABILITY_MODULE = new AbilityModule();
    private StringBuilder simulationText;


    Simulator(){

    }

    //Main method for running the simulation. Takes in two fighters
    String runSim(Fighter fighter1, Fighter fighter2) {
        final int F1_FIRST = 0;
        final int F2_FIRST = 1;
        //Generates battle stats for the two fighters
        BattleStats f1 = new BattleStats(fighter1, fighter2);
        BattleStats f2 = new BattleStats(fighter2, fighter1);
        //Initializes simulation text
        simulationText = new StringBuilder();
        boolean doubles = false;
        int first = F1_FIRST;
        //Checks if one of the fighters is going to double
        if (f1.as > 0) {
            //If fighter1 has higher attack speed, will attack first
            if (f1.as >= 5) {
                //If fighter1 outspeeds by 5, will double
                doubles = true;
            }
        } else if (f2.as > 0) {
            //If fighter2 has higher attack speed, will attack first
            first = F2_FIRST;
            if (f2.as >= 5) {
                //If fighter2 outspeeds by 5, will double
                doubles = true;
            }
        } else {
            //If the fighters have equal attack speed, generate a random number
            //to determine who attacks first
            Random rand = new Random();
            first = rand.nextInt(2);
        }
        //Generates a battle state for each of the fighters (See inner class BattleState for details)
        BattleState f1state = new BattleState(fighter1, fighter2, doubles);
        BattleState f2state = new BattleState(fighter2, fighter1, doubles);
        //Who goes first affects the order in which the states are passed to the fight simulator
        if (first == F1_FIRST) {
            return simFight(f1state, f2state, doubles);
        } else {
            return simFight(f2state, f1state, doubles);
        }
    }

    //Runs a simulation where neither party doubles. The state provided as f1state attacks first
    private String simFight(BattleState f1state, BattleState f2state, boolean doubles){
        //keeps track of the current round
        int roundCnt = 1;
        boolean stalemate = false;
        //while both fighters still have HP remaining
        while(f1state.fighterHP > 0 && f2state.fighterHP > 0){
            //Processes passive healing. Performed at the beginning of the round
            //so that fighters cannot be revived from 0 health with passive healing
            roundHealing(f1state);
            roundHealing(f2state);
            //Indicates the start of a new round of combat
            simulationText.append(String.format("%s%d%s", "\nRound: ", roundCnt, "\n\n"));
            roundCnt++;
            //Processes the first attack
            attack(f1state, f2state);
            //If that attack killed, end the battle
            if(f1state.fighterHP <= 0 ||f2state.fighterHP <= 0){
                break;
            }
            //Processes the second attack
            attack(f2state, f1state);
            //If that attack killed, end the battle
            if(f1state.fighterHP <= 0 ||f2state.fighterHP <= 0){
                break;
            }
            //If there are doubles, the first party attacks again
            if(doubles){
                //Processes the third attack
                attack(f1state, f2state);
            }
            if(f1state.fighterHP <= 0 ||f2state.fighterHP <= 0){
                break;
            }
            //Processes damage over time.
            damageOverTime(f1state, f2state);
            damageOverTime(f2state, f1state);
            if(roundCnt > 200){
                stalemate = true;
                break;
            }
        }
        //Checks to see which fighter won the match
        if(stalemate){
            simulationText.append("Neither fighter can defeat the other. Stalemate");
            return simulationText.toString();
        }
        if(f1state.fighterHP > 0){
            simulationText.append(f1state.fighter.name);
        } else {
            simulationText.append(f2state.fighter.name);
        }
        simulationText.append(" wins!");
        //returns the text of the simulation
        return simulationText.toString();
    }

    //Processes an attack between an attacker battle State and a defender battle state
    private void attack(BattleState attacker, BattleState defender){
        final int CRIT_MODIFIER = 2;
        /* need to check hit, crit, and active skill.
         * Need to make sure that passive skill modifiers exist on the units
         * Some active skills are defensive active skills, need to check for those.
         * Some active skills create effects that last for an entire round, need to make sure that those
         * effects persist.
         */
        //updates the HP values for each fighter
        attacker.abilities.atkHP = attacker.fighterHP;
        attacker.abilities.defHP = defender.fighterHP;
        defender.abilities.atkHP = defender.fighterHP;
        defender.abilities.defHP = attacker.fighterHP;
        //sets the passive modifiers for each fighter (some skills rely on updated HP stats)
        attacker.passive = attacker.abilities.passiveCall();
        defender.passive = defender.abilities.passiveCall();

        //Generates random numbers to be used in the simulation
        Random rand = new Random();
        int hit = rand.nextInt(99);
        int crit = rand.nextInt(99);
        int activation = rand.nextInt(99);
        //Initializes strings that will be used in message readouts
        String offensiveSkill = "";
        String defensiveSkill = "";
        String critical = "";
        String connection = "Miss!";
        String counter = "";
        String recovery = "";
        String selfDamage = "";
        //Booleans to keep track of skill activation
        boolean defense = false;
        boolean offense = false;
        int damage = 0;

        //Checks for active skills first
        if(attacker.abilities.onAttack()){
            //If the attacker has an ability that activates on their own attacks
            if(activation < attacker.abilities.getActivation()){
                //If the random number for skill activation is lower than the activation rate,
                //the skill activated
                if(!(attacker.active[ABILITY_MODULE.EXTRA_ATTACK] > 0)) {
                    //Extra attacks cannot activate more active skills
                    //performs an active skill call to get the effects of the active skill
                    attacker.active = attacker.abilities.activeCall();
                    attacker.duration = attacker.abilities.getDuration();
                    if (attacker.doubles) {
                        attacker.duration *= 2;
                    }
                    //sets the offensive skill activation to true
                    offense = true;
                    //String that will say the name of the skill
                    offensiveSkill = attacker.fighter.abilities[ABILITY_MODULE.ACTIVE].toUpperCase() + " ";
                }
            }
        }
        //Checks for defensive active skills
        if(!defender.abilities.onAttack()){
            //If the defender has a skill that activates when under attack
            //Roll a new random number for skill activation
            activation = rand.nextInt(99);
            if(activation < defender.abilities.getActivation()){
                //If the number was lower than the activation rate
                //Perform an active skill call to get the effects of the skill
                defender.active = defender.abilities.activeCall();
                defender.duration = defender.abilities.getDuration();
                //Set the defensive skill to true
                defense = true;
                if(attacker.doubles){
                    defender.duration *= 2;
                }
                //String that says that a defensive skill activated
                defensiveSkill = "(" + defender.fighter.name + " " +
                        defender.fighter.abilities[ABILITY_MODULE.ACTIVE].toUpperCase() + ") ";
            }
        }
        //Calculates the threshold that the hit random number needs to be below for an attack to connect
        //Base hit + active and passive skill modifiers for hit -
        //base avoid - activa and passive skill modifiers for avoid
        int hitThresh = attacker.stats.hit + attacker.active[ABILITY_MODULE.HITUP]
                + attacker.passive[ABILITY_MODULE.HITUP] - defender.active[ABILITY_MODULE.AVOIDUP] -
                defender.passive[ABILITY_MODULE.AVOIDUP];
        if(hit < hitThresh){
            //If the hit random number was lower than the threshold, the attack connected
            connection = "Hit! ";
            //Calculates the damage done. Base power further affected by the attacker's skill modifiers for damageup
            //and the defender's skill modifiers for damage reduction
            damage = attacker.stats.power + attacker.active[ABILITY_MODULE.DAMAGEUP]
                    + attacker.passive[ABILITY_MODULE.DAMAGEUP] - defender.active[ABILITY_MODULE.DAMAGEREDUCTION] -
                    defender.passive[ABILITY_MODULE.DAMAGEREDUCTION];
            //Calculates the value needed to critical. Based on attacker crit, further affected by the attacker's
            //skill modifiers for critup and the defender's skill modifiers for ddgup
            int critThresh = attacker.stats.crit + attacker.active[ABILITY_MODULE.CRITUP]
                    + attacker.passive[ABILITY_MODULE.CRITUP] - defender.active[ABILITY_MODULE.DDGUP] -
                    defender.passive[ABILITY_MODULE.DDGUP];
            if((!offense || attacker.abilities.canCrit()) && crit < critThresh){
                //If((Offensive skill activating -> canCrit) AND crit random number met the threshold)
                if(attacker.fighter.abilities[ABILITY_MODULE.PASSIVE].toLowerCase().equals("precision")){
                    //If the attacker has the passive skill "precision" crits do extra damage
                    damage *= 3;
                } else {
                    //Multiply damage by the crit modifier
                    damage *= CRIT_MODIFIER;
                }
                //String that says a hit was a critical
                critical = "CRITICAL ";
            }
            //An attacker cannot deal negative damage
            if(damage < 0){
                damage = 0;
            }
            //subtract the damage done from the defender's HP
            defender.fighterHP -= damage;

            //Healing check
            if(attacker.active[ABILITY_MODULE.BONUSHEALING] != 0){
                //If the attacker's active skill restored health
                int healing = (damage*attacker.active[ABILITY_MODULE.BONUSHEALING])/100;
                //Add the healing done to the attacker's HP
                attacker.fighterHP += healing;
                //String indicating that recovery occurred
                recovery = String.format("%s%s%s%d%s%d%s", " ", attacker.fighter.name,
                        " recovers ", healing, " HP! (", attacker.fighterHP, ")");
            }
            //Self damage check
            if(attacker.passive[ABILITY_MODULE.SELFDAMAGE] != 0 || attacker.active[ABILITY_MODULE.SELFDAMAGE] != 0){
                //If the attacker's skills inflicts self damage
                attacker.fighterHP -= attacker.passive[ABILITY_MODULE.SELFDAMAGE];
                //String that indicates that self damage occurred
                selfDamage = String.format("%s%s%s%d%s%d%s", "\n\n", attacker.fighter.name, " hurts themselves for ",
                        attacker.passive[ABILITY_MODULE.SELFDAMAGE], " damage. (", attacker.fighterHP, ")");
            }
            //Counter check
            if(defense &&
                    (defender.fighterHP > 0 &&
                            defender.fighter.abilities[ABILITY_MODULE.ACTIVE].toLowerCase().equals("counter"))){
                //Check for the counter skill. Need to have activated a defensive skill, still be alive, and
                //have the counter skill
                //Deals damage to the attacker equal to half the damage received
                attacker.fighterHP -= damage/2;
                //String that indicates a counter occurred
                counter = defender.fighter.name +
                        " COUNTERS! (" + damage/2 + "/" + attacker.fighterHP + ")\n\n";
            }
            //Damage over time application
            if(offense && attacker.active[ABILITY_MODULE.DOT] != 0){
                damageOverTimeStack(attacker, defender);
            }
        }
        //Guts check
        if(defender.fighter.abilities[ABILITY_MODULE.ACTIVE].toLowerCase().equals("guts") && defense){
            //If the defender has the guts ability and it activated
            if(defender.fighterHP <= 0) {
                //If the defender died from the last attack, reset it to the guts restoration value
                defender.fighterHP = defender.active[ABILITY_MODULE.BONUSHP];
            } else {
                defensiveSkill = "";
            }
        }

        //Adds the attack to the simulation text. Most of the strings are blank by default so they will only
        //print if their respective event occurred. A maxed attack string looks like:
        /*
        Jellal: (Walle COUNTER) SOL Hit! (0/250) Jellal recovers 0 HP! (30)

        Jellal hurts themselves for 4 damage. (26)

        Walle COUNTERS! (0/26)
        */
        simulationText.append(String.format("%s%s%s%s%s%s%s%d%s%d%s%s%s\n\n%s", attacker.fighter.name, ": ",
                defensiveSkill, offensiveSkill, critical,
                connection, "(", damage, "/", defender.fighterHP, ")", recovery, selfDamage, counter));

        //Decreases the duration of currently active defensive active skills
        if(!defender.abilities.onAttack()){
            if(defender.duration == 0) {
                Arrays.fill(defender.active, 0);
            } else {
                defender.duration--;
            }
        }

        //Extra attack check
        if(attacker.active[ABILITY_MODULE.EXTRA_ATTACK] > 0 && defender.fighterHP > 0){
            //if the attacker has an active attack and the defender is not defeated
            //Lower the number of remaining extra attacks
            attacker.active[ABILITY_MODULE.EXTRA_ATTACK]--;
            //perform a bonus attack
            this.attack(attacker, defender);
        }

        //Decreases the duration of currently active offensive active skills
        if(attacker.duration > 0 && attacker.abilities.onAttack()){
            attacker.duration--;
        } else {
            Arrays.fill(attacker.active, 0);
        }
    }

    //Method to process passive healing given a battle state
    private void roundHealing(BattleState attacker){
        if(attacker.passive[ABILITY_MODULE.BONUSHEALING] != 0){
            //if passive healing is not 0
            if(attacker.fighterHP < attacker.fighter.hp()){
                attacker.fighterHP += attacker.passive[ABILITY_MODULE.BONUSHEALING];
                if(attacker.fighterHP > attacker.fighter.hp()){
                    attacker.fighterHP = attacker.fighter.hp();
                }
                //adds a message to the simulation text
                simulationText.append(String.format("%s%s%s%d%s%d%s", "\n", attacker.fighter.name, " recovers ",
                        attacker.passive[ABILITY_MODULE.BONUSHEALING], "HP (", attacker.fighterHP, ")\n"));
            }
        }
    }

    //Method to add damage over time stacks to a character
    private void damageOverTimeStack(BattleState attacker, BattleState defender){
        //Looks for an empty spot in the damage over time array. Puts the duration of active skill there
        if(attacker.active[ABILITY_MODULE.DOT] != 0){
            for(int i = 0; i < defender.damageOverTime.length; i++){
                if(defender.damageOverTime[i] == 0){
                    defender.damageOverTime[i] = attacker.abilities.getDuration();
                    break;
                }
            }
        }
    }

    //Method to process damage over time.
    private void damageOverTime(BattleState attacker, BattleState defender){
        int damage = 0;
        //Goes through the damage over time array. For each non 0 entry, increases the damage done by the
        //damage over time amount and decreases the duration of the stack by 1.
        for(int i = 0; i < defender.damageOverTime.length; i++){
            if(defender.damageOverTime[i] > 0){
                damage += attacker.active[ABILITY_MODULE.DOT];
                defender.damageOverTime[i]--;
            }
        }
        //Applies the damage and outputs a message
        defender.fighterHP -= damage;
        if( damage > 0) {
            simulationText.append(String.format("%s%s%s%d%s%d%s", "\n", defender.fighter.name, " takes ", damage,
                    " damage from damage over time. (", defender.fighterHP, ")\n"));
        }
    }

    //Internal class to manage battle state
    private class BattleState{
        //records the fighter's current HP
        int fighterHP;
        //records how long their active skill is active
        int duration;
        //records damage over time data
        int[] damageOverTime;
        //saves their fighter data
        Fighter fighter;
        //Checks if the fighter is doubling (used for duration checks)
        boolean doubles;
        //Creates an ability module for the attacker
        AbilityModule abilities;
        //Records their battle stats
        BattleStats stats;
        //keeps record of the modifiers from their passive and active skillss
        int[] active;
        int[] passive;
        //Constructor
        BattleState(Fighter fighter1, Fighter fighter2, boolean doubles){
            this.fighterHP = fighter1.hp();
            this.doubles = doubles;
            this.fighter = fighter1;
            this.abilities = new AbilityModule(fighter1, fighter2, fighterHP, fighter2.hp());
            this.stats = new BattleStats(fighter1, fighter2);
            active = Arrays.copyOf(ABILITY_MODULE.BASE, ABILITY_MODULE.BASE.length);
            passive = this.abilities.passiveCall();
            damageOverTime = new int[] {0,0,0,0,0};
        }
    }
}
