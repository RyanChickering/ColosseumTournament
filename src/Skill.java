interface Skill {
    String name();
    String desc();
    int[] effect(Fighter f1, Fighter f2);
}

interface ActiveSkill extends Skill {
    boolean canCrit();
    boolean onAttack();
    int activationRate(Fighter f1, Fighter f2);
    int duration();
}

interface PassiveSkill extends Skill {

}