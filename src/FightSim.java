/*  Colosseum Tournament's Fighting Simulator Component
    Stage 1: Loads fighter or creates a new fighter based on the input given
    Stage 2: Gets the fighter's stats into memory
    Stage 3: Conducts a battle between the two fighters
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import java.util.*;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;

public class FightSim {
    private static Fighter fighter1;
    private static Fighter fighter2;
    private static JPanel fighter1Stats;
    private static JPanel fighter2Stats;
    private static JComboBox<String> fighter1Select;
    private static JComboBox<String> fighter2Select;
    private static JPanel skillPickers;
    private static StringBuilder simulationText;
    private final static int TOTALALLOWED = 120;
    private final static int WEAPONALLOWED = 25;
    private static final AbilityModule ABILITY_MODULE = new AbilityModule();
    private static final int CRIT_MODIFIER = 2;
    private static final int[] BASE = {0,0,0,0,0,0,0,0,0,0};
    private static JFrame mainFrame;

    //It's pretty self explanatory
    public static void main(String[] args){
        buildUI();
    }

    private static void buildUI(){
        mainFrame = new JFrame("Colosseum Tournament");
        fighter1Select = new JComboBox<>(getFighterFiles());
        fighter2Select = new JComboBox<>(getFighterFiles());
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
        mainFrame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        mainFrame.add(fighter1Select, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        mainFrame.add(fighter2Select, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.ipadx = 60;
        constraints.ipady = 140;
        mainFrame.add(fighter1Stats, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.ipadx = 60;
        constraints.ipady = 140;
        mainFrame.add(fighter2Stats, constraints);
        constraints.gridx = 2;
        constraints.gridy = 0;
        mainFrame.add(createFighter, constraints);
        constraints.gridx = 2;
        constraints.gridy = 1;
        mainFrame.add(runSim, constraints);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(500,500);
        mainFrame.setVisible(true);
        fighter1Select.addActionListener(e -> selectionChange(mainFrame, fighter1Select, 0));
        fighter2Select.addActionListener(e -> selectionChange(mainFrame, fighter2Select, 1));
        createFighter.addActionListener(e -> createFighterWindow());
        runSim.addActionListener(e -> simulate());
    }

    private static void simulate(){
        JFrame frame = new JFrame("Simulation");
        JTextArea battleOutput = new JTextArea(printReadout(
                new BattleStats(fighter1, fighter2, 1), new BattleStats(fighter2, fighter1, 1)) + runSim());
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
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newFighter.name = name.getText();
                try {
                    newFighter.writeFile();
                } catch(Exception ex){
                    throwError("Error writing file");
                }
                fighter1Select = new JComboBox<>(getFighterFiles());
                fighter2Select = new JComboBox<>(getFighterFiles());
                frame.dispose();
                mainFrame.revalidate();
                mainFrame.repaint();
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(700,500);
        frame.setVisible(true);
    }

    private static JPanel skillPicker(Fighter fighter){
        JPanel skillPanel = new JPanel();
        Class classes = new Class();
        Class[] classlist = classes.classList();
        JComboBox classPicker = new JComboBox<>(classes.classNames());
        skillPickers = skillPickers(classlist[classPicker.getSelectedIndex()], fighter);
        skillPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        skillPanel.add(classPicker, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        skillPanel.add(skillPickers, constraints);
        classPicker.addActionListener(e -> updateSkillPickers(skillPanel, classlist, classPicker, fighter));
        return skillPanel;
    }

    private static void updateSkillPickers(JPanel skillPanel, Class[] classlist, JComboBox classPicker, Fighter fighter){
        skillPanel.remove(skillPickers);
        skillPickers = skillPickers(classlist[classPicker.getSelectedIndex()], fighter);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        skillPanel.add(skillPickers, constraints);
        skillPanel.revalidate();
        skillPanel.repaint();
    }

    private static void setSkill(Fighter fighter, boolean active, JComboBox<String> select, JTextArea desc){
        if(active) {
            fighter.abilities[ABILITY_MODULE.ACTIVE] = select.getItemAt(select.getSelectedIndex());
        } else {
            fighter.abilities[ABILITY_MODULE.PASSIVE] = select.getItemAt(select.getSelectedIndex());
        }
        desc.setText(ABILITY_MODULE.skillDesc(select.getItemAt(select.getSelectedIndex())));
    }

    private static JPanel skillPickers(Class fighterClass, Fighter fighter){
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
        activeSkill.addActionListener(e -> setSkill(fighter, true, activeSkill, activeDesc));
        passiveSkill.addActionListener(e -> setSkill(fighter, false, passiveSkill, passiveDesc));
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
        name.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fighter.weapon.name = name.getText();
            }
        });
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
        BattleState f1state = new BattleState(fighter1, fighter2, doubles);
        BattleState f2state = new BattleState(fighter2, fighter1, doubles);
        if (first == 0) {
            return noDoubles(f1state, f2state);
        } else if(first == 2){
            return doubles(f1state, f2state);
        } else if(first == 1){
            return noDoubles(f1state, f2state);
        } else {
            return doubles(f1state, f2state);
        }
    }

    private static String noDoubles(BattleState f1state, BattleState f2state){
        int roundCnt = 1;
        while(f1state.fighterHP > 0 && f2state.fighterHP > 0){
            roundHealing(f1state);
            roundHealing(f2state);
            simulationText.append(String.format("%s%d%s", "\nRound: ", roundCnt, "\n"));
            roundCnt++;
            attack(f1state, f2state);
            if(f1state.fighterHP <= 0 ||f2state.fighterHP <= 0){
                break;
            }
            attack(f2state, f1state);
            damageOverTime(f1state, f2state);
            damageOverTime(f2state, f1state);
        }
        if(f1state.fighterHP > 0){
            simulationText.append(f1state.fighter.name);
        } else {
            simulationText.append(f2state.fighter.name);
        }
        simulationText.append(" wins!");
        return simulationText.toString();
    }
    private static String doubles(BattleState f1state, BattleState f2state){
        int roundCnt = 1;
        while(f1state.fighterHP > 0 && f2state.fighterHP > 0){
            roundHealing(f1state);
            roundHealing(f2state);
            simulationText.append(String.format("%s%d%s", "\nRound: ", roundCnt, "\n"));
            roundCnt++;
            attack(f1state, f2state);
            if(f1state.fighterHP <= 0 ||f2state.fighterHP <= 0){
                break;
            }
            attack(f2state, f1state);
            if(f1state.fighterHP <= 0 ||f2state.fighterHP <= 0){
                break;
            }
            attack(f1state, f2state);
            damageOverTime(f1state, f2state);
            damageOverTime(f2state, f1state);
        }
        if(f1state.fighterHP > 0){
            simulationText.append(f1state.fighter.name);
        } else {
            simulationText.append(f2state.fighter.name);
        }
        simulationText.append(" wins!");
        return simulationText.toString();
    }

    private static void attack(BattleState attacker, BattleState defender){
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
        String connection = "Miss!";
        String counter = "";
        String recovery = "";
        String selfDamage = "";
        boolean defense = false;
        boolean offense = false;
        int damage = 0;

        if(attacker.abilities.onAttack()){
            if(activation < attacker.abilities.getActivation()){
                attacker.active = attacker.abilities.activeCall();
                attacker.duration = attacker.abilities.getDuration();
                if(attacker.doubles){
                    attacker.duration *= 2;
                }
                offense = true;
                offensiveSkill = attacker.fighter.abilities[ABILITY_MODULE.ACTIVE].toUpperCase() + " ";
            }
        }
        if(!defender.abilities.onAttack()){
            activation = rand.nextInt(99);
            if(activation < defender.abilities.getActivation()){
                defender.active = defender.abilities.activeCall();
                defender.duration = defender.abilities.getDuration();
                defense = true;
                if(attacker.doubles){
                    defender.duration *= 2;
                }
                defensiveSkill = "(" + defender.fighter.name + " " +
                        defender.fighter.abilities[ABILITY_MODULE.ACTIVE].toUpperCase() + ") ";
            }
        }
        int hitThresh = attacker.stats.hit + attacker.active[ABILITY_MODULE.HITUP]
                + attacker.passive[ABILITY_MODULE.HITUP] - defender.active[ABILITY_MODULE.AVOIDUP] -
                defender.passive[ABILITY_MODULE.AVOIDUP];
        if(hit < hitThresh){
            connection = "Hit! ";
            damage = attacker.stats.power + attacker.active[ABILITY_MODULE.DAMAGEUP]
                    + attacker.passive[ABILITY_MODULE.DAMAGEUP] - defender.active[ABILITY_MODULE.DAMAGEREDUCTION] -
                    defender.passive[ABILITY_MODULE.DAMAGEREDUCTION];
            int critThresh = attacker.stats.crit + attacker.active[ABILITY_MODULE.CRITUP]
                    + attacker.passive[ABILITY_MODULE.CRITUP] - defender.active[ABILITY_MODULE.DDGUP] -
                    defender.passive[ABILITY_MODULE.DDGUP];
            if((!offense || attacker.abilities.canCrit()) && crit < critThresh){
                if(attacker.fighter.abilities[ABILITY_MODULE.PASSIVE].toLowerCase().equals("precision")){
                    damage *= 3;
                }
                damage *= CRIT_MODIFIER;
                critical = "CRITICAL ";
            }
            if(damage < 0){
                damage = 0;
            }
            defender.fighterHP -= damage;
            if(defense &&
                    (defender.fighterHP > 0 && defender.fighter.abilities[ABILITY_MODULE.PASSIVE].equals("Counter"))){
                counter = "\n" + defender.fighter.name +
                        "COUNTERS! (" + damage/2 + "/" + attacker.fighterHP + ")\n";
            }
        }
        if(attacker.active[ABILITY_MODULE.BONUSHEALING] != 0){
            int healing = (damage*attacker.active[ABILITY_MODULE.BONUSHEALING])/100;
            attacker.fighterHP += healing;
            recovery = String.format("%s%s%d%s%d%s", attacker.fighter.name,
                    " recovers ", healing, "HP! (", attacker.fighterHP, ")\n");
        }
        if(attacker.passive[ABILITY_MODULE.SELFDAMAGE] != 0){
            attacker.fighterHP -= attacker.passive[ABILITY_MODULE.SELFDAMAGE];
            selfDamage = String.format("%s%s%d%s%d%s", attacker.fighter.name, " hurts themselves for ",
                    attacker.passive[ABILITY_MODULE.SELFDAMAGE], " damage. (", attacker.fighterHP, ")\n");
        }

        if(defender.fighter.abilities[ABILITY_MODULE.ACTIVE].toLowerCase().equals("guts") && defense){
            if(defender.fighterHP <= 0) {
                defender.fighterHP = defender.active[ABILITY_MODULE.BONUSHP];
            } else {
                defensiveSkill = "";
            }
        }

        simulationText.append(String.format("%s%s%s%s%s%s%s%d%s%d%s%s%s\n%s", attacker.fighter.name, ": ",
                defensiveSkill, offensiveSkill, critical,
                connection, "(", damage, "/", defender.fighterHP, ")", selfDamage, recovery, counter));

        if(attacker.duration > 0 && attacker.abilities.onAttack()){
            attacker.duration--;
        } else {
            Arrays.fill(attacker.active, 0);
        }

    }

    private static void roundHealing(BattleState attacker){
        if(attacker.passive[ABILITY_MODULE.BONUSHEALING] != 0){
            if(attacker.fighterHP < attacker.fighter.hp()){
                attacker.fighterHP += attacker.passive[ABILITY_MODULE.BONUSHEALING];
                if(attacker.fighterHP > attacker.fighter.hp()){
                    attacker.fighterHP = attacker.fighter.hp();
                }
                simulationText.append(String.format("%s%s%s%d%s%d%s", "\n", attacker.fighter.name, " recovers ",
                        attacker.passive[ABILITY_MODULE.BONUSHEALING], "HP (", attacker.fighterHP, ")\n"));
            }
        }
    }

    private static void damageOverTime(BattleState attacker, BattleState defender){
        int damage = 0;
        if(attacker.active[ABILITY_MODULE.DOT] != 0){
            for(int i = 0; i < defender.damageOverTime.length; i++){
                if(defender.damageOverTime[i] == 0){
                    defender.damageOverTime[i] = 1;
                    defender.damageOverTime[i]--;
                }
            }
        }
        for(int i = 0; i < defender.damageOverTime.length; i++){
            if(defender.damageOverTime[i] > 0){
                damage += attacker.active[ABILITY_MODULE.DOT];
            }
        }
        defender.fighterHP -= damage;
        if( damage > 0) {
            simulationText.append(String.format("%s%s%s%d%s", "\n", defender.fighter.name, " takes ", damage,
                    "from damage over time.\n"));
        }
    }

    private static class BattleState{
        int fighterHP;
        int duration;
        int[] damageOverTime;
        Fighter fighter;
        boolean doubles;
        int attackNum;
        AbilityModule abilities;
        BattleStats stats;
        int[] active;
        int[] passive;
        BattleState(Fighter fighter1, Fighter fighter2, boolean doubles){
            this.fighterHP = fighter1.hp();
            this.doubles = doubles;
            this.fighter = fighter1;
            this.attackNum = 0;
            this.abilities = new AbilityModule(fighter1, fighter2, fighterHP, fighter2.hp());
            this.stats = new BattleStats(fighter1, fighter2);
            active = Arrays.copyOf(BASE, BASE.length);
            passive = this.abilities.passiveCall();
            damageOverTime = new int[] {0,0,0,0,0};
        }
    }




    private static String printReadout(BattleStats f1, BattleStats f2){
        return (String.format("%12s%15s\n", fighter1.name, fighter2.name))
                + (String.format("%-4s:%7s%s%-4s:%7s\n", "MT", f1.power, "   ", "MT", f2.power))
                + (String.format("%-4s:%7s%s%-4s:%7s\n", "Hit", f1.hit, "   ", "Hit", f2.hit))
                + (String.format("%-4s:%7s%s%-4s:%7s\n", "Crit", f1.crit, "   ", "Crit", f2.crit));
    }
}
