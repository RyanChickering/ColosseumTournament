public class Patience implements Abilities{
    @Override
    public int activationRate(Fighter f1, Fighter f2) {
        return 100;
    }

    @Override
    public int[] effect(Fighter f1, Fighter f2) {
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        out[1] = 15;
        return out;
    }
}
