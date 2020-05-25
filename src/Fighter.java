import java.io.*;
import java.util.*;

//Class type for all fighters
public class Fighter
{
    String name;
    private String[] statNames = {"HP","Str","Skl","Spd","Luc","Def","Res"};
    private int[] fstats = new int[7];
    public String[] abilities = new String[2];
    public Weapon weapon = new Weapon(0,0,0);

    public class Weapon{
        public int[] wstats = new int[3];
        public String name;
        public String flavor;
        public int type;
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
            public int mt(){
                return wstats[0];
            }
            public int hit(){
                return wstats[1];
            }
            public int crit(){
                return wstats[2];
            }
    }

    /* Easy to return stats*/
    public int hp(){
        return fstats[0];
    }
    public int str(){
        return fstats[1];
    }
    public int skl(){
        return fstats[2];
    }
    public int spd(){
        return fstats[3];
    }
    public int luc(){
        return fstats[4];
    }
    public int def(){
        return fstats[5];
    }
    public int res(){
        return fstats[6];
    }


    public Fighter() throws IOException{
        getInput();
        writeFile();
    }

    public Fighter(int[] stats, String[]abilities, String name){
        this.name = name;
        for(int i = 0; i < 7; i++) {
            fstats[i] = stats[i];
        }
        for(int i = 7; i < 10; i++){
            weapon.wstats[(i-7)] = stats[i];
        }
        this.abilities[0] = abilities[0];
        this.abilities[1] = abilities[1];
    }


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

    private void writeFile() throws IOException
    {
        String charDirectory = System.getProperty("user.dir") + "/Fighters";
        File filePath = new File(charDirectory);
        File charFolder = new File("Characters");
        String wroteFilePath = charDirectory + "/" + name + ".txt";
        System.out.println(System.getProperty("user.dir"));
        PrintWriter printer = new PrintWriter(wroteFilePath, "UTF-8");
        printer.println(String.format("%-4s:%10s","Name",name));
        for(int i = 0; i<7;i++)
        {
            printer.println(String.format("%-4s:%10d",statNames[i],fstats[i] ));

        }
        printer.println(String.format("%-4s:%10s","Weapon", weapon.name));
        printer.println(String.format("%-4s:%10s","Type", weapon.type));
        printer.println(String.format("%-4s:%10s","Mt",weapon.mt()));
        printer.println(String.format("%-4s:%10s","Hit",weapon.hit()));
        printer.println(String.format("%-4s:%10s","Crit",weapon.crit()));
        printer.println(String.format("%-4s:%10s","Text",weapon.flavor));
        printer.println("Abilities:");
        printer.println(abilities[0]);
        printer.println(abilities[1]);
        printer.close();
    }

    @Override
    public String toString()
    {
        String out = "";
        for(int i = 0; i < 6; i++)
        {
            out += statNames[i] + " = " + fstats[i] + " ";
        }
        return out;
    }

}