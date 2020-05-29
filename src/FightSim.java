/*  Colosseum Tournament's Fighting Simulator Component
    Methods for building of UI and loading of fighters from file
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

public class FightSim {
    private static Fighter fighter1;
    private static Fighter fighter2;
    private static JComboBox<String> fighter1Select;
    private static JComboBox<String> fighter2Select;
    private static JPanel skillPickers;
    private final static int TOTALALLOWED = 130;
    private final static int WEAPONALLOWED = 32;
    private final static int STAT_CAP = 45;
    private static final AbilityModule ABILITY_MODULE = new AbilityModule();
    private static JFrame mainFrame;
    private static ArrayList<JTextArea> textAreaList;

    //It's pretty self explanatory
    public static void main(String[] args){
        buildUI();
    }

    //Builds the UI for the main screen of the application
    private static void buildUI(){
        //List of textAreas so that the text can be edited later
        textAreaList = new ArrayList<>();
        //Name of the frame
        mainFrame = new JFrame("Colosseum Tournament");
        //Selection drop downs for the two fighters
        fighter1Select = new JComboBox<>(getFighterFiles());
        fighter2Select = new JComboBox<>(getFighterFiles());
        //Buttons for editing fighters
        JButton createFighter = new JButton("Create new fighter");
        JButton editFighter1 = new JButton("Edit Fighter 1");
        JButton editFighter2 = new JButton("Edit Fighter 2");
        //Tries to load in fighters based on the fighters selected
        try {
            fighter1 = loadFighter(fighter1Select.getItemAt(fighter1Select.getSelectedIndex()));
            fighter2 = loadFighter(fighter2Select.getItemAt(fighter1Select.getSelectedIndex()));
        } catch(Exception e){
            //Creates an error window if something goes wrong
            throwError("The fighter file could not be read");
        }
        //Creates readouts for the fighter stats
        JPanel fighter1Stats = new JPanel();
        if(fighter1 != null) {
            fighter1Stats = fighterReadout(fighter1);
        }
        JPanel fighter2Stats = new JPanel();
        if(fighter2 != null) {
            fighter2Stats = fighterReadout(fighter2);
        }
        //Button to run a battle between the two selected fighters
        JButton runSim = new JButton("Run simulation");
        //Sets the layout of the window
        mainFrame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        //Fighter 1 select is in row 0 column 0
        constraints.gridx = 0;
        constraints.gridy = 0;
        mainFrame.add(fighter1Select, constraints);
        //Fighter 2 select is in row 0 column 1
        constraints.gridx = 1;
        constraints.gridy = 0;
        mainFrame.add(fighter2Select, constraints);
        //The stats of the first fighter go below the selection of fighter 1
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.ipadx = 60;
        constraints.gridheight = 3;
        mainFrame.add(fighter1Stats, constraints);
        //THe stats of the second fighter go below the selection of fighter 2
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.ipadx = 60;
        constraints.gridheight = 3;
        mainFrame.add(fighter2Stats, constraints);
        //The Buttons for creation, editing, and running the simulation go on the far left
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
        //Program ends if closed
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(700,400);
        mainFrame.setVisible(true);
        //Actions for each of the components.
        fighter1Select.addActionListener(e -> selectionChange(mainFrame, fighter1Select, 0));
        fighter2Select.addActionListener(e -> selectionChange(mainFrame, fighter2Select, 1));
        createFighter.addActionListener(e -> createFighterWindow(new Fighter()));
        editFighter1.addActionListener(e -> createFighterWindow(fighter1));
        editFighter2.addActionListener(e -> createFighterWindow(fighter2));
        runSim.addActionListener(e -> simulate());
    }

    //When the simulate button in the main UI is pressed, this method is run
    private static void simulate(){
        //Puts the simulation into a new window
        JFrame frame = new JFrame("Simulation");
        //Creates the text area that the simulation goes in. The simulation
        //consists of battle stats for each fighter and text output of a simulation
        //between the two fighers (see Simulator.java)
        JTextArea battleOutput = new JTextArea(printReadout(
                new BattleStats(fighter1, fighter2, 1), new BattleStats(fighter2, fighter1, 1))
                + new Simulator().runSim(fighter1, fighter2));
        //Puts it in a scrollpane so that long simulations can be read
        JScrollPane scrollPane = new JScrollPane(battleOutput);
        frame.add(scrollPane);
        frame.setSize(400, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    //method that opens UI to make a new fighter
    private static void createFighterWindow(Fighter newFighter){
        JFrame frame = new JFrame("Create Fighter");
        //Readout of important battle statistics for a fighter
        JTextArea readout = battleStatPanel(newFighter);
        //Panel that has all the stats of a fighter and allows editing of stats
        JPanel stats = statsPanel(newFighter, readout);
        //Panel that has info on a fighter weapon
        JPanel weapon = weaponPanel(newFighter, readout);
        //Panel that contains class and skill selection options
        JPanel skills = skillPicker(newFighter, readout);
        //Save button
        JButton save = new JButton("Save and complete");
        JTextField name = new JTextField(newFighter.name);
        //Layout setup
        frame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        //The skill selection goes at the bottom of the window in row 4.
        constraints.gridx = 0;
        constraints.gridy = 4;
        //It is 2 columns wide
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        //It has high weight
        constraints.weightx = 0.75;
        frame.add(skills, constraints);
        //Name is at the top of the window
        constraints.gridx = 0;
        constraints.gridy = 0;
        //Two columns wide and fills them
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        frame.add(name, constraints);
        //The stats of the fighter is the first column in the second row
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridy = 1;
        constraints.gridheight = 2;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        //Shares the same weight as all items in the second row
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
        constraints.gridheight = 2;
        constraints.gridwidth = 1;
        frame.add(readout,constraints);
        constraints.gridx = 2;
        constraints.gridy = 4;
        constraints.gridheight = 1;
        frame.add(save, constraints);
        //Action for the save button
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Writes a file based on the Fighter passed into the create fighter window.
                newFighter.name = name.getText();
                try {
                    newFighter.writeFile();
                } catch(Exception ex){
                    throwError("Error writing file");
                }
                //Has to rebuild the main UI so that the new fighter is
                //added to the selection lists
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
        //Gets the list of classes that can be chosen for a combo box
        Class[] classlist = classes.classList();
        JComboBox classPicker = new JComboBox<>(classes.classNames());
        //Creates the panel that contains the combo boxes for active and passive skills
        skillPickers = skillPickers(classlist[classPicker.getSelectedIndex()], fighter, readout);
        //Sets up the layout of the panel
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
        //When the class is changed, need to update the skill combo boxes to be the skills of the selected class
        classPicker.addActionListener(e -> updateSkillPickers(skillPanel, classlist, classPicker, fighter, readout));
        return skillPanel;
    }

    //Updates the skill pickers when the class is changed
    private static void updateSkillPickers(JPanel skillPanel, Class[] classlist,
                                           JComboBox classPicker, Fighter fighter, JTextArea readout){
        //Need to remove and then add new skillpickers as JCombo boxes can't easily be reset
        skillPanel.remove(skillPickers);
        skillPickers = skillPickers(classlist[classPicker.getSelectedIndex()], fighter, readout);
        //Resets the layout for the skillpickers
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.weightx = 1.0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        skillPanel.add(skillPickers, constraints);
        //Redraws the window with the new info
        skillPanel.revalidate();
        skillPanel.repaint();
    }

    //When a skill is selected, sets the skill of the fighter to that skill
    private static void setSkill(Fighter fighter, boolean active, JComboBox<String> select,
                                 JTextArea desc, JTextArea readout){
        if(active) {
            //If we are setting an active skill, set the active ability to the selected skill
            fighter.abilities[ABILITY_MODULE.ACTIVE] = select.getItemAt(select.getSelectedIndex());
        } else {
            //If we are setting a passive skill, set the passive skill ability to the selected skill
            fighter.abilities[ABILITY_MODULE.PASSIVE] = select.getItemAt(select.getSelectedIndex());
            //Many passives change battle stats, so the readout is refreshed when passive is updated
            refreshBattleStats(readout, fighter);
        }
        //Sets the description text to match the selected ability
        desc.setText(ABILITY_MODULE.skillDesc(select.getItemAt(select.getSelectedIndex())));
    }

    //Creates a panel that contains a combo box for selecting active and passive skills
    private static JPanel skillPickers(Class fighterClass, Fighter fighter, JTextArea readout){
        JPanel skillPanel = new JPanel();
        //Skill picker
        JComboBox<String> activeSkill = new JComboBox<>(fighterClass.activeSkills);
        //Description of the skill
        JTextArea activeDesc = new JTextArea(
                ABILITY_MODULE.skillDesc(activeSkill.getItemAt(activeSkill.getSelectedIndex())));
        JComboBox<String> passiveSkill = new JComboBox<>(fighterClass.passiveSkills);
        JTextArea passiveDesc = new JTextArea(
                ABILITY_MODULE.skillDesc(passiveSkill.getItemAt(passiveSkill.getSelectedIndex())));
        skillPanel.setLayout(new GridBagLayout());
        //Makes text in the descriptions wrap on words
        activeDesc.setLineWrap(true);
        passiveDesc.setLineWrap(true);
        activeDesc.setWrapStyleWord(true);
        passiveDesc.setWrapStyleWord(true);
        //Sets the layout of the window
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
        //Adds action listeners for the skill selectors
        activeSkill.addActionListener(e -> setSkill(fighter, true, activeSkill, activeDesc, readout));
        passiveSkill.addActionListener(e -> setSkill(fighter, false, passiveSkill, passiveDesc, readout));
        return skillPanel;
    }

    //Panel that displays the current stats of a fighter
    private static JTextArea battleStatPanel(Fighter fighter){
        //Creates a new text area with battle stats in it
        JTextArea info = new JTextArea(battleStatText(fighter));
        info.setFont(new Font("Courier New", Font.PLAIN, 12));
        info.setSize(200,150);
        return info;
    }

    //Returns a string that can be used in a battleStatPanel
    private static String battleStatText(Fighter fighter){
        //Cumulative stats that are important for battle
        int[] cumstats = fighter.calcFinals();
        /*Formats the text to look like:
        Might:            12
        Hit:             100
        Avoid:            30
        Critical:          5
        Critical Avoid:   10

        Stat Points:   62/130
W       eapon Points: 16/32
        */
        //The battlestats of a fighter are updated every time that a stat is changed
        //Sets the attacker and defender of the ability module to be the fighter
        //(Used to calculate the effects of passive skills on battle stats)
        ABILITY_MODULE.setAttacker(fighter);
        ABILITY_MODULE.setDefender(fighter);
        //Gets the effects of the passive
        int[] passives = ABILITY_MODULE.passiveCall(fighter.abilities[ABILITY_MODULE.PASSIVE]);
        //Adds the effects of the passive to their relevant cumstats
        cumstats[0] += passives[ABILITY_MODULE.DAMAGEUP];
        cumstats[1] += passives[ABILITY_MODULE.HITUP];
        cumstats[2] += passives[ABILITY_MODULE.AVOIDUP];
        cumstats[3] += passives[ABILITY_MODULE.CRITUP];
        cumstats[4] += passives[ABILITY_MODULE.DDGUP];
        //Resets the text of the text area in the same format as it was originally made in
        return String.format(
                "%-16s%4d\n%-16s%4d\n%-16s%4d\n%-16s%4d\n%-16s%4d\n\n%-15s%2d/%2d\n%-15s%2d/%2d",
                "Might:", cumstats[0], "Hit:", cumstats[1], "Avoid:", cumstats[2],
                "Critical:", cumstats[3], "Critical Avoid:", cumstats[4],
                "Stat Points:", fighter.calcPoints(), TOTALALLOWED,
                "Weapon Points:", fighter.weapon.calcPoints(), WEAPONALLOWED);

    }

    //Method that refreshes the BattleStat panel
    private static void refreshBattleStats(JTextArea battleStats, Fighter fighter){
        //The battlestats of a fighter are updated every time that a stat is changed
        battleStats.setText(battleStatText(fighter));
    }

    //Creates a set of panels for each of the weapon stats
    private static JPanel weaponPanel(Fighter fighter, JTextArea readout){
        JPanel weapon = new JPanel();
        JTextField name = new JTextField("Enter a weapon name");
        weapon.add(name);
        //For each of the weapon stats, creates a new stat panel
        for(int i = 0; i < Fighter.NUMWSTATS; i++){
            weapon.add(statPanel(fighter, i, true, readout));
        }
        //Creates a selection for choosing a physical or magical weapon type
        JComboBox<String> weaponType = new JComboBox<>(new String[]{"Physical","Magical"});
        weapon.add(weaponType);
        //Lays out the elements in a 5 by 1 grid.
        weapon.setLayout(new GridLayout(5,1));
        //action for weapon type records the type of weapon
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
        //action for setting a name sets the name
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
        for(int i = 0; i < Fighter.NUMSTATS; i++){
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
        JLabel value;
        //Sets the text for name and value
        if(weapon){
            //If the stat panel is for a weapon stat
            name = new JLabel(fighter.weapon.wnames[statNum]);
            value = new JLabel(fighter.weapon.wstats[statNum] + "");
        } else {
            //If not for a weapon stat
            name = new JLabel(fighter.statNames[statNum]);
            value = new JLabel(fighter.fstats[statNum] + "");
        }
        //Sets the sizes of the labels
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
        //Action listeners for the statControls. up has a mod of 1, down a mod of -1.
        up.addActionListener(e -> statControls(fighter, statNum, 1, value, weapon, readout));
        down.addActionListener(e-> statControls(fighter, statNum, -1, value, weapon, readout));
        //Configures the layout of the stats
        stat.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        //Name and value are each 2 columns wide and 2 rows tall
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
        //The buttons are only 1 column wide and 1 row tall so that they are smaller
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
                                     JLabel value, boolean weapon, JTextArea readout){
        if(weapon){
            //If the stat being modified is for a weapon
            //Check that the build isn't using too many points
            if(fighter.weapon.calcPoints()+mod <= WEAPONALLOWED) {
                if (statNum == Fighter.HIT) {
                    //Hit is increased by a different value per point
                    mod *= Fighter.HITINC;
                } else if (statNum == Fighter.CRIT) {
                    //Crit is increased by a different value per point
                    mod *= Fighter.CRITINC;
                }
                //Makes sure that stats do not go into the negative.
                if (fighter.weapon.wstats[statNum] + mod >= 0) {
                    fighter.weapon.wstats[statNum] += mod;
                    value.setText(fighter.weapon.wstats[statNum] + "");
                }
            }
        } else {
            //Checks that the build doesn't use too many points
            if(fighter.calcPoints()+ mod <= TOTALALLOWED) {
                if (statNum == Fighter.HP) {
                    //HP is increased by a different value per point
                    mod *= Fighter.HPINC;
                }
                //Checks to make sure that the stat does not go negative and that it does not exceed the stat cap
                if (fighter.fstats[statNum] + mod > 0 && fighter.fstats[statNum] + mod <= STAT_CAP*Math.abs(mod))
                    fighter.fstats[statNum] += mod;
                value.setText(fighter.fstats[statNum] + "");
            }
        }
        //Refreshes the battle stats to reflect any changes made
        refreshBattleStats(readout, fighter);
    }

    //method that runs when a change in the fighter selection is found
    private static void selectionChange(JFrame frame, JComboBox fighter1Select, int num){
        //Awkward solution
        //If num is 1 we're changing fighter2
        try {
            if (num == 1) {
                fighter2 = loadFighter(fighter1Select.getItemAt(fighter1Select.getSelectedIndex()).toString());
                //text areas 2 and 3 are text readouts of fighter 2
                textAreaList.get(2).setText(fighter2.fighterData());
                textAreaList.get(3).setText(battleStatText(fighter2));
            } else {
                //text areas 0 and 1 are text readouts of fighter 1
                fighter1 = loadFighter(fighter1Select.getItemAt(fighter1Select.getSelectedIndex()).toString());
                textAreaList.get(0).setText(fighter1.fighterData());
                textAreaList.get(1).setText(battleStatText(fighter1));
            }
        }
        catch(Exception e){
            throwError("The selected fighter could not be created.");
        }
        frame.revalidate();
        frame.repaint();
    }

    //Builds a panel that includes fighter stats
    private static JPanel fighterReadout(Fighter fighter){
        //Creates the readout for a fighter used on the main UI
        JPanel panel = new JPanel();
        //Two text areas, one is the stats of the fighter
        JTextArea readout = new JTextArea(fighter.fighterData());
        readout.setFont(new Font("Courier New", Font.PLAIN, 12));
        //The other text area is for relevant battle statistics
        JTextArea battleStats = battleStatPanel(fighter);
        panel.setLayout(new GridBagLayout());
        //Sets the layour
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipady = 5;
        panel.add(readout, constraints);
        constraints.ipady = 5;
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(battleStats, constraints);
        //Adds the text areas to the list of text areas so that they can be edited later
        textAreaList.add(readout);
        textAreaList.add(battleStats);
        return panel;
    }

    //Method that gets the list of fighter files out of the directory
    private static String[] getFighterFiles(){
        File fighterFolder  = new File(System.getProperty("user.dir") + "/Fighters");
        //Checks if the folder exists first
        if(fighterFolder.exists()){
            try {
                String[] output = new String[fighterFolder.listFiles().length];
                int i = 0;
                //iterates through the files in the folder and adds them to the output array.
                //Only adds files that end in .txt
                for (final File fighter : fighterFolder.listFiles()) {
                    if(fighter.getName().contains("txt")) {
                        output[i] = fighter.getName();
                        i++;
                    }
                }
                return output;
            } catch(Exception e){
                //Checks for errors and throws an error window if one occurred
                throwError("Error reading files");
                return null;
            }
        } else {
            //Throw an error if there is no fighter directory
            throwError("Could not find fighter directory");
            return new String[]{""};
        }
    }

    //Method used for creating a panel that can display different error messages
    private static void throwError(String errorName){
        JFrame error = new JFrame("Error");
        //Uses a text area because some errors are long
        JTextArea message = new JTextArea(errorName);
        //creates a button to close the error window
        JButton close = new JButton("Close");
        close.addActionListener(e -> error.dispose());
        //Makes sure that messages can wrap
        message.setWrapStyleWord(true);
        message.setLineWrap(true);
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

    //Prints a readout of fighter names and some relevant battle statistics
    private static String printReadout(BattleStats f1, BattleStats f2){
        return (String.format("%12s%15s\n", fighter1.name, fighter2.name))
                + (String.format("%-4s:%7s%s%-4s:%7s\n", "MT", f1.power, "   ", "MT", f2.power))
                + (String.format("%-4s:%7s%s%-4s:%7s\n", "Hit", f1.hit, "   ", "Hit", f2.hit))
                + (String.format("%-4s:%7s%s%-4s:%7s\n", "Crit", f1.crit, "   ", "Crit", f2.crit));
    }
}
