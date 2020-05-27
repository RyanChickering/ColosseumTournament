/* Class that converts a fighter's stats into stats processable by the fightSim
 */

public class BattleStats {
    int power;
    int hit;
    int crit;
    int as;
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
        power += modifiers[ability.DAMAGEUP];
        hit = attacker.weapon.hit() + attacker.skl()*2 - defender.spd()*2 - defender.luc() + modifiers[ability.HITUP] - avoid;
        crit = attacker.weapon.crit() + attacker.skl()/2 - defender.luc() + modifiers[ability.AVOIDUP];
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
