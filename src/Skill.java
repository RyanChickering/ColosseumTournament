interface Skill {
    String name();
    String desc();
    boolean canCrit();
    int[] effect(Fighter f1, Fighter f2);
    int activationRate(Fighter f1, Fighter f2);
    boolean onAttack();
    int duration();
}
