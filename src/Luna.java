public class Luna implements Abilities{
    @Override
    public int activationRate(Fighter f1, Fighter f2) {
        return f1.skl()*2;
    }

    @Override
    public int[] effect(Fighter f1, Fighter f2) {
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        out[0] = (f2.def()/2);
        return out;
    }
}
