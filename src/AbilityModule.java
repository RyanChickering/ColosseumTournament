/*
Class that contains all the effects of the abilities.

 */

import java.util.HashMap;

class AbilityModule {
    Fighter attacker;
    Fighter defender;
    int atkHP;
    int defHP;
    int activation;
    int phase = 0;
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
    9.
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
    final int[] BASE = {0,0,0,0,0,0,0,0,0,0};
    private HashMap<String, Skill> skillMap = new HashMap<>();

    int[] activeCall(){
        return skillMap.get(attacker.abilities[ACTIVE]).effect(attacker, defender);
    }

    int[] passiveCall(){
        return skillMap.get(attacker.abilities[PASSIVE]).effect(attacker, defender);
    }

    int getActivation(){
        return skillMap.get(attacker.abilities[ACTIVE]).activationRate(attacker, defender);
    }

    int getDuration(){
        return skillMap.get(attacker.abilities[ACTIVE]).duration();
    }

    AbilityModule(){
        skillMap.put("Patience", new Patience());
        skillMap.put("Luna", new Luna());
        skillMap.put("Sol", new Sol());
        skillMap.put("Colossus", new Colossus());
        skillMap.put("Counter", new Counter());
        skillMap.put("Corona", new Corona());
        skillMap.put("Miracle", new Miracle());
        skillMap.put("Veteran", new Veteran());
        skillMap.put("Gamble", new Gamble());
        skillMap.put("HP+", new HPUP());
        skillMap.put("Sacrifice", new Sacrifice());
        skillMap.put("Crit+", new CritUp());
        skillMap.put("Renewal", new Renewal());
        skillMap.put("Shield of Faith", new ShieldOfFaith());
    }

    AbilityModule(Fighter attacker, Fighter defender, int atkHP, int defHP){
        phase = 0;
        this.attacker = attacker;
        this.defender = defender;
        this.atkHP = atkHP;
        this.defHP = defHP;
    }

    void setAttacker(Fighter attacker){
        this.attacker = attacker;
    }

    void setDefender(Fighter defender){
        this.defender = defender;
    }

    boolean onAttack(){
        return skillMap.get(attacker.abilities[0]).onAttack();
    }


    String skillDesc(String skillname){
        return skillMap.get(skillname).desc();
    }

    class Sol implements Skill{
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

        public boolean active(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = BASE;
            activation = attacker.skl()*2;
            out[BONUSHEALING] = 50;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.skl()*2;
        }
    }

    class Luna implements Skill{
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

        public boolean active(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = BASE;
            activation = attacker.skl()*2;
            out[DAMAGEUP] = defender.def()/2;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.skl()*2;
        }
    }

    class Colossus implements Skill{
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

        public boolean active(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = BASE;
            activation = attacker.str()*15/10;
            out[DAMAGEUP] = attacker.str()/2;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.str()*15/10;
        }
    }

    class Counter implements Skill{
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

        public boolean active(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = BASE;
            phase = 1;
            activation = attacker.str();
            out[DAMAGEUP] = (defender.str() + defender.weapon.mt())/2;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.str();
        }
    }

    class Corona implements Skill{
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

        public boolean active(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = BASE;
            phase = 1;
            activation = (attacker.str()*3)/2;
            out[AVOIDUP] = 30;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return attacker.str()*3/2;
        }
    }

    class Miracle implements Skill{
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

        public boolean active(){
            return true;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = BASE;
            activation = (attacker.hp() - atkHP)/2;
            out[DAMAGEREDUCTION] = defender.str()+defender.weapon.mt()-attacker.def() - 1;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return (attacker.hp() - atkHP)/2;
        }
    }

    //Passives beyond

    //If speed is lower than opponent, increase hit and avoid by 20
    class Patience implements Skill{
        Patience(){
        }

        public int duration(){
            return 0;
        }

        public boolean onAttack(){
            return false;
        }

        public String name(){
            return "Patience";
        }

        public String desc(){
            return "If this unit's speed is lower\n Than" +
                    "the opponent's, hit and avoid + 20";
        }

        public boolean active(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = BASE;
            if(attacker.spd() < defender.spd()) {
                out[HITUP] = 20;
                out[AVOIDUP] = 20;
            }
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return 100;
        }
    }

    class Veteran implements Skill{
        Veteran(){
        }

        public boolean onAttack(){
            return false;
        }

        public int duration(){
            return 0;
        }

        public String name(){
            return "Veteran";
        }

        public String desc(){
            return "Decrease damage by 4 and increase\n" +
                    "critical by 25";
        }

        public boolean active(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            activation = 100;
            int[] out = BASE;
            out[DAMAGEUP] = -4;
            out[CRITUP] = 25;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return 100;
        }
    }

    //Decrease hit by 25 and increase crit by 25
    class Gamble implements Skill{
        String name = "Gamble";
        Gamble(){

        }

        Gamble(String name){
            this.name = name;
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
            return "Decreases hit by 25 and increases\n" +
                    "critical by 25";
        }

        public boolean active(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = BASE;
            out[HITUP] = -25;
            out[CRITUP] = 25;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return 100;
        }
    }

    //Increases HP by 25
    class HPUP implements Skill{
        String name = "HP+";
        HPUP(){

        }

        HPUP(String name){
            this.name = name;
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
            return "Increases HP by 25";
        }

        public boolean active(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = BASE;
            out[BONUSHP] = 25;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return 100;
        }
    }

    class Sacrifice implements Skill{
        String name = "Sacrifice";
        Sacrifice(){

        }

        Sacrifice(String name){
            this.name = name;
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
            return "Increases damage by 1/4 of str\n" +
                    "at the cost of 1/8 HP each turn";
        }

        public boolean active(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = BASE;
            out[DAMAGEUP] = attacker.str() / 4;
            out[SELFDAMAGE] = attacker.hp() / 8;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return 100;
        }
    }

    class CritUp implements Skill{
        String name = "Crit+";
        CritUp(){

        }

        CritUp(String name){
            this.name = name;
        }

        public int duration(){
            return 0;
        }

        public boolean onAttack(){
            return false;
        }

        public String name(){
            return name;
        }

        public String desc(){
            return "Increases critical by 15";
        }

        public boolean active(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = BASE;
            out[CRITUP] = 15;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return 100;
        }
    }

    class Renewal implements Skill{
        String name = "Renewal";
        Renewal(){

        }

        public int duration(){
            return 0;
        }

        Renewal(String name){
            this.name = name;
        }

        public boolean onAttack(){
            return false;
        }

        public String name(){
            return name;
        }

        public String desc(){
            return "Heals 1/20 HP every turn";
        }

        public boolean active(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = BASE;
            out[BONUSHEALING] = attacker.hp()/20;
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return 100;
        }
    }

    class ShieldOfFaith implements Skill{
        String name = "ShieldOfFaith";
        ShieldOfFaith(){

        }

        public boolean onAttack(){
            return false;
        }

        public int duration(){
            return 0;
        }

        ShieldOfFaith(String name){
            this.name = name;
        }

        public String name(){
            return name;
        }

        public String desc(){
            return "Reduces damage based on luck";
        }

        public boolean active(){
            return false;
        }

        public int[] effect(Fighter attacker, Fighter defender){
            int[] out = BASE;
            out[DAMAGEREDUCTION] = attacker.luc();
            return out;
        }

        public int activationRate(Fighter attacker, Fighter defender){
            return 100;
        }
    }

}
