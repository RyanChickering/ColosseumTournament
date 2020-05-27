/*
Class that contains all the effects of the abilities.

 */

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
    8.
    9. Extra spaces for expansion
     */
    final int DAMAGEUP = 0;
    final int HITUP = 1;
    final int CRITUP = 2;
    final int BONUSHEALING = 3;
    final int AVOIDUP = 4;
    final int SELFDAMAGE = 5;
    final int DAMAGEREDUCTION = 6;
    final int BONUSHP = 7;
    final int[] BASE = {0,0,0,0,0,0,0,0,0,0};

    int[] activeCall(){
        String ability = attacker.abilities[0];
        switch(ability){
            case "Sol":
                return sol();
            case "Luna":
                return luna();
            case "Colossus":
                return colossus();
            case "Flare":
                return colossus();
            case "Counter":
                return counter();
                default:
                    return BASE;
        }
    }
    int[] passiveCall(){
        String ability = attacker.abilities[1];
        switch(ability) {
            case "Patience":
                return patience();
            case "Veteran":
                return veteran();
            case "HP+":
                return hpup();
            case "Gamble":
                return gamble();
            default:
                return BASE;
        }
    }
    AbilityModule(Fighter attacker, Fighter defender, int atkHP, int defHP){
        phase = 0;
        this.attacker = attacker;
        this.defender = defender;
        this.atkHP = atkHP;
        this.defHP = defHP;
    }

    private int[] sol(){
        int[] out = BASE;
        activation = attacker.skl()*2;
        out[BONUSHEALING] = 50;
        return out;
    }

    private int[] luna(){
        int[] out = BASE;
        activation = attacker.skl()*2;
        out[DAMAGEUP] = defender.def()/2;
        return out;
    }

    private int[] colossus(){
        int[] out = BASE;
        activation = attacker.str()*15/10;
        out[DAMAGEUP] = attacker.str()/2;
        return out;
    }

    private int[] counter(){
        int[] out = BASE;
        phase = 1;
        activation = attacker.str();
        out[DAMAGEUP] = (defender.str() + defender.weapon.mt())/2;
        return out;
    }

    private int[] corona(){
        int[] out = BASE;
        phase = 1;
        activation = (attacker.str()*3)/2;
        out[AVOIDUP] = 30;
        return out;
    }

    private int[] miracle(){
        int[] out = BASE;
        activation = (attacker.hp() - atkHP)/2;
        out[DAMAGEREDUCTION] = defender.str()+defender.weapon.mt()-attacker.def() - 1;
        return out;
    }

    //Passives beyond

    //If speed is lower than opponent, increase hit and avoid by 20
    private int[] patience(){
        int[] out = BASE;
        if(attacker.spd() < defender.spd()) {
            out[HITUP] = 20;
            out[AVOIDUP] = 20;
        }
        return out;
    }

    //Decrease damage by 4 and increase crit by 25
    private int[] veteran(){
        activation = 100;
        int[] out = BASE;
        out[DAMAGEUP] = -4;
        out[CRITUP] = 25;
        return out;
    }

    //Decrease hit by 25 and increase crit by 25
    private int[] gamble(){
        int[] out = BASE;
        out[HITUP] = -25;
        out[CRITUP] = 25;
        return out;
    }

    //Increases HP by 25
    private int[] hpup(){
        int[] out = BASE;
        out[BONUSHP] = 25;
        //Just include the bonus HP in your build please.
        return out;
    }

    //Adds 1/4 damage at the cost of 1/8th of own health per attack
    private int[] sacrifice() {
        int[] out = BASE;
        out[DAMAGEUP] = attacker.str() / 4;
        out[SELFDAMAGE] = attacker.hp() / 8;
        return out;
    }

    //Increases crit by 15
    private int[] critup(){
        int[] out = BASE;
        out[CRITUP] = 15;
        return out;
    }

    //Heals 1/20th of health per turn
    private int[] renewal(){
        int[] out = BASE;
        out[BONUSHEALING] = attacker.hp()/20;
        return out;
    }

    //Provides a portion of luck as defense
    private int[] shieldOfFaith(){
        int[] out = BASE;
        out[DAMAGEREDUCTION] = attacker.luc();
        return out;
    }

}
