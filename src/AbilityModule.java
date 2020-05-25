public class AbilityModule {
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
    7.
    8.
    9. Extra spaces for expansion
     */
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
                    return new int[] {0,0,0,0,0,0,0,0,0,0};
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
                return new int[] {0,0,0,0,0,0,0,0,0,0};
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
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        activation = attacker.skl()*2;
        out[3] = 50;
        return out;
    }

    private int[] luna(){
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        activation = attacker.skl()*2;
        out[0] = defender.def()/2;
        return out;
    }

    private int[] colossus(){
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        activation = attacker.str()*15/10;
        out[0] = attacker.str()/2;
        return out;
    }

    private int[] counter(){
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        phase = 1;
        activation = attacker.str();
        out[0] = (defender.str() + defender.weapon.mt())/2;
        return out;
    }

    private int[] corona(){
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        phase = 1;
        activation = (attacker.str()*3)/2;
        out[4] = 30;
        return out;
    }

    private int[] miracle(){
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        activation = (attacker.hp() - atkHP)/2;
        out[6] = defender.str()+defender.weapon.mt()-attacker.def() - 1;
        return out;
    }

    //Passives beyond

    private int[] patience(){
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        if(attacker.spd() < defender.spd()) {
            out[1] = 20;
            out[4] = 20;
        }
        return out;
    }
    private int[] veteran(){
        activation = 100;
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        out[0] = -4;
        out[2] = 25;
        return out;
    }
    private int[] gamble(){
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        out[1] = -25;
        out[2] = 25;
        return out;
    }
    private int[] hpup(){
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        //Just include the bonus HP in your build please.
        return out;
    }

    private int[] sacrifice() {
        int[] out = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        out[0] = attacker.str() / 4;
        out[5] = attacker.hp() / 8;
        return out;
    }

    private int[] critup(){
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        out[2] = 15;
        return out;
    }

    private int[] renewal(){
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        out[3] = attacker.hp()/20;
        return out;
    }

    private int[] shieldOfFaith(){
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        out[4] = attacker.luc();
        return out;
    }

}
