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
                )

        };
    }

    String className;
    String[] passiveSkills;
    String[] activeSkills;

    Class(){

    }

    Class(String className, String[] activeSkills, String[] passiveSkills){
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
