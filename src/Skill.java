/*
 * The base skill interface.
 * name(): returns the name of the skill
 * desc(): returns the description of the skill
 * effect(Fighter, Fighter): returns an array where the fields modified by the skill are modified
 */
interface Skill {
    String name();
    String desc();
    int[] effect(Fighter f1, Fighter f2);
}

/*
 * The modified ActiveSkill interface.
 * onAttack(): Indicates if the skill activates while attacking or defending. True is on attack,
 *      false is on defense
 * duration(): Indicates how long the skill lasts. 0 is one attack, 1 is one round, 2 is two rounds etc.
 * canCrit(): Indicates if a skill can critical. true it can, false it can't.
 * activationRate(Fighter, Fighter): returns the activation rate of a skill
 */
interface ActiveSkill extends Skill {
    boolean onAttack();
    int duration();
    boolean canCrit();
    int activationRate(Fighter f1, Fighter f2);
}