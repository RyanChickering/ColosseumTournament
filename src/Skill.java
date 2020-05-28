interface Skill {
    String name();
    String desc();
    boolean active();
    int[] effect(Fighter f1, Fighter f2);
    int activationRate(Fighter f1, Fighter f2);
    boolean onAttack();
    int duration();
}