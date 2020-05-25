public interface Abilities {
    //returns a single integer value for the activation rate. Passive skills have their effects calculated before battle
    public int activationRate(Fighter f1, Fighter f2);

    //returns an array of values of possible things that are effected
    /* key:
    0. bonus damage
    1. bonus hit
    2. bonus crit
    3. bonus healing
    4.
    5.
    -
    9. Extra spaces for expansion
     */
    public int[] effect(Fighter f1, Fighter f2);

}
