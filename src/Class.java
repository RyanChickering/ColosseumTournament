class Class {

    //returns an array of all the classes.
    Class[] classList(){
        return new Class[]{
                new Class(
                        "Gladiator",
                        new String[]{"Sol", "Luna"},
                        new String[]{"Patience", "Veteran"},
                        new int[]{50,10,10,10,10,10,10}

                ),
                new Class(
                        "Warrior",
                        new String[]{"Colossus", "Counter"},
                        new String[]{"HP+", "Gamble"},
                        new int[]{50,10,10,10,10,10,10}
                ),
                new Class(
                        "Bishop",
                        new String[]{"Corona", "Miracle"},
                        new String[]{"Sacrifice", "Renewal"},
                        new int[]{50,10,10,10,10,10,10}
                ),
                new Class(
                        "General",
                        new String[]{"Bonfire", "Pavise"},
                        new String[]{"WaryFighter", "Resolve"},
                        new int[]{50,10,10,10,10,10,10}
                ),
                new Class(
                        "Berserker",
                        new String[]{"Guts", "Colossus"},
                        new String[]{"Axefaire", "Crit+"},
                        new int[]{50,10,10,10,10,10,10}
                ),
                new Class(
                        "Assassin",
                        new String[]{"Lethality", "PoisonedBlade"},
                        new String[]{"Avoid+", "Precision"},
                        new int[]{50,10,10,10,10,10,10}
                ),
                new Class(
                        "Sniper",
                        new String[]{"Deadeye", "SureShot","Pierce"},
                        new String[]{"Crit+", "EagleEye", "Precision"},
                        new int[]{50,10,10,10,10,10,10}
                ),
                new Class(
                        "Mage",
                        new String[]{"Flare", "Adept", "StarStorm"},
                        new String[]{"EmpoweredMagic", "Crit+"},
                        new int[]{50,10,10,10,10,10,10}
                ),
                new Class(
                        "Swordmaster",
                        new String[]{"StarStorm", "Adept"},
                        new String[]{"Crit+", "Avoid+"},
                        new int[]{50,10,10,10,10,10,10}
                )
        };
    }

    private String className;
    String[] passiveSkills;
    String[] activeSkills;
    int[] bases;

    Class(){

    }

    private Class(String className, String[] activeSkills, String[] passiveSkills, int[] bases){
        this.className = className;
        this.passiveSkills = passiveSkills;
        this.activeSkills = activeSkills;
        this.bases = bases;
    }

    //returns the names of all the classes
    String[] classNames(){
        String[] out = new String[classList().length];
        for(int i = 0; i < classList().length; i++){
            out[i] = classList()[i].className;
        }
        return out;
    }
}
