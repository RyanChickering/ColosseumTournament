public class Sol implements Abilities{
    @Override
    public int activationRate(Fighter f1, Fighter f2) {
        return f1.skl()*2;
    }

    @Override
    public int[] effect(Fighter f1, Fighter f2) {
        int[] out = {0,0,0,0,0,0,0,0,0,0};
        out[3] = (f1.str() + f1.weapon.mt() - f2.def())/2;
        return out;
    }
}
