class Class {

    //returns an array of all the classes.
    Class[] classList(){
        return new Class[]{
                new Class(
                        "Hero",
                        new String[]{"Sol", "Luna"},
                        new String[]{"Patience", "Veteran"}

                ),
                new Class(
                        "Warrior",
                        new String[]{"Colossus", "Counter"},
                        new String[]{"HP+", "Gamble"}
                ),
                new Class(
                        "Bishop",
                        new String[]{"Corona", "Miracle"},
                        new String[]{"Sacrifice", "Renewal"}
                ),
                new Class(
                        "General",
                        new String[]{"Bonfire", "Pavise"},
                        new String[]{"WaryFighter", "Resolve"}
                ),
                new Class(
                        "Berserker",
                        new String[]{"Guts", "Colossus"},
                        new String[]{"Axefaire", "Crit+"}
                ),
                new Class(
                        "Assassin",
                        new String[]{"Lethality", "PoisonedBlade"},
                        new String[]{"Avoid+", "Precision"}
                )


        };
    }

    private String className;
    String[] passiveSkills;
    String[] activeSkills;

    Class(){

    }

    private Class(String className, String[] activeSkills, String[] passiveSkills){
        this.className = className;
        this.passiveSkills = passiveSkills;
        this.activeSkills = activeSkills;
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
