/* Class that converts a fighter's stats into stats processable by the fightSim
 */

public class BattleStats {
    int power;
    int hit;
    int crit;
    int as;

    BattleStats(Fighter attacker, Fighter defender, int one){
        if(attacker.weapon.type == 0) {
            this.power = attacker.str() + attacker.weapon.wstats[0] - defender.def();
        } else {
            this.power = attacker.str() + attacker.weapon.wstats[0] - defender.res();
        }
        AbilityModule attackerAbilities = new AbilityModule(attacker, defender, attacker.hp(), defender.hp());
        AbilityModule defenderAbilities = new AbilityModule(defender, attacker, defender.hp(), attacker.hp());

        int[] modifiers = attackerAbilities.passiveCall();
        int[] defenderModifiers = defenderAbilities.passiveCall();
        power += modifiers[attackerAbilities.DAMAGEUP]
                - defenderModifiers[defenderAbilities.DAMAGEREDUCTION];
        hit = attacker.weapon.hit() + attacker.skl()*2 + modifiers[attackerAbilities.HITUP]
                - defender.spd()*2 - defender.luc()  - defenderModifiers[defenderAbilities.AVOIDUP];
        crit = attacker.weapon.crit() + attacker.skl()/2 + modifiers[attackerAbilities.CRITUP]
                - defender.luc() + defenderModifiers[defenderAbilities.DDGUP];
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

    BattleStats(Fighter attacker, Fighter defender, AbilityModule attackerAbilities, AbilityModule defenderAbilities){
        if(attacker.weapon.type == 0) {
            this.power = attacker.str() + attacker.weapon.wstats[0] - defender.def();
        } else {
            this.power = attacker.str() + attacker.weapon.wstats[0] - defender.res();
        }

        int[] modifiers = attackerAbilities.passiveCall();
        int[] defenderModifiers = defenderAbilities.passiveCall();
        power += modifiers[attackerAbilities.DAMAGEUP]
                - defenderModifiers[defenderAbilities.DAMAGEREDUCTION];
        hit = attacker.weapon.hit() + attacker.skl()*2 + modifiers[attackerAbilities.HITUP]
                - defender.spd()*2 - defender.luc()  - defenderModifiers[defenderAbilities.AVOIDUP];
        crit = attacker.weapon.crit() + attacker.skl()/2 + modifiers[attackerAbilities.CRITUP]
                - defender.luc() + defenderModifiers[defenderAbilities.DDGUP];
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

    BattleStats(Fighter attacker, Fighter defender){
        if(attacker.weapon.type == 0) {
            this.power = attacker.str() + attacker.weapon.wstats[0] - defender.def();
        } else {
            this.power = attacker.str() + attacker.weapon.wstats[0] - defender.res();
        }
        hit = attacker.weapon.hit() + attacker.skl()*2
                - defender.spd()*2 - defender.luc();
        crit = attacker.weapon.crit() + attacker.skl()/2
                - defender.luc();
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
