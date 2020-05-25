/* Class that converts a fighter's stats into stats processable by the fightSim
 */

public class BattleStats {
    public int power;
    public int hit;
    public int crit;
    public int as;
    int[] modifiers;
    public BattleStats(Fighter attacker, Fighter defender){
        if(attacker.weapon.type == 0) {
            this.power = attacker.str() + attacker.weapon.wstats[0] - defender.def();
        } else {
            this.power = attacker.str() + attacker.weapon.wstats[0] - defender.res();
        }
        int avoid = 0;
        AbilityModule ability = new AbilityModule(attacker, defender, attacker.hp(), defender.hp());
        if(defender.abilities[1].equals("Patience")){
            avoid = 0;
        }
        modifiers = ability.passiveCall();
        power += modifiers[0];
        hit = attacker.weapon.hit() + attacker.skl()*2 - defender.spd()*2 - defender.luc() + modifiers[1] - avoid;
        crit = attacker.weapon.crit() + attacker.skl()/2 - defender.luc() + modifiers[2];
        as = attacker.spd()-defender.spd();
        if(power < 0){
            power = 0;
        }
        if(hit < 0){
            hit = 0;
        }
        if(crit < 0){
            crit = 0;
        }
    }

    @Override
    public String toString() {
        return  "MT:" + power + "\nHit:" + hit + "\nCrit" + crit;
    }
}