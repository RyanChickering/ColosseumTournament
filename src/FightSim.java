/*  Colosseum Tournament's Fighting Simulator Component
    Stage 1: Loads fighter or creates a new fighter based on the input given
    Stage 2: Gets the fighter's stats into memory
    Stage 3: Conducts a battle between the two fighters
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
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
    private final static int TOTALALLOWED = 130;
    private final static int WEAPONALLOWED = 32;
    private static final AbilityModule ABILITY_MODULE = new AbilityModule();
    private static JFrame mainFrame;
    private static ArrayList<JTextArea> textAreaList;

    //It's pretty self explanatory
    public static void main(String[] args){
        buildUI();
    }

    private static void buildUI(){
        textAreaList = new ArrayList<>();
        mainFrame = new JFrame("Colosseum Tournament");
        fighter1Select = new JComboBox<>(getFighterFiles());
        fighter2Select = new JComboBox<>(getFighterFiles());
        JButton createFighter = new JButton("Create new fighter");
        JButton editFighter1 = new JButton("Edit Fighter 1");
        JButton editFighter2 = new JButton("Edit Fighter 2");
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
        constraints.gridheight = 3;
        mainFrame.add(fighter1Stats, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.ipadx = 60;
        constraints.gridheight = 3;
        mainFrame.add(fighter2Stats, constraints);
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        mainFrame.add(createFighter, constraints);
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        mainFrame.add(editFighter1, constraints);
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        mainFrame.add(editFighter2,constraints);
        constraints.gridx = 2;
        constraints.gridy = 3;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        mainFrame.add(runSim, constraints);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(700,400);
        mainFrame.setVisible(true);
        fighter1Select.addActionListener(e -> selectionChange(mainFrame, fighter1Select, 0));
        fighter2Select.addActionListener(e -> selectionChange(mainFrame, fighter2Select, 1));
        createFighter.addActionListener(e -> createFighterWindow(new Fighter()));
        editFighter1.addActionListener(e -> createFighterWindow(fighter1));
        editFighter2.addActionListener(e -> createFighterWindow(fighter2));
        runSim.addActionListener(e -> simulate());
    }

    private static void simulate(){
        JFrame frame = new JFrame("Simulation");
        JTextArea battleOutput = new JTextArea(printReadout(
                new BattleStats(fighter1, fighter2, 1), new BattleStats(fighter2, fighter1, 1))
                + new Simulator().runSim(fighter1, fighter2));
        JScrollPane scrollPane = new JScrollPane(battleOutput);
        frame.add(scrollPane);
        frame.setSize(400, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    //method that opens UI to make a new fighter
    private static void createFighterWindow(Fighter newFighter){
        JFrame frame = new JFrame("Create Fighter");
        JTextArea readout = battleStatPanel(newFighter);
        JPanel stats = statsPanel(newFighter, readout);
        JPanel weapon = weaponPanel(newFighter, readout);
        JPanel skills = skillPicker(newFighter, readout);
        JButton save = new JButton("Save and complete");
        JTextField name = new JTextField(newFighter.name);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.75;
        frame.add(skills, constraints);
        constraints.gridx = 0;
        constraints.ipadx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        frame.add(name, constraints);
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridy = 1;
        constraints.gridheight = 2;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.25;
        frame.add(stats, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.ipadx = 0;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        frame.add(weapon, constraints);
        constraints.gridx = 2;
        constraints.gridy = 0;
        //constraints.ipadx = 100;
        constraints.gridheight = 2;
        constraints.gridwidth = 1;
        frame.add(readout,constraints);
        constraints.gridx = 2;
        constraints.gridy = 4;
        constraints.gridheight = 1;
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
                mainFrame.dispose();
                frame.dispose();
                buildUI();
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(700,400);
        frame.setVisible(true);
    }

    //Creates the panel that contains the class picker and skill pickers
    private static JPanel skillPicker(Fighter fighter, JTextArea readout){
        JPanel skillPanel = new JPanel();
        Class classes = new Class();
        Class[] classlist = classes.classList();
        JComboBox classPicker = new JComboBox<>(classes.classNames());
        skillPickers = skillPickers(classlist[classPicker.getSelectedIndex()], fighter, readout);
        skillPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        skillPanel.add(classPicker, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        skillPanel.add(skillPickers, constraints);
        classPicker.addActionListener(e -> updateSkillPickers(skillPanel, classlist, classPicker, fighter, readout));
        return skillPanel;
    }

    //Updates the skill pickers when the class is changed
    private static void updateSkillPickers(JPanel skillPanel, Class[] classlist,
                                           JComboBox classPicker, Fighter fighter, JTextArea readout){
        skillPanel.remove(skillPickers);
        skillPickers = skillPickers(classlist[classPicker.getSelectedIndex()], fighter, readout);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.weightx = 1.0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        skillPanel.add(skillPickers, constraints);
        skillPanel.revalidate();
        skillPanel.repaint();
    }

    //When a skill is selected, sets the skill of the fighter to that skill
    private static void setSkill(Fighter fighter, boolean active, JComboBox<String> select,
                                 JTextArea desc, JTextArea readout){
        if(active) {
            fighter.abilities[ABILITY_MODULE.ACTIVE] = select.getItemAt(select.getSelectedIndex());
        } else {
            fighter.abilities[ABILITY_MODULE.PASSIVE] = select.getItemAt(select.getSelectedIndex());
            refreshBattleStats(readout, fighter);
        }
        desc.setText(ABILITY_MODULE.skillDesc(select.getItemAt(select.getSelectedIndex())));
    }

    private static JPanel skillPickers(Class fighterClass, Fighter fighter, JTextArea readout){
        JPanel skillPanel = new JPanel();
        JComboBox<String> activeSkill = new JComboBox<>(fighterClass.activeSkills);
        JTextArea activeDesc = new JTextArea(
                ABILITY_MODULE.skillDesc(activeSkill.getItemAt(activeSkill.getSelectedIndex())));
        JComboBox<String> passiveSkill = new JComboBox<>(fighterClass.passiveSkills);
        JTextArea passiveDesc = new JTextArea(
                ABILITY_MODULE.skillDesc(passiveSkill.getItemAt(passiveSkill.getSelectedIndex())));
        skillPanel.setLayout(new GridBagLayout());
        activeDesc.setLineWrap(true);
        passiveDesc.setLineWrap(true);
        activeDesc.setWrapStyleWord(true);
        passiveDesc.setWrapStyleWord(true);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
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
        activeSkill.addActionListener(e -> setSkill(fighter, true, activeSkill, activeDesc, readout));
        passiveSkill.addActionListener(e -> setSkill(fighter, false, passiveSkill, passiveDesc, readout));
        return skillPanel;
    }

    //Panel that displays the current stats of a fighter
    private static JTextArea battleStatPanel(Fighter fighter){
        JTextArea info = new JTextArea(battleStatText(fighter));
        info.setFont(new Font("Courier New", Font.PLAIN, 12));
        info.setSize(200,150);
        return info;
    }

    private static String battleStatText(Fighter fighter){
        int[] cumstats = fighter.calcFinals();
        return String.format(
                "%-16s%4d\n%-16s%4d\n%-16s%4d\n%-16s%4d\n%-16s%4d\n\n%-15s%2d/%2d\n%-15s%2d/%2d",
                "Might:", cumstats[0], "Hit:", cumstats[1], "Avoid:", cumstats[2],
                "Critical:", cumstats[3], "Critical Avoid:", cumstats[4],
                "Stat Points:", fighter.calcPoints(), TOTALALLOWED,
                "Weapon Points:", fighter.weapon.calcPoints(), WEAPONALLOWED
        );

    }

    //Method that refreshes the BattleStat panel
    private static void refreshBattleStats(JTextArea battleStats, Fighter fighter){
        int[] cumstats = fighter.calcFinals();
        ABILITY_MODULE.setAttacker(fighter);
        ABILITY_MODULE.setDefender(fighter);
        int[] passives = ABILITY_MODULE.passiveCall(fighter.abilities[ABILITY_MODULE.PASSIVE]);
        cumstats[0] += passives[ABILITY_MODULE.DAMAGEUP];
        cumstats[1] += passives[ABILITY_MODULE.HITUP];
        cumstats[2] += passives[ABILITY_MODULE.AVOIDUP];
        cumstats[3] += passives[ABILITY_MODULE.CRITUP];
        cumstats[4] += passives[ABILITY_MODULE.DDGUP];
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
        JComboBox<String> weaponType = new JComboBox<>(new String[]{"Physical","Magical"});
        weapon.add(weaponType);
        weapon.setLayout(new GridLayout(5,1));
        weaponType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(weaponType.getItemAt(weaponType.getSelectedIndex()).equals("Physical")) {
                    fighter.weapon.type = 0;
                } else {
                    fighter.weapon.type = 1;
                }
            }
        });
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
        constraints.gridheight = 2;
        constraints.gridwidth = 2;
        stat.add(name, constraints);
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridheight = 2;
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
            textAreaList.get(2).setText(fighter2.fighterData());
            textAreaList.get(3).setText(battleStatText(fighter2));
        } else {
            fighter1 = loadFighter(fighter1Select.getItemAt(fighter1Select.getSelectedIndex()).toString());
            textAreaList.get(0).setText(fighter1.fighterData());
            textAreaList.get(1).setText(battleStatText(fighter1));
        }
        frame.revalidate();
        frame.repaint();
    }

    //Builds a panel that includes fighter stats
    private static JPanel fighterReadout(Fighter fighter){
        JPanel panel = new JPanel();
        JTextArea readout = new JTextArea(fighter.fighterData());
        readout.setFont(new Font("Courier New", Font.PLAIN, 12));
        JTextArea battleStats = battleStatPanel(fighter);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipady = 5;
        panel.add(readout, constraints);
        constraints.ipady = 5;
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(battleStats, constraints);
        textAreaList.add(readout);
        textAreaList.add(battleStats);
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
        String fighterName = lines.get(0).substring(lines.get(0).lastIndexOf(" ")+1);
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
        Fighter newFighter = new Fighter(stats, abilities, fighterName);
        newFighter.weapon.type = Integer.parseInt(lines.get(10).substring(lines.get(10).lastIndexOf(' ') + 1));
        return newFighter;
    }

    private static String printReadout(BattleStats f1, BattleStats f2){
        return (String.format("%12s%15s\n", fighter1.name, fighter2.name))
                + (String.format("%-4s:%7s%s%-4s:%7s\n", "MT", f1.power, "   ", "MT", f2.power))
                + (String.format("%-4s:%7s%s%-4s:%7s\n", "Hit", f1.hit, "   ", "Hit", f2.hit))
                + (String.format("%-4s:%7s%s%-4s:%7s\n", "Crit", f1.crit, "   ", "Crit", f2.crit));
    }
}
