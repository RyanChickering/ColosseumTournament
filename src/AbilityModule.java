/* Class that contains all the effects of the abilities.
 * Creates hash maps of abilities so that information about the
 * skills can be pulled.
 */

import java.util.Arrays;
import java.util.HashMap;

class AbilityModule {
    private Fighter attacker;
    private Fighter defender;
    int atkHP;
    int defHP;
    //returns an array of values of possible things that are effected
    /* key:
    0. bonus damage
    1. bonus hit
    2. bonus crit
    3. bonus healing, (for actives: as a percentage (0-100)), (for passives, calculate full return in the skill)
    4. bonus avo
    5. self inflicted damage
    6. bonus defense
    7. bonus HP
    8. DDGUP
    9. Damage over time
    10. Extra attack
     */
    final int DAMAGEUP = 0;
    final int HITUP = 1;
    final int CRITUP = 2;
    final int BONUSHEALING = 3;
    final int AVOIDUP = 4;
    final int SELFDAMAGE = 5;
    final int DAMAGEREDUCTION = 6;
    final int ACTIVE = 0;
    final int PASSIVE = 1;
    final int BONUSHP = 7;
    final int DDGUP = 8;
    final int DOT = 9;
    final int EXTRA_ATTACK = 10;
    private final int infinity = 100000;
    final int[] BASE = {0,0,0,0,0,0,0,0,0,0,0};
    private HashMap<String, ActiveSkill> activeSkillMap = new HashMap<>();
    private HashMap<String, Skill> passiveSkillMap = new HashMap<>();

    //Returns the modifiers of the attacker's active skill
    int[] activeCall(){
        return activeSkillMap.get(attacker.abilities[ACTIVE].toLowerCase()).effect(attacker, defender);
    }

    //Returns modifiers of the attacker's passive skill
    int[] passiveCall(){
        return passiveSkillMap.get(attacker.abilities[PASSIVE].toLowerCase()).effect(attacker, defender);
    }

    //Returns modifiers of a passive skill based on a skillname
    int[] passiveCall(String skillname){
        try {
            return passiveSkillMap.get(skillname.toLowerCase()).effect(attacker, defender);
        } catch(Exception e){
            return BASE;
        }
    }

    //Returns the activation rate of the attacker's active skill
    int getActivation(){
        return activeSkillMap.get(attacker.abilities[ACTIVE].toLowerCase()).activationRate(attacker, defender);
    }

    //Returns the duration of the attacker's active skill
    int getDuration(){
        return activeSkillMap.get(attacker.abilities[ACTIVE].toLowerCase()).duration();
    }

    //Returns whether or not the attacker's active skill can land a critical hit
    boolean canCrit(){
        return activeSkillMap.get(attacker.abilities[ACTIVE].toLowerCase()).canCrit();
    }

    //Method that returns whether an active skill should be processed while attacking or defending
    boolean onAttack(){
        return activeSkillMap.get(attacker.abilities[ACTIVE].toLowerCase()).onAttack();
    }

    //Set the fighter to be used as the attacker
    void setAttacker(Fighter fighter){
        attacker = fighter;
    }

    //Set the figher to be used as the defender
    void setDefender(Fighter fighter){
        defender = fighter;
    }

    //Returns the description of a skill given the name
    String skillDesc(String skillname){
        Skill skill = activeSkillMap.get(skillname.toLowerCase());
        if(skill == null){
            return passiveSkillMap.get(skillname.toLowerCase()).desc();
        }
        return skill.desc();
    }

    //Default constructor
    AbilityModule(){
        //Builds the hash maps for active and passive skills
        buildActiveMap();
        buildPassiveMap();
    }

    //Constructor with an attacker and defender
    AbilityModule(Fighter attacker, Fighter defender, int atkHP, int defHP){
        //sets internal values
        this.attacker = attacker;
        this.defender = defender;
        this.atkHP = atkHP;
        this.defHP = defHP;
        //Builds the hash maps for active and passive skills
        buildActiveMap();
        buildPassiveMap();
    }

    //Method to build the hash map of active skills
    private void buildActiveMap(){
        
        activeSkillMap.put("luna", new Luna());
        activeSkillMap.put("sol", new Sol());
        activeSkillMap.put("colossus", new Colossus());
        activeSkillMap.put("counter", new Counter());
        activeSkillMap.put("corona", new Corona());
        activeSkillMap.put("miracle", new Miracle());
        activeSkillMap.put("guts", new Guts());
        activeSkillMap.put("slayer", new Slayer());
        activeSkillMap.put("pavise", new Pavise());
        activeSkillMap.put("savageblow", new SavageBlow());
        activeSkillMap.put("bonfire", new Bonfire());
        activeSkillMap.put("lethality", new Lethality());
        activeSkillMap.put("poisonedblade", new PoisonedBlade());
        activeSkillMap.put("pierce", new Luna("Pierce"));
        activeSkillMap.put("deadeye", new Deadeye());
        activeSkillMap.put("sureshot", new SureShot());
        activeSkillMap.put("adept", new Adept());
        activeSkillMap.put("starstorm", new StarStorm());
        activeSkillMap.put("flare", new Colossus("Flare"));
        activeSkillMap.put("inferno", new Inferno());
    }

    //Method to build the hash map of passive skills
    private void buildPassiveMap(){
        passiveSkillMap.put("renewal", new Renewal());
        passiveSkillMap.put("patience", new Patience());
        passiveSkillMap.put("veteran", new Veteran());
        passiveSkillMap.put("gamble", new Gamble());
        passiveSkillMap.put("hp+", new HPUP());
        passiveSkillMap.put("sacrifice", new Sacrifice());
        passiveSkillMap.put("crit+", new CritUp());
        passiveSkillMap.put("shield of faith", new ShieldOfFaith());
        passiveSkillMap.put("axefaire", new Axefaire());
        passiveSkillMap.put("panicdodge", new PanicDodge());
        passiveSkillMap.put("resolve", new Resolve());
        passiveSkillMap.put("waryfighter", new WaryFighter());
        passiveSkillMap.put("avoid+", new AvoidUp());
        passiveSkillMap.put("precision", new Precision());
        passiveSkillMap.put("empoweredmagic", new EmpoweredMagic());
        passiveSkillMap.put("eagleeye", new EagleEye());
    }

    /* ACTIVE skills:
     * Creating an active skill:
     * onAttack(): Indicates if the skill activates while attacking or defending. True is on attack,
     *      false is on defense
     * duration(): Indicates how long the skill lasts. 0 is one attack, 1 is one round, 2 is two rounds etc.
     * canCrit(): Indicates if a skill can critical. true it can, false it can't.
     * name(): Returns the name of the skill
     * desc(): returns the description of the skill
     * effect(Fighter, Fighter): returns an array where the fields modified by the skill are modified
     * activationRate(Fighter, Fighter): returns the activation rate of a skill
     *
     * Don't forget to add the new skill to its respective map
     */

    class Sol implements ActiveSkill{
        Sol(){
        }

        public boolean onAttack(){
            return true;
        }

        public int duration(){
            return 0;
        }

        public String name(){
            return "Sol";
        }

        public String desc(){
            return "Skill*2 percent chance to " +
                    "recover damage equal to half " +
                    "of the damage dealt.";
        }

        public boolean canCrit(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[BONUSHEALING] = 50;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.skl()*2;
        }
    }

    class Slayer implements ActiveSkill{
        Slayer(){
        }

        public boolean onAttack(){
            return true;
        }

        public int duration(){
            return 1;
        }

        public String name(){
            return "Slayer";
        }

        public String desc(){
            return "Skill percent chance to gain 15 hit " +
                    "and 10 attack for a whole round";
        }

        public boolean canCrit(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEUP] = 10;
            out[HITUP] = 15;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.skl();
        }
    }

    class Luna implements ActiveSkill{
        String name = "Luna";
        Luna(){
        }

        Luna(String name){
            this.name = name;
        }

        public boolean onAttack(){
            return true;
        }

        public int duration(){
            return 0;
        }

        public String name(){
            return "Luna";
        }

        public String desc(){
            return "Skill*2 chance to inflict " +
                    "bonus damage equal to half of " +
                    "the opponent's defense";
        }

        public boolean canCrit(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEUP] = defender.def()/2;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.skl()*2;
        }
    }

    class Colossus implements ActiveSkill{
        String name = "Colossus";
        Colossus(){

        }

        public int duration(){
            return 0;
        }

        public boolean onAttack(){
            return true;
        }

        Colossus(String name){
            this.name = name;
        }

        public String name(){
            return name;
        }

        public String desc(){
            return "Str * 1.5 chance to deal " +
                    "bonus damage equal to str/2";
        }

        public boolean canCrit(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEUP] = attacker.str()/2;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.str()*15/10;
        }
    }

    class Counter implements ActiveSkill{
        Counter(){

        }

        public int duration(){
            return 0;
        }

        public boolean onAttack(){
            return false;
        }

        public String name(){
            return "Counter";
        }

        public String desc(){
            return "Str percent chance to return " +
                    "half the damage dealt";
        }

        public boolean canCrit(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEUP] = (defender.str() + defender.weapon.mt())/2;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.str();
        }
    }

    class Corona implements ActiveSkill{
        String name = "Corona";
        Corona(){

        }

        public int duration(){
            return 1;
        }

        public boolean onAttack(){
            return true;
        }

        public String name(){
            return name;
        }

        public String desc(){
            return "Str*1.5 chance to increase avoid by 30";
        }

        public boolean canCrit(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[AVOIDUP] = 30;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.str()*3/2;
        }
    }

    class Miracle implements ActiveSkill{
        String name = "Miracle";
        Miracle(){

        }

        public boolean onAttack(){
            return false;
        }

        public int duration(){
            return 0;
        }

        public String name(){
            return name;
        }

        public String desc(){
            return "Chance to reduce incoming damage to 1";
        }

        public boolean canCrit(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEREDUCTION] = defender.str()+defender.weapon.mt()-attacker.def() - 1;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return (attacker.hp() - atkHP)/2;
        }
    }

    class SavageBlow implements ActiveSkill{
        SavageBlow(){
        }

        public boolean onAttack(){
            return true;
        }

        public int duration(){
            return 1;
        }

        public String name(){
            return "SavageBlow";
        }

        public String desc(){
            return "Skill*2 percent chance to deal " +
                    "15% of the opponent's health";
        }

        public boolean canCrit(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            BattleStats stats = new BattleStats(attacker, defender, 1);
            if(stats.power < (defHP*23)/20){
                out[DAMAGEUP] = (defHP*23)/20 - stats.power;
            }
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.skl()*2;
        }
    }

    class Guts implements ActiveSkill{
        Guts(){
        }

        public boolean onAttack(){
            return false;
        }

        public int duration(){
            return 0;
        }

        public String name(){
            return "Guts";
        }

        public String desc(){
            return "Luck% chance to revive  " +
                    "with 20% of max HP";
        }

        public boolean canCrit(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[BONUSHP] = attacker.hp()/5;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return (attacker.luc()*3)/2;
        }
    }

    class Pavise implements ActiveSkill{
        Pavise(){
        }

        public boolean onAttack(){
            return false;
        }

        public int duration(){
            return 0;
        }

        public String name(){
            return "Pavise";
        }

        public String desc(){
            return "Def% chance to block all incoming " +
                    "damage.";
        }

        public boolean canCrit(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEREDUCTION] = infinity;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.def();
        }
    }

    class Lethality implements ActiveSkill{
        Lethality(){
        }

        public boolean onAttack(){
            return true;
        }

        public boolean canCrit(){
            return false;
        }

        public int duration(){
            return 0;
        }

        public String name(){
            return "Lethality";
        }

        public String desc(){
            return "Skill/2% chance to halve the " +
                    "enemy's health.";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            BattleStats battleStats = new BattleStats(attacker, defender);
            if(defHP/2 > battleStats.power) {
                out[DAMAGEUP] = defHP / 2 - battleStats.power;
            } else if(battleStats.power == 0){
                out[DAMAGEUP] = 1;
            }
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return defender.skl()/2;
        }
    }

    class PoisonedBlade implements ActiveSkill{
        PoisonedBlade(){
        }

        public boolean onAttack(){
            return true;
        }

        public boolean canCrit(){
            return true;
        }

        public int duration(){
            return 3;
        }

        public String name(){
            return "Poisoned Blade";
        }

        public String desc(){
            return "Skill*3/2 chance to inflict " +
                    "a poison stack on the enemy.";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DOT] = defender.hp()/16;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return (defender.skl()*3)/2;
        }
    }

    class Bonfire implements ActiveSkill{
        Bonfire(){
        }

        public boolean onAttack(){
            return true;
        }

        public int duration(){
            return 0;
        }

        public String name(){
            return "Bonfire";
        }

        public String desc(){
            return "Skill*2% chance to " +
                    "increase attack by 20% of  " +
                    "defense";
        }

        public boolean canCrit(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEUP] = attacker.def()/5;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.skl()*2;
        }
    }

    class Deadeye implements ActiveSkill{
        Deadeye(){
        }

        public boolean onAttack(){
            return true;
        }

        public int duration(){
            return 0;
        }

        public String name(){
            return "Deadeye";
        }

        public String desc(){
            return "Skill*2% chance to " +
                    "increase hit and " +
                    "critical by 20";
        }

        public boolean canCrit(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[CRITUP] = 20;
            out[HITUP] = 20;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.skl()*2;
        }
    }

    class SureShot implements ActiveSkill{
        SureShot(){
        }

        public boolean onAttack(){
            return true;
        }

        public int duration(){
            return 0;
        }

        public String name(){
            return "SureShot";
        }

        public String desc(){
            return "Skill% chance that the " +
                    "next attack will " +
                    "never miss and deal " +
                    "20% bonus damae";
        }

        public boolean canCrit(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[HITUP] = infinity;
            BattleStats stats = new BattleStats(attacker, defender);
            out[DAMAGEUP] = stats.power/5;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.skl()*2;
        }
    }

    class Adept implements ActiveSkill {
        Adept() {
        }

        public boolean onAttack() {
            return true;
        }

        public int duration() {
            return 1;
        }

        public String name() {
            return "Adept";
        }

        public String desc() {
            return "Skill% chance to gain a " +
                    "bonus followup attack.";
        }

        public boolean canCrit() {
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender) {
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[EXTRA_ATTACK] = 1;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender) {
            return attacker.skl();
        }
    }

    class StarStorm implements ActiveSkill {
        StarStorm() {
        }

        public boolean onAttack() {
            return true;
        }

        public int duration() {
            return 4;
        }

        public String name() {
            return "Star Storm";
        }

        public String desc() {
            return "Skill% chance to perform " +
                    "a flurry of 5 attacks";
        }

        public boolean canCrit() {
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender) {
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[EXTRA_ATTACK] = 4;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender) {
            return attacker.skl();
        }
    }

    class Inferno implements ActiveSkill {
        Inferno() {
        }

        public boolean onAttack() {
            return true;
        }

        public int duration() {
            return 5;
        }

        public String name() {
            return "Inferno";
        }

        public String desc() {
            return "Skill% chance to increase" +
                    "damage by 15% and inflict a 5" +
                    "turn burn.";
        }

        public boolean canCrit() {
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender) {
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEUP] = (attacker.str()*3)/20;
            out[DOT] = defender.hp()/20;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender) {
            return attacker.skl();
        }
    }

    /* PASSIVE skills
     * Creating a passive skill:
     * name(): returns the name of the skill
     * desc(): returns the description of the skill
     * effect(Fighter, Fighter): returns an array where the fields modified by the skill are modified
     *
     * Don't forget to add the new skill to its respective map
     */

    class Patience implements Skill{
        Patience(){
        }

        public String name(){
            return "Patience";
        }

        public String desc(){
            return "If this unit's speed is lower than" +
                    " the opponent's, hit and avoid + 20";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            if(attacker.spd() < defender.spd()) {
                out[HITUP] = 20;
                out[AVOIDUP] = 20;
            }
            return out;
        }
    }

    class Veteran implements Skill{
        Veteran(){
        }

        public String name(){
            return "Veteran";
        }

        public String desc(){
            return "Decrease damage by 4 and increase " +
                    "critical by 25";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEUP] = -4;
            out[CRITUP] = 25;
            return out;
        }
    }

    class Gamble implements Skill{
        String name = "Gamble";
        Gamble(){

        }

        public String name(){
            return name;
        }

        public String desc(){
            return "Decreases hit by 25 and increases " +
                    "critical by 25";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[HITUP] = -25;
            out[CRITUP] = 25;
            return out;
        }
    }

    class HPUP implements Skill{
        String name = "HP+";
        HPUP(){

        }

        public String name(){
            return name;
        }

        public String desc(){
            return "Increases HP by 25";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[BONUSHP] = 25;
            return out;
        }
    }

    class Sacrifice implements Skill{
        String name = "Sacrifice";
        Sacrifice(){

        }

        public String name(){
            return name;
        }

        public String desc(){
            return "Increases damage by 1/4 of str " +
                    "at the cost of 1/8 HP each turn";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEUP] = attacker.str() / 4;
            out[SELFDAMAGE] = attacker.hp() / 8;
            return out;
        }
    }

    class CritUp implements Skill{
        String name = "Crit+";
        CritUp(){

        }

        public String name(){
            return name;
        }

        public String desc(){
            return "Increases critical by 15";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[CRITUP] = 15;
            return out;
        }
    }

    class Renewal implements Skill{
        String name = "Renewal";
        Renewal(){

        }
        
        public String name(){
            return name;
        }

        public String desc(){
            return "Heals 1/10 HP every turn";
        }

        public int[] effect(Fighter attacker, Fighter defender) {
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[BONUSHEALING] = attacker.hp() / 10;
            return out;
        }
    }

    class ShieldOfFaith implements Skill{
        String name = "ShieldOfFaith";
        ShieldOfFaith(){

        }

        public String name(){
            return name;
        }

        public String desc(){
            return "Reduces damage based on luck";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEREDUCTION] = attacker.luc();
            return out;
        }
    }

    class PanicDodge implements Skill{
        PanicDodge(){
        }
        
        public String name(){
            return "Panic Dodge";
        }

        public String desc(){
            return "Increases dodge and avoid by 20 " +
                    "when under 33% health";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            if(atkHP < attacker.hp()/3) {
                out[DDGUP] = 20;
                out[AVOIDUP] = 20;
            }
            return out;
        }
    }

    class Axefaire implements Skill{
        Axefaire(){
        }

        public String name(){
            return "Axefaire";
        }

        public String desc(){
            return "Increases hit by 5 for each point " +
                    "invested in strength";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[HITUP] = attacker.str()*5 - 15;
            return out;
        }
    }

    class Resolve implements Skill{
        Resolve(){
        }
        
        public String name(){
            return "Resolve";
        }

        public String desc(){
            return "Increases strength, skill, and speed " +
                    "by 5 while under 50% HP.";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            if(atkHP < attacker.hp()/2) {
                out[DAMAGEUP] = 5;
                out[HITUP] = 10;
                out[CRITUP] = 2;
                out[AVOIDUP] = 10;
            }
            return out;
        }
    }

    class WaryFighter implements Skill{
        WaryFighter(){
        }

        public String name(){
            return "Wary Fighter";
        }

        public String desc(){
            return "Reduces damage based on difference " +
                    "in speed. Increases attack against " +
                    "opponents you outspeed.";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEREDUCTION] = (defender.str()*(defender.spd() - attacker.spd())/2)+1;
            if(out[DAMAGEREDUCTION] < 0){
                out[DAMAGEREDUCTION] = 0;
                out[DAMAGEUP] = (attacker.str()*23)/20;
            }
            return out;
        }
    }

    class AvoidUp implements Skill{
        AvoidUp(){
        }
        
        public String name(){
            return "Avoid+";
        }

        public String desc(){
            return "Increases avoid by 20";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[AVOIDUP] = 20;
            return out;
        }
    }

    class Precision implements Skill{
        Precision(){
        }

        public String name(){
            return "Precision";
        }

        public String desc(){
            return "Increases the damage done  " +
                    "by criticals";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            return Arrays.copyOf(BASE, BASE.length);
        }
    }

    class EagleEye implements Skill{
        EagleEye(){
        }

        public String name(){
            return "Eagle Eye";
        }

        public String desc(){
            return "Increases hit by 10, " +
                    "critical by 10, and " +
                    "power by 1.";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEUP] = 1;
            out[HITUP] = 10;
            out[CRITUP] = 10;
            return out;
        }
    }

    class EmpoweredMagic implements Skill{
        EmpoweredMagic(){
        }

        public String name(){
            return "Empowered Magic";
        }

        public String desc(){
            return "Increases damage at " +
                    "the cost of health";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEUP] = attacker.str()/3;
            if(atkHP-out[DAMAGEUP] > attacker.str()/5) {
                out[SELFDAMAGE] = attacker.str() / 5;
            }
            return out;
        }
    }




}
