/*  Colosseum Tournament's Fighting Simulator Component
    Stage 1: Loads fighter or creates a new fighter based on the input given
    Stage 2: Gets the fighter's stats into memory
    Stage 3: Conducts a battle between the two fighters
 */

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;

public class FightSim {
    private static Fighter fighter1;
    private static Fighter fighter2;
    private static int f1HP;
    private static int f2HP;
    private static JPanel fighter1Stats;
    private static JPanel fighter2Stats;
    private static JPanel skillPickers;
    private static StringBuilder simulationText;
    private final static int TOTALALLOWED = 120;
    private final static int WEAPONALLOWED = 25;
    private static final AbilityModule ABILITY_MODULE = new AbilityModule();

    //It's pretty self explanatory
    public static void main(String[] args){
        buildUI();
    }

    private static void buildUI(){
        JFrame frame = new JFrame("Colosseum Tournament");
        JComboBox<String> fighter1Select = new JComboBox<>(getFighterFiles());
        JComboBox<String> fighter2Select = new JComboBox<>(getFighterFiles());
        JButton createFighter = new JButton("Create new fighter");
        fighter1 = loadFighter(fighter1Select.getItemAt(fighter1Select.getSelectedIndex()));
        fighter1Stats = new JPanel();
        if(fighter1 != null) {
            fighter1Stats = fighterReadout(fighter1);
        }
        fighter2 = loadFighter(fighter2Select.getItemAt(fighter1Select.getSelectedIndex()));
        fighter2Stats = new JPanel();
        if(fighter2 != null) {
            fighter2Stats = fighterReadout(fighter2);
        }
        JButton runSim = new JButton("Run simulation");
        frame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        frame.add(fighter1Select, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        frame.add(fighter2Select, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.ipadx = 60;
        constraints.ipady = 140;
        frame.add(fighter1Stats, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.ipadx = 60;
        constraints.ipady = 140;
        frame.add(fighter2Stats, constraints);
        constraints.gridx = 2;
        constraints.gridy = 0;
        frame.add(createFighter, constraints);
        constraints.gridx = 2;
        constraints.gridy = 1;
        frame.add(runSim, constraints);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500,500);
        frame.setVisible(true);
        fighter1Select.addActionListener(e -> selectionChange(frame, fighter1Select, 0));
        fighter2Select.addActionListener(e -> selectionChange(frame, fighter2Select, 1));
        createFighter.addActionListener(e -> createFighterWindow());
        runSim.addActionListener(e -> simulate());
    }

    private static void simulate(){
        JFrame frame = new JFrame("Simulation");
        JTextArea battleOutput = new JTextArea(printReadout(
                new BattleStats(fighter1, fighter2), new BattleStats(fighter2, fighter1)) + runSim());
        JScrollPane scrollPane = new JScrollPane(battleOutput);
        frame.add(scrollPane);
        frame.setSize(400, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    //method that opens UI to make a new fighter
    private static void createFighterWindow(){
        Fighter newFighter = new Fighter();
        JFrame frame = new JFrame("Create Fighter");
        JTextArea readout = battleStatPanel(newFighter);
        JPanel stats = statsPanel(newFighter, readout);
        JPanel weapon = weaponPanel(newFighter, readout);
        JPanel skills = skillPicker(newFighter);
        JButton save = new JButton("Save and complete");
        JTextField name = new JTextField("Enter a name");
        frame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        //constraints.gridwidth = 2;
        //constraints.fill = GridBagConstraints.HORIZONTAL;
        frame.add(name, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridheight = 2;
        frame.add(stats, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        frame.add(weapon, constraints);
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        frame.add(skills, constraints);
        constraints.gridx = 2;
        constraints.gridy = 1;
        frame.add(readout,constraints);
        constraints.gridx = 2;
        constraints.gridy = 4;
        frame.add(save, constraints);

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(500,500);
        frame.setVisible(true);
    }

    private static JPanel skillPicker(Fighter fighter){
        JPanel skillPanel = new JPanel();
        Class classes = new Class();
        Class[] classlist = classes.classList();
        JComboBox classPicker = new JComboBox<>(classes.classNames());
        skillPickers = skillPickers(classlist[classPicker.getSelectedIndex()]);
        skillPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        skillPanel.add(classPicker, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        skillPanel.add(skillPickers, constraints);
        classPicker.addActionListener(e -> updateSkillPickers(skillPanel, classlist, classPicker));
        return skillPanel;
    }

    private static void updateSkillPickers(JPanel skillPanel, Class[] classlist, JComboBox classPicker){
        skillPanel.remove(skillPickers);
        skillPickers = skillPickers(classlist[classPicker.getSelectedIndex()]);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        skillPanel.add(skillPickers, constraints);
        skillPanel.revalidate();
        skillPanel.repaint();
    }

    private static JPanel skillPickers(Class fighterClass){
        JPanel skillPanel = new JPanel();
        JComboBox<String> activeSkill = new JComboBox<>(fighterClass.activeSkills);
        JTextArea activeDesc = new JTextArea(
                ABILITY_MODULE.skillDesc(activeSkill.getItemAt(activeSkill.getSelectedIndex())));
        JComboBox<String> passiveSkill = new JComboBox<>(fighterClass.passiveSkills);
        JTextArea passiveDesc = new JTextArea(
                ABILITY_MODULE.skillDesc(passiveSkill.getItemAt(passiveSkill.getSelectedIndex())));
        skillPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        skillPanel.add(activeSkill, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        skillPanel.add(passiveSkill, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        skillPanel.add(activeDesc, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        skillPanel.add(passiveDesc, constraints);
        return skillPanel;
    }

    private static JTextArea battleStatPanel(Fighter fighter){
        int[] cumstats = fighter.calcFinals();
        JTextArea info = new JTextArea(String.format(
                "%-16s%4d\n%-16s%4d\n%-16s%4d\n%-16s%4d\n%-16s%4d\n\n%-15s%2d/%2d\n%-15s%2d/%2d",
                "Might:", cumstats[0], "Hit:", cumstats[1], "Avoid:", cumstats[2],
                "Critical:", cumstats[3], "Critical Avoid:", cumstats[4],
                "Stat Points:", fighter.calcPoints(), TOTALALLOWED,
                "Weapon Points:", fighter.weapon.calcPoints(), WEAPONALLOWED
        ));
        info.setFont(new Font("Courier New", Font.PLAIN, 12));
        info.setSize(200,150);
        return info;
    }

    private static void refreshBattleStats(JTextArea battleStats, Fighter fighter){
        int[] cumstats = fighter.calcFinals();
        battleStats.setText(String.format(
                "%-16s%4d\n%-16s%4d\n%-16s%4d\n%-16s%4d\n%-16s%4d\n\n%-15s%2d/%2d\n%-15s%2d/%2d",
                "Might:", cumstats[0], "Hit:", cumstats[1], "Avoid:", cumstats[2],
                "Critical:", cumstats[3], "Critical Avoid:", cumstats[4],
                "Stat Points:", fighter.calcPoints(), TOTALALLOWED,
                "Weapon Points:", fighter.weapon.calcPoints(), WEAPONALLOWED
        ));
    }

    //Creates a set of panels for each of the weapon stats
    private static JPanel weaponPanel(Fighter fighter, JTextArea readout){
        JPanel weapon = new JPanel();
        JTextField name = new JTextField("Enter a weapon name");
        weapon.add(name);
        for(int i = 0; i < Fighter.NUMWSTATS; i++){
            weapon.add(statPanel(fighter, i, true, readout));
        }
        weapon.setLayout(new GridLayout(4,1));
        weapon.setSize(75,120);
        return weapon;
    }

    //Creates a set of panels for each of the main stats
    private static JPanel statsPanel(Fighter fighter, JTextArea readout){
        JPanel stats = new JPanel();
        for(int i = 0; i < fighter.fstats.length; i++){
            stats.add(statPanel(fighter, i, false, readout));
        }
        stats.setLayout(new GridLayout(7,1));
        stats.setSize(75,210);
        return stats;

    }

    //creates a panel with the name of a stat, textfield for stat, and buttons to control the
    //value of the stat
    private static JPanel statPanel(Fighter fighter, int statNum, boolean weapon, JTextArea readout){
        JPanel stat = new JPanel();
        JLabel name;
        JTextField value;
        if(weapon){
            name = new JLabel(fighter.weapon.wnames[statNum]);
            value = new JTextField(fighter.weapon.wstats[statNum] + "");
        } else {
            name = new JLabel(fighter.statNames[statNum]);
            value = new JTextField(fighter.fstats[statNum] + "");
        }
        name.setSize(30,30);
        name.setPreferredSize(name.getSize());
        value.setSize(30,30);
        value.setPreferredSize(value.getSize());
        JButton up = new JButton("");
        up.setSize(15,15);
        up.setPreferredSize(up.getSize());
        JButton down = new JButton("");
        down.setSize(15,15);
        down.setPreferredSize(down.getSize());
        up.addActionListener(e -> statControls(fighter, statNum, 1, value, weapon, readout));
        down.addActionListener(e-> statControls(fighter, statNum, -1, value, weapon, readout));
        stat.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 4;
        constraints.gridwidth = 2;
        stat.add(name, constraints);
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridheight = 4;
        constraints.gridwidth = 2;
        stat.add(value, constraints);
        constraints.gridx = 4;
        constraints.gridy = 0;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        stat.add(up, constraints);
        constraints.gridx = 4;
        constraints.gridy = 1;
        stat.add(down, constraints);
        stat.setSize(75,30);
        return stat;
    }

    //Method for the stat modifying buttons
    private static void statControls(Fighter fighter, int statNum, int mod,
                                     JTextField value, boolean weapon, JTextArea readout){
        if(weapon){
            if(fighter.weapon.calcPoints()+mod <= WEAPONALLOWED) {
                if (statNum == Fighter.HIT) {
                    mod *= Fighter.HITINC;
                } else if (statNum == Fighter.CRIT) {
                    mod *= Fighter.CRITINC;
                }
                if (fighter.weapon.wstats[statNum] + mod > 0) {
                    fighter.weapon.wstats[statNum] += mod;
                    value.setText(fighter.weapon.wstats[statNum] + "");
                }
            }
        } else {
            if(fighter.calcPoints()+ mod <= TOTALALLOWED) {
                if (statNum == Fighter.HP) {
                    mod *= Fighter.HPINC;
                }
                if (fighter.fstats[statNum] + mod > 0)
                    fighter.fstats[statNum] += mod;
                value.setText(fighter.fstats[statNum] + "");
            }
        }
        refreshBattleStats(readout, fighter);

    }

    //method that runs when a change in the fighter selection is found
    private static void selectionChange(JFrame frame, JComboBox fighter1Select, int num){
        //Awkward solution
        //If num is 1 we're changing fighter2
        if(num == 1) {
            fighter2 = loadFighter(fighter1Select.getItemAt(fighter1Select.getSelectedIndex()).toString());
            frame.remove(fighter2Stats);
            fighter2Stats = fighterReadout(fighter2);
        } else {
            fighter1 = loadFighter(fighter1Select.getItemAt(fighter1Select.getSelectedIndex()).toString());
            frame.remove(fighter1Stats);
            fighter1Stats = fighterReadout(fighter1);
        }
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = num;
        constraints.gridy = 1;
        constraints.ipadx = 60;
        constraints.ipady = 140;
        if(num == 1) {
            frame.add(fighter2Stats, constraints);
        } else {
            frame.add(fighter1Stats, constraints);
        }
        frame.revalidate();
        frame.repaint();
    }

    //Builds a panel that includes fighter stats
    private static JPanel fighterReadout(Fighter fighter){
        JPanel panel = new JPanel();
        JLabel hp = new JLabel("HP");
        JLabel str = new JLabel("STR");
        JLabel skl = new JLabel("SKL");
        JLabel spd = new JLabel("SPD");
        JLabel luc = new JLabel("LUC");
        JLabel def = new JLabel("DEF");
        JLabel res = new JLabel("RES");
        JTextField hpStat = new JTextField(fighter.hp() + "");
        JTextField strStat = new JTextField(fighter.str() + "");
        JTextField sklStat = new JTextField(fighter.skl() + "");
        JTextField spdStat = new JTextField(fighter.spd() + "");
        JTextField lucStat = new JTextField(fighter.luc() + "");
        JTextField defStat = new JTextField(fighter.def() + "");
        JTextField resStat = new JTextField(fighter.res() + "");
        panel.add(hp);
        panel.add(hpStat);
        panel.add(str);
        panel.add(strStat);
        panel.add(skl);
        panel.add(sklStat);
        panel.add(spd);
        panel.add(spdStat);
        panel.add(luc);
        panel.add(lucStat);
        panel.add(def);
        panel.add(defStat);
        panel.add(res);
        panel.add(resStat);
        panel.setLayout(new GridLayout(7,2));
        panel.setSize(60,140);
        return panel;
    }

    private static String[] getFighterFiles(){
        File fighterFolder  = new File(System.getProperty("user.dir") + "/Fighters");
        if(fighterFolder.exists()){
            try {
                String[] output = new String[fighterFolder.listFiles().length];
                int i = 0;
                for (final File fighter : fighterFolder.listFiles()) {
                    if(fighter.getName().contains("txt")) {
                        output[i] = fighter.getName();
                        i++;
                    }
                }
                return output;
            } catch(Exception e){
                throwError("Error reading files");
                return null;
            }
        } else {
            return null;
        }
    }

    private static void throwError(String errorName){
        JFrame error = new JFrame("Error");
        JLabel message = new JLabel(errorName);
        JButton close = new JButton("Close");
        close.addActionListener(e -> error.dispose());
        error.add(message);
        error.add(close);
        error.setSize(200,200);
        error.setLayout(new GridLayout(2,1));
        error.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        error.setVisible(true);
    }

    //Process to load a fighter from a file
    private static Fighter loadFighter(String filename){
        File filepath = new File(System.getProperty("user.dir") + "/Fighters/" + filename);
        List<String> lines;
        try {
             lines = Files.readAllLines(filepath.toPath(), Charset.defaultCharset());
        } catch(Exception e) {
            throwError("Error reading the selected file");
            return null;
        }
        String fighterName = lines.get(0).substring(lines.get(0).lastIndexOf(" "));
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
        return new Fighter(stats, abilities, fighterName);
    }

    private static String runSim() {
        BattleStats f1 = new BattleStats(fighter1, fighter2);
        BattleStats f2 = new BattleStats(fighter2, fighter1);
        simulationText = new StringBuilder();
        boolean doubles = false;
        int first = 0;
        if (f1.as > 0) {
            if (f1.as >= 5) {
                doubles = true;
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
            BattleState state = new BattleState(fighter1, fighter2, doubles);
            return noDoubles(state);
        } else if(first == 2){
            BattleState state = new BattleState(fighter1, fighter2, doubles);
            return doubles(state);
        } else if(first == 1){
            BattleState state = new BattleState(fighter2, fighter1, doubles);
            return noDoubles(state);
        } else {
            BattleState state = new BattleState(fighter2, fighter1, doubles);
            return doubles(state);
        }
    }

    private static String noDoubles(BattleState state){
        int roundCnt = 0;
        while(state.fighter1HP > 0 && state.fighter2HP > 0){
            simulationText.append("ROUND:");
            simulationText.append(roundCnt);
            roundCnt++;
            f2HP = attack(state);
            if(f2HP <= 0 ||f1HP <= 0){
                break;
            }
            f1HP = attack(state);
        }
        return simulationText.toString();
    }
    private static String doubles(BattleState state){
        int roundCnt = 0;
        while(state.fighter1HP > 0 && state.fighter2HP > 0){
            simulationText.append("\n");
            simulationText.append("ROUND: ");
            simulationText.append(roundCnt);
            simulationText.append("\n");
            roundCnt++;
            f2HP = attack(state);
            if(state.fighter1HP <= 0 ||state.fighter2HP <= 0){
                break;
            }
            f1HP = attack(state);
            if(state.fighter1HP <= 0 ||state.fighter2HP <= 0){
                break;
            }
            f2HP = attack(state);
        }
        if(state.fighter1HP > 0){
            simulationText.append(state.fighter1.name);
        } else {
            simulationText.append(state.fighter2.name);
        }
        simulationText.append(" wins!");
        return simulationText.toString();
    }

    private static int attack(String name, Fighter aggressor, Fighter defender, int attackerHP,  int defenderHP){
        AbilityModule attackerAbilities = new AbilityModule(aggressor, defender, aggressor.hp(), defender.hp());
        AbilityModule defenderAbilities = new AbilityModule(defender,aggressor, defender.hp(), aggressor.hp());
        int attackerNum = 1;
        if(f1HP == defenderHP){
            attackerNum = 2;
        }
        //initializes the activation rate within the module
        attackerAbilities.activeCall();
        defenderAbilities.activeCall();
        BattleStats attacker = new BattleStats(aggressor,defender);
        //creates random numbers
        Random rand = new Random();
        int hit = rand.nextInt(99)+1;
        int crit = rand.nextInt(99)+1;
        int activation = rand.nextInt(99)+1;
        int[] currMods = attackerAbilities.BASE;
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
        simulationText.append(name);
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
            simulationText.append(": ");
            simulationText.append(skill);
            simulationText.append(critical);
            simulationText.append(" (");
            simulationText.append(damage);
            simulationText.append("/");
        } else {
            simulationText.append(name);
            simulationText.append(": Miss! (0/");
        }
        simulationText.append(defenderHP);
        simulationText.append(")");
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
            simulationText.append(" Recovered ");
            simulationText.append((currMods[3] * damage) / 100);
            simulationText.append(" HP!");
        }
        simulationText.append("\n");
        if(counter){
            if(f2HP > 0 && f1HP > 0) {
                if (attackerNum == 1) {
                    f1HP -= damage / 2;
                    simulationText.append(fighter2.name);
                    simulationText.append(" counters! ");
                    simulationText.append("(");
                    simulationText.append(damage / 2);
                    simulationText.append("/");
                    simulationText.append(f1HP);
                } else {
                    f2HP -= damage / 2;
                    simulationText.append(fighter1.name);
                    simulationText.append(" counters! ");
                    simulationText.append("(");
                    simulationText.append(damage / 2);
                    simulationText.append("/");
                    simulationText.append(f2HP);

                }
                simulationText.append(")");
                simulationText.append("\n");
            }
        }
        return defenderHP;
    }

    private static int attack(BattleState state){
        /* need to check hit, crit, and active skill.
         * Need to make sure that passive skill modifiers exist on the units
         * Some active skills are defensive active skills, need to check for those.
         * Some active skills create effects that last for an entire round, need to make sure that those
         * effects persist.
         */

        Random rand = new Random();
        int hit = rand.nextInt(99);
        int crit = rand.nextInt(99);
        int activation = rand.nextInt(99);
        String offensiveSkill = "";
        String defensiveSkill = "";
        String critical = "";
        int damage;

        if(state.whichAttack()){
            //First check if the ability activated
            if(state.f1Abilities.onAttack()){
                if(activation < state.f1Abilities.getActivation()) {
                    state.f1Active = state.f1Abilities.activeCall();
                    state.f1Duration = state.f1Abilities.getDuration();
                    offensiveSkill = state.fighter1.abilities[state.f1Abilities.ACTIVE];
                }
            }
            if(!state.f2Abilities.onAttack()){
                activation = rand.nextInt(99);
                if(activation < state.f2Abilities.getActivation()) {
                    state.f2Passive = state.f2Abilities.activeCall();
                    defensiveSkill = state.fighter2.abilities[state.f2Abilities.ACTIVE];
                }
            }
            //Then check if we hit
            if(hit < state.fighter1Stats.hit 
                    + state.f1Passive[state.f1Abilities.HITUP] + state.f1Active[state.f1Abilities.HITUP]
                    - state.f2Passive[state.f1Abilities.AVOIDUP] - state.f2Active[state.f2Abilities.AVOIDUP]){
                //Then check for crit
                if(crit < state.fighter1Stats.crit
                        + state.f1Passive[state.f1Abilities.CRITUP] + state.f1Active[state.f1Abilities.CRITUP]
                        - state.f2Passive[state.f1Abilities.DDGUP] - state.f2Active[state.f2Abilities.DDGUP]){
                    critical = "CRITICAL!";

                }
            } else {

            }
            if(state.f1Duration > 0){
                state.f1Duration--;
            } else {
                state.f1Active = state.f1Abilities.BASE;
            }
        }

        return state.fighter2HP;


    }

    private static class BattleState{
        int fighter1HP;
        int fighter2HP;
        int f1Duration;
        int f2Duration;
        Fighter fighter1;
        Fighter fighter2;
        boolean doubles;
        int attackNum;
        AbilityModule f1Abilities;
        AbilityModule f2Abilities;
        BattleStats fighter1Stats;
        BattleStats fighter2Stats;
        int[] f1Active;
        int[] f2Active;
        int[] f1Passive;
        int[] f2Passive;
        BattleState(Fighter fighter1, Fighter fighter2, boolean doubles){
            this.fighter1HP = fighter1.hp();
            this.fighter2HP = fighter2.hp();
            this.doubles = doubles;
            this.attackNum = 0;
            this.f1Abilities = new AbilityModule(fighter1, fighter2, fighter1HP, fighter2HP);
            this.f2Abilities = new AbilityModule(fighter2, fighter1, fighter2HP, fighter1HP);
            this.fighter1Stats = new BattleStats(fighter1, fighter2);
            this.fighter2Stats = new BattleStats(fighter2, fighter1);
            f1Active = f1Abilities.BASE;
            f1Passive = f1Abilities.BASE;
            f2Active = f2Abilities.BASE;
            f2Passive = f2Abilities.BASE;
        }

        //true is the first to go is attacking, false is the second to attack is attacking
        boolean whichAttack(){
            if(doubles){
                return 1 != attackNum%3;
            } else {
                return 0 == attackNum%2;
            }
        }
    }




    private static String printReadout(BattleStats f1, BattleStats f2){
        String out =(String.format("%12s%15s\n", fighter1.name, fighter2.name))
                + (String.format("%-4s:%7s%s%-4s:%7s\n", "MT", f1.power, "   ", "MT", f2.power))
                + (String.format("%-4s:%7s%s%-4s:%7s\n", "Hit", f1.hit, "   ", "Hit", f2.hit))
                + (String.format("%-4s:%7s%s%-4s:%7s\n", "Crit", f1.crit, "   ", "Crit", f2.crit));
        return out;
    }
}
