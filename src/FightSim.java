/*  Colosseum Tournament's Fighting Simulator Component
    Stage 1: Loads fighter or creates a new fighter based on the input given
    Stage 2: Gets the fighter's stats into memory
    Stage 3: Conducts a battle between the two fighters
 */

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Random;

public class FightSim {
    private static String fighter1Name;
    private static String fighter2Name;
    private static Scanner scan = new Scanner(System.in);
    private static Fighter fighter1;
    private static Fighter fighter2;
    private static int fighterCnt;
    private static int f1HP;
    private static int f2HP;

    //It's pretty self explanatory
    public static void main(String[] args) throws IOException {
        getUserIn();
        createFighters();
        runSim();
    }

    //Checks the names of the fighters who are going to fight
    private static void getUserIn() {
        System.out.println("Enter the name of fighter 1");
        fighter1Name = scan.next();
        System.out.println("Enter the name of fighter 2");
        fighter2Name = scan.next();
    }

    //Based on the names given, checks if there is a fighter file for that name. If there is, load it, if not make a new one
    private static void createFighters() throws IOException {
        String fighterDir = System.getProperty("user.dir") + "/Fighters";
        File fig1File = new File(fighterDir + "/" + fighter1Name + ".txt");
        fighterCnt = 0;
        if(fig1File.exists()){
            loadFighter(fig1File);
        } else {
            //will call the Fighter constructor which asks for input for a fighter
            fighter1 = new Fighter();
        }
        fighterCnt = 1;
        File fig2File = new File(fighterDir + "/" + fighter2Name + ".txt");
        if(fig2File.exists()){
            loadFighter(fig2File);
        } else {
            fighter2 = new Fighter();
        }
    }

    //Process to load a fighter from a file
    private static void loadFighter(File filepath) throws IOException{
        List<String> lines = Files.readAllLines(filepath.toPath(), Charset.defaultCharset());
        int[] stats = new int[10];
        String[] abilities = new String[2];
        /*A for loop that looks at the lines in the fighter document that contain stats and adds the value after
        the spaces to the stat array. */
        for(int i = 1; i < 8; i++){
            stats[i-1] = Integer.parseInt(lines.get(i).substring(lines.get(i).lastIndexOf(' ') + 1));
        }
        for(int i = 10; i < 13; i++){
            stats[i-3] = Integer.parseInt(lines.get(i).substring(lines.get(i).lastIndexOf(' ') + 1));
        }
        abilities[0] = lines.get(15);
        abilities[1] = lines.get(16);
        if(fighterCnt == 0) {
            fighter1 = new Fighter(stats, abilities, fighter1Name);
            //System.out.println(fighter1);
        } else {
            fighter2 = new Fighter(stats,abilities, fighter2Name);
            //System.out.println(fighter2);
        }
    }

    private static void runSim() {
        BattleStats f1 = new BattleStats(fighter1, fighter2);
        BattleStats f2 = new BattleStats(fighter2, fighter1);
        printReadout(f1,f2);
        int first = 0;
        if (f1.as > 0) {
            if (f1.as >= 5) {
                first = 2;
            }
        } else if (f2.as > 0) {
            first = 1;
            if (f2.as >= 5) {
                first = 3;
            }
        } else {
            Random rand = new Random();
            first = rand.nextInt(2);
        }
        if (first == 0) {
            noDoubles(fighter1,fighter2);
        } else if(first == 2){
            doubles(fighter1,fighter2);
        } else if(first == 1){
            noDoubles(fighter2,fighter1);
        } else {
            doubles(fighter2,fighter1);
        }
    }
    private static void noDoubles(Fighter f1, Fighter f2){
        f1HP = f1.hp();
        f2HP = f2.hp();
        int roundCnt = 0;
        while(f1HP > 0 && f2HP > 0){
            System.out.println("ROUND:" + roundCnt);
            roundCnt++;
            f2HP = attack(f1.name, f1, f2, f1HP, f2HP);
            if(f2HP <= 0 ||f1HP <= 0){
                break;
            }
            f1HP = attack(f2.name, f2, f1, f2HP, f1HP);
        }
    }
    private static void doubles(Fighter f1, Fighter f2){
        f1HP = f1.hp();
        f2HP = f2.hp();
        int roundCnt = 0;
        while(f1HP > 0 && f2HP > 0){
            System.out.println("\n" + "ROUND: " + roundCnt + "\n");
            roundCnt++;
            f2HP = attack(f1.name, f1, f2, f1HP, f2HP);
            if(f2HP <= 0 ||f1HP <= 0){
                break;
            }
            f1HP = attack(f2.name, f2, f1, f2HP, f1HP);
            if(f2HP <= 0 ||f1HP <= 0){
                break;
            }
            f2HP = attack(f1.name, f1, f2, f1HP, f2HP);
        }
        if(f1HP > 0){
            System.out.println(f1.name + " wins!");
        } else {
            System.out.println(f2.name + " wins!");
        }
    }
    private static int attack(String name, Fighter aggressor, Fighter defender, int attackerHP,  int defenderHP){
        AbilityModule attackerAbilities = new AbilityModule(aggressor, defender, aggressor.hp(), defender.hp());
        AbilityModule defenderAbilities = new AbilityModule(defender,aggressor, defender.hp(), aggressor.hp());
        int attackerNum = 1;
        if(f1HP == defenderHP){
            attackerNum = 2;
        }
        attackerAbilities.activeCall();
        defenderAbilities.activeCall();
        BattleStats attacker = new BattleStats(aggressor,defender);
        Random rand = new Random();
        int hit = rand.nextInt(99)+1;
        int crit = rand.nextInt(99)+1;
        int activation = rand.nextInt(99)+1;
        int[] currMods = {0,0,0,0,0,0,0,0,0};
        String skill = "";
        boolean counter = false;
        if(activation <= attackerAbilities.activation){
            if(attackerAbilities.phase == 0) {
                skill = aggressor.abilities[0].toUpperCase() + " ";
                attackerAbilities = new AbilityModule(aggressor, defender, attackerHP, defenderHP);
                currMods = attackerAbilities.activeCall();
            }
        } else {
            Arrays.fill(currMods,0);
        }
        String critical = "Hit! ";
        int damage = attacker.power+currMods[0];
        if(hit <= attacker.hit+currMods[1]){
            if(crit <= attacker.crit+currMods[2]){
                damage *= 3;
                critical = "CRITICAL!";
            }
            if(defenderAbilities.phase == 1){
                if(defender.abilities[0].equals("Counter")){
                    int defActive = rand.nextInt(99) + 1;
                    if(defActive <= defenderAbilities.activation) {
                        counter = true;
                    }
                }
            }
            defenderHP -= damage;
            System.out.print(name + ": " + skill + critical + " (" + damage + "/" + defenderHP + ")");
        } else {
            System.out.print(name + ": Miss! (0/" + defenderHP + ")");
        }
        if(currMods[3] != 0) {
            if (attackerNum == 1) {
                f1HP += (currMods[3] * damage) / 100;
                if (f1HP > aggressor.hp()) {
                    f1HP = aggressor.hp();
                }
            } else {
                f2HP += (currMods[3] * damage) / 100;
                if (f2HP > aggressor.hp()) {
                    f2HP = aggressor.hp();
                }
            }
            System.out.print(" Recovered " + (currMods[3] * damage) / 100 + " HP!");
        }
        System.out.println();
        if(counter){
            if(f2HP > 0 && f1HP > 0) {
                if (attackerNum == 1) {
                    f1HP -= damage / 2;
                    System.out.println(fighter2Name + " counters! " + "(" + damage / 2 + "/" + f1HP + ")");
                } else {
                    f2HP -= damage / 2;
                    System.out.println(fighter1Name + " counters! " + "(" + damage / 2 + "/" + f2HP + ")");
                }
            }
        }
        return defenderHP;
    }
    private static void printReadout(BattleStats f1, BattleStats f2){
        System.out.println(String.format("%12s%15s", fighter1.name, fighter2.name));
        System.out.println(String.format("%-4s:%7s%s%-4s:%7s", "MT", f1.power, "   ", "MT", f2.power));
        System.out.println(String.format("%-4s:%7s%s%-4s:%7s", "Hit", f1.hit, "   ", "Hit", f2.hit));
        System.out.println(String.format("%-4s:%7s%s%-4s:%7s", "Crit", f1.crit, "   ", "Crit", f2.crit));
    }
}
