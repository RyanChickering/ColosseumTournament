import java.io.*;
import java.util.*;

//Class type for all fighters


public class Fighter
{
    final static int NUMSTATS = 7;
    final static int NUMWSTATS = 3;
    final static int HIT = 1;
    final static int CRIT = 2;
    final static int HP = 0;
    final static int HITINC = 5;
    final static int HPINC = 5;
    final static int CRITINC = 3;
    String name;
    String[] statNames = {"HP","Str","Skl","Spd","Luc","Def","Res", "Mt", "Hit", "Crit"};
    int[] fstats = new int[NUMSTATS];
    private int[] cumstats = new int[5];
    String[] abilities = new String[2];
    Weapon weapon = new Weapon(0,0,0);

    //Internal class that has the parameters of a character's weapon
    public class Weapon{
        int[] wstats = new int[3];
        String[] wnames = {"Mt", "Hit", "Crit"};
        String name;
        String flavor;
        int type;
            Weapon(int mt, int hit, int crit){
                this.name = "";
                this.flavor = "";
                this.type = 0;
                wstats[0] = mt;
                wstats[1] = hit;
                wstats[2] = crit;
            }
            Weapon(String name, int mt, int hit, int crit, String flavor){
                this.name =name;
                this.flavor = flavor;
                this.type = 0;
                wstats[0] = mt;
                wstats[1] = hit;
                wstats[2] = crit;
            }
            int mt(){
                return wstats[0];
            }
            int hit(){
                return wstats[1];
            }
            int crit(){
                return wstats[2];
            }

            int calcPoints(){
                int total = wstats[0];
                total += wstats[HIT]/HITINC;
                total += wstats[CRIT]/CRITINC;
                return total;
            }
    }

    /* Easy to return stats*/
    int hp(){
        return fstats[0];
    }
    int str(){
        return fstats[1];
    }
    int skl(){
        return fstats[2];
    }
    int spd(){
        return fstats[3];
    }
    int luc(){
        return fstats[4];
    }
    int def(){
        return fstats[5];
    }
    int res(){
        return fstats[6];
    }


    //Default constructor
    Fighter(){
        fstats[0] = 10;
        for(int i = 1; i < fstats.length; i++){
            fstats[i] = 10;
        }
        this.weapon.wstats[0] = 2;
        this.weapon.wstats[1] = 70;
        this.weapon.wstats[2] = 0;
        this.name = "Enter a name";
    }

    //Constructor that takes parameters
    Fighter(int[] stats, String[]abilities, String name){
        this.name = name;
        fstats = Arrays.copyOf(stats, 7);
        for(int i = 7; i < 10; i++){
            weapon.wstats[(i-7)] = stats[i];
        }
        this.abilities[0] = abilities[0];
        this.abilities[1] = abilities[1];
    }

    //Calculates the total number of stat points used in a fighter's build
    int calcPoints(){
        int total = fstats[0]/5;
        for(int i = 1; i < fstats.length; i++){
            total += fstats[i];
        }
        return total;
    }

    //Calculates combined statistics for a fighter
    int[] calcFinals(){
        //total might
        cumstats[0] = this.str() + this.weapon.mt();
        //hit
        cumstats[1] = 2*this.skl() + this.luc() + this.weapon.hit();
        //avo
        cumstats[2] = 2*this.spd() + this.luc();
        //crit
        cumstats[3] = this.skl()/2 + this.weapon.crit();
        //ddg
        cumstats[4] = this.luc();
        return cumstats;
    }

    //Method to get input when running as a command line program
    private void getInput()
    {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter a name:");
        name = scan.next();
        for(int i = 0; i < statNames.length; i++)
        {
            System.out.println("Enter " + statNames[i]);
            int val = scan.nextInt();
            fstats[i] = val;
        }
        System.out.println("Enter a weapon name:");
        weapon.name = scan.next();
        System.out.println("Enter mt,hit,crit:");
        for(int i = 0; i<3; i++){
            weapon.wstats[i] = scan.nextInt();
        }
        System.out.println("Enter a flavor text: (Press 0 to skip)");
        String flavor = scan.next();
        if(!flavor.equals("0")){
            weapon.flavor = flavor;
        }
        System.out.println("Enter ability names:");
        abilities[0] = scan.next();
        abilities[1] = scan.next();

    }

    //Method to save a fighter object as a file
    void writeFile() throws IOException
    {
        //Gets the Fighter Directory
        String charDirectory = System.getProperty("user.dir") + "/Fighters";
        String wroteFilePath = charDirectory + "/" + name + ".txt";
        PrintWriter printer = new PrintWriter(wroteFilePath, "UTF-8");
        //Prints the fighter name to the file
        printer.println(String.format("%-4s:%10s","Name",name));
        //Prints the 7 main stats to the file
        for(int i = 0; i<7;i++)
        {
            printer.println(String.format("%-4s:%10d",statNames[i],fstats[i] ));

        }
        //Prints out the weapon information to the file
        printer.println(String.format("%-4s:%10s","Weapon", weapon.name));
        printer.println(String.format("%-4s:%10s","Type", weapon.type));
        printer.println(String.format("%-4s:%10s","Mt",weapon.mt()));
        printer.println(String.format("%-4s:%10s","Hit",weapon.hit()));
        printer.println(String.format("%-4s:%10s","Crit",weapon.crit()));
        printer.println(String.format("%-4s:%10s","Text",weapon.flavor));
        //Prints the abilities to the file
        printer.println("Abilities:");
        printer.println(abilities[0]);
        printer.println(abilities[1]);
        printer.close();
    }

    @Override
    public String toString()
    {
        StringBuilder out = new StringBuilder();
        for(int i = 0; i < 6; i++)
        {
            out.append(String.format("%s%s%d%s", statNames[i], " = ", fstats[i], " \n"));
        }
        return out.toString();
    }

    //Returns a string of the stats and abilities of a fighter.
    String fighterData(){
        return String.format("%-10s%10d%-10s%11d%-10s%11d" +
                        "%-10s%11d%-10s%11d%-10s%11d" +
                        "%-10s%11d%-10s%11d%-10s%11d" +
                        "%-10s%11d%-10s%13s%-10s%13s",
                "HP: ", this.hp(),
                "\nStr: ", this.str(),
                "\nSkl: ", this.skl(),
            "\nSpd: ", this.spd(),
            "\nLuc: ", this.luc(),
            "\nDef: ", this.def(),
            "\nRes: ", this.res(),
            "\nMight: ", this.weapon.mt(),
            "\nHit: ", this.weapon.hit(),
            "\nCrit: ", this.weapon.crit(),
            "\nActive: ", this.abilities[0],
            "\nPassive: ", this.abilities[1]);
    }

}