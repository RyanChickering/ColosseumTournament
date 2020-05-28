/*
Class that contains all the effects of the abilities.

 */

import java.util.Arrays;
import java.util.HashMap;

class AbilityModule {
    private Fighter attacker;
    private Fighter defender;
    private int atkHP;
    private int defHP;
    private int activation;
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
    final int[] BASE = {0,0,0,0,0,0,0,0,0,0};
    private HashMap<String, ActiveSkill> activeSkillMap = new HashMap<>();
    private HashMap<String, Skill> passiveSkillMap = new HashMap<>();

    int[] activeCall(){
        return activeSkillMap.get(attacker.abilities[ACTIVE].toLowerCase()).effect(attacker, defender);
    }

    int[] passiveCall(){
        return passiveSkillMap.get(attacker.abilities[PASSIVE].toLowerCase()).effect(attacker, defender);
    }

    int getActivation(){
        return activeSkillMap.get(attacker.abilities[ACTIVE].toLowerCase()).activationRate(attacker, defender);
    }

    int getDuration(){
        return activeSkillMap.get(attacker.abilities[ACTIVE].toLowerCase()).duration();
    }

    boolean canCrit(){
        return activeSkillMap.get(attacker.abilities[ACTIVE].toLowerCase()).canCrit();
    }

    AbilityModule(){
        buildActiveMap();
        buildPassiveMap();
    }

    AbilityModule(Fighter attacker, Fighter defender, int atkHP, int defHP){
        this.attacker = attacker;
        this.defender = defender;
        this.atkHP = atkHP;
        this.defHP = defHP;
        buildActiveMap();
        buildPassiveMap();
    }

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
    }
    
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
    }

    void setAttacker(Fighter attacker){
        this.attacker = attacker;
    }

    void setDefender(Fighter defender){
        this.defender = defender;
    }

    boolean onAttack(){
        return activeSkillMap.get(attacker.abilities[ACTIVE].toLowerCase()).onAttack();
    }


    String skillDesc(String skillname){
        Skill skill = activeSkillMap.get(skillname.toLowerCase());
        if(skill == null){
            return passiveSkillMap.get(skillname.toLowerCase()).desc();
        }
        return skill.desc();
    }

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
            return "Skill*2 percent chance to\n" +
                    "recover damage equal to half\n" +
                    "of the damage dealt.";
        }

        public boolean canCrit(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            activation = attacker.skl()*2;
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
            return "Skill percent chance to gain 15 hit\n" +
                    "and 10 attack for a whole round";
        }

        public boolean canCrit(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            activation = attacker.skl();
            out[DAMAGEUP] = 10;
            out[HITUP] = 15;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.skl();
        }
    }

    class Luna implements ActiveSkill{
        Luna(){
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
            return "Skill*2 chance to inflict\n" +
                    "bonus damage equal to half of\n" +
                    "the opponent's defense";
        }

        public boolean canCrit(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            activation = attacker.skl()*2;
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
            return "Str * 1.5 chance to deal\n" +
                    "bonus damage equal to str/2";
        }

        public boolean canCrit(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            activation = attacker.str()*15/10;
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
            return "Str percent chance to return\n" +
                    "half the damage dealt";
        }

        public boolean canCrit(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            activation = attacker.str();
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

        Corona(String name){
            this.name = name;
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
            activation = (attacker.str()*3)/2;
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

        Miracle(String name){
            this.name = name;
        }

        public String name(){
            return name;
        }

        public String desc(){
            return "Chance to survive mortal damage with 1 health";
        }

        public boolean canCrit(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            activation = (attacker.hp() - atkHP)/2;
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
            return "Skill*2 percent chance to deal\n" +
                    "15% of the opponent's health";
        }

        public boolean canCrit(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            activation = attacker.skl()*2;
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
            return "Luck% chance to revive \n" +
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
            return attacker.luc();
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
            return "Def% chance to block all incoming\n" +
                    "damage.";
        }

        public boolean canCrit(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEREDUCTION] = 100000;
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
            return "Skill/2% chance to halve the\n" +
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
            return false;
        }

        public int duration(){
            return 3;
        }

        public String name(){
            return "Poisoned Blade";
        }

        public String desc(){
            return "Skill*2 chance to inflict\n" +
                    "a poison stack on the enemy.";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DOT] = defender.hp()/16;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return defender.skl()*2;
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
            return "Skill*2% chance to\n" +
                    "increase attack by 20% of \n" +
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

    //Passives beyond

    class Patience implements Skill{
        Patience(){
        }

        public String name(){
            return "Patience";
        }

        public String desc(){
            return "If this unit's speed is lower\n Than" +
                    "the opponent's, hit and avoid + 20";
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
            return "Decrease damage by 4 and increase\n" +
                    "critical by 25";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            activation = 100;
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
            return "Decreases hit by 25 and increases\n" +
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
            return "Increases damage by 1/4 of str\n" +
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
            return "Increases dodge and avoid by 20\n" +
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
            return "Increases hit by 5 for each point\n" +
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
            return "Increases strength, skill, and speed\n" +
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
            return "Reduces damage based on difference\n" +
                    "in speed. Increases attack against\n" +
                    "opponents you outspeed.";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[DAMAGEREDUCTION] = ((defender.spd() - attacker.spd())*5)/2;
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
            return "Increases the damage done \n" +
                    "by criticals";
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = Arrays.copyOf(BASE, BASE.length);
            out[AVOIDUP] = 20;
            return out;
        }
    }


}
