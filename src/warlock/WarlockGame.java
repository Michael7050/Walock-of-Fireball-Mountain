/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package warlock;

import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Jones
 */
public class WarlockGame {

    private static WarlockGame instance = new WarlockGame();
    //load or create char sheet
    //start run page loop
    private final HashMap<String, CharacterSheet> savedGames;
    private final String fileName = "resources/CharacterSheets/CharacterSheetSaves.txt";

    private static databaseManager dbManager;
    private static Connection conn;
    private static Statement statement;



    private WarlockGame() {
        dbManager = new databaseManager();
        conn = dbManager.getConnection();
        this.loadPageData();
        CharacterSheet player = null;
        int tempPageNum = 0;
        GameFrame gameGUI = new GameFrame();
        gameGUI.setVisible(true);
        this.savedGames = new HashMap<>();
        this.getPlayers(fileName);

        //Game Startup Text
        //System.out.println(FileMethods.readText("resources/PageText/gamestart.txt"));
        GameFrame.writeToScreen(FileMethods.readText("resources/PageText/gamestart.txt"));

        //load or create char sheet
        try {
            player = checkPlayer(startSaves());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Start page loop.
        while (true) //this loop runs the whole game page by page.
        {
            int x = player.getPagenum();
            try {
                tempPageNum = (RunPage.runPage(x, player));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            player.setPagenum(tempPageNum);
            saveGame(player); //this loop should never end, and instead end via individual runpage methods.
        }
    }

    public void getPlayers(String fn) //this populates the list of saved games from file
    {
        FileInputStream fin;
        try {
            fin = new FileInputStream(fn);
            Scanner fileScanner = new Scanner(fin);

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                StringTokenizer st = new StringTokenizer(line);
                CharacterSheet player = new CharacterSheet(st.nextToken(), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), st.nextToken());
                this.savedGames.put(player.getName(), player);
            }
            fin.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public CharacterSheet checkPlayer(String name) throws InterruptedException //checks file to see if player already exists, creates new if it does not.
    {
        CharacterSheet player;

        if (this.savedGames.containsKey(name)) {
            player = this.savedGames.get(name);
            //System.out.println("Your current page: " + player.getPagenum());
            GameFrame.writeToScreen("Your current page: " + player.getPagenum());
        } else {
            player = LoadCharacter.createCharacter(name);
            this.savedGames.put(name, player);
        }
        return player;
    }

    public void saveGame(CharacterSheet player) {
        this.savedGames.put(player.getName(), player);
        try {
            FileOutputStream fOut = new FileOutputStream(this.fileName);
            PrintWriter pw = new PrintWriter(fOut);
            for (CharacterSheet x : this.savedGames.values()) {
                pw.println(FileMethods.characterToOutput(x));
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //write a method that lists all current characters, and prompts user to enter a name
    public String startSaves()
    {
        Scanner scan = new Scanner(System.in);
        //System.out.println("Current saved games:");
        GameFrame.writeToScreen("Current saved games:");
        //System.out.println(savedGames.keySet());
        String[] svGame = savedGames.keySet().toArray(new String[0]);
        GameFrame.writeToScreen(svGame);
        //System.out.println("Enter player name to load game, or start new game:");
        GameFrame.writeToScreen("Choose your character");
        chooseChar2 selection = new chooseChar2();
        selection.setVisible(true);

        return chooseChar2.getChar();
    }


    public static WarlockGame getInstance() {
        return instance;
    }

    //checks to see if table exists, and if it does, drops it, to make a new one.
    public void checkTables(String name) {

        try {
            DatabaseMetaData dbmd = this.conn.getMetaData();
            String[] types = {"TABLE"};
            statement = this.conn.createStatement();
            ResultSet results = dbmd.getTables(null, null, null, types);

            while (results.next()) {
                String table_name = results.getString("TABLE_NAME");
                System.out.println(table_name);
                if (table_name.equalsIgnoreCase(name)) {
                    statement.executeUpdate("DROP TABLE " + name);
                    System.out.println("Table" + name + " deleted.");
                    break;
                }
            }
            results.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    //looks in the database for page data
    public static int[] getPageDataFromDB(int pgnum) {
        String[] strData = new String[15];
        int[] intData = new int[15];
        try {
            ResultSet rs = conn.prepareStatement("SELECT * FROM PAGEDATA WHERE pgnum = '" + pgnum + "'").executeQuery();

            while (rs.next()) {
                for (int i = 1; i <= 15; i++) {
                    strData[(i - 1)] = (rs.getString(i));
                }
            }

            if (strData[0] == null) {
                System.out.println("game died");
                return null;
            }

            for (int i = 0; i < strData.length; i++) {
                try {
                    intData[i] = Integer.parseInt(strData[i]);
                } catch (NumberFormatException e) {
                    //replaces empty variables with error value
                    if (strData[i].equals("dest1") || strData[i].equals("dest2") || strData[i].equals("encounternum") || strData[i].equals("dest3") || strData[i].equals("dest4") || strData[i].equals("dest5") || strData[i].equals("pgnum")) {
                        intData[i] = 1;
                    } //replaces empty variables with empty value
                    else if (strData[i].equals("typeofinput") || strData[i].equals("stamgainplus") || strData[i].equals("skillgainplus") || strData[i].equals("stamgain") || strData[i].equals("skillgain") || strData[i].equals("luckgain") || strData[i].equals("gold") || strData[i].equals("provisions")) {
                        intData[i] = 0;
                    } else {
                        intData[i] = 0;
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return intData;
    }

    //creates table and populates it with data for Page Data
    public void loadPageData() {
        try {
            this.statement = conn.createStatement();
            this.checkTables("PAGEDATA");
            this.statement.addBatch(this.createPageData());
            this.statement.executeBatch();
            //this.statement.addBatch("INSERT INTO PAGEDATA VALUES ('1', '1', '71', '278', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5')");
            this.statement.executeBatch();
            this.statement.addBatch(this.insertPageData1());
            this.statement.executeBatch();
            this.statement.addBatch(this.insertPageData1_5());
            this.statement.executeBatch();
            this.statement.addBatch(this.insertPageData2());
            this.statement.executeBatch();
            this.statement.addBatch(this.insertPageData2_5());
            this.statement.executeBatch();
            System.out.println("Table created and populated.");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //creates pagedata table
    private String createPageData() {
        String sql = ("CREATE TABLE \"PAGEDATA\" \n"
                + "(\n"
                + "    \"PGNUM\"\tvarchar(300),\n"
                + "    \"typeofinput\"\tvarchar(300),\n"
                + "    \"dest1\"\tvarchar(300),\n"
                + "    \"dest2\"\tvarchar(300),\n"
                + "    \"encounternum\"\tvarchar(300),\n"
                + "    \"stamgainplus\"\tvarchar(300),\n"
                + "    \"skillgainplus\"\tvarchar(300),\n"
                + "    \"stamgain\"\tvarchar(300),\n"
                + "    \"skillgain\"\tvarchar(300),\n"
                + "    \"dest3\"\tvarchar(300),\n"
                + "    \"luckgain\"\tvarchar(300),\n"
                + "    \"gold\"\tvarchar(300),\n"
                + "    \"provisions\"\tvarchar(300),\n"
                + "    \"dest4\"\tvarchar(300),\n"
                + "    \"dest5\"\tvarchar(300)\n"
                + ")");
        return sql;
    }

    private String insertPageData1() {
        String sql = ("INSERT INTO PAGEDATA VALUES "
                + "    ('1', '1', '71', '278', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('2', '2', '16', '269', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('3', '3', '272', '127', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('4', '1', '46', '332', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('5', '1', '97', '292', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('6', '4', '89', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '2', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('7', '4', '214', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '2', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('8', '5', '189', '273', '1', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('9', '1', '34', '322', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('10', '4', '77', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '2', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('11', '1', '366', '250', 'encounternum', 'stamgainplus', 'skillgainplus', '2', '1', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('12', '6', '256', '364', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('13', '4', '282', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '2', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('14', '7', '117', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('15', '4', '367', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', '6', '1', 'dest3', 'luckgain', 'gold', '-1', 'dest4', 'dest5'),\n"
                + "    ('16', '5', '269', '50', '2', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('17', '8', '380', '144', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '327', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('18', '23', '348', '261', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('19', '10', '317', 'dest2', '1', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('20', '10', '291', '376', '2', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('21', '1', '339', '293', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('22', '4', '4', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '2', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('23', '1', '326', '229', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('24', '11', '360', '135', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('25', '12', '90', '340', 'stamgainplus', 'skillgainplus', 'stamgain', '-1', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5', ''),\n"
                + "    ('26', '4', '371', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('27', '37', '319', 'dest2', '1', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '2', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('28', '4', '351', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', '2', 'dest3', '2', '8', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('29', '4', '375', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('30', '1', '67', '267', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('31', '4', '90', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', '2', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('32', '41', '124', 'dest2', '7', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '2', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('33', '14', '320', '147', '3', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('34', '40', '96', 'dest2', '18', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('35', '1', '136', '361', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('36', '1', '263', '353', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('37', '15', '366', '11', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '277', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('38', '4', '66', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', '2', 'dest4', 'dest5'),\n"
                + "    ('39', '16', '396', 'dest2', '4', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('40', '15', '355', '265', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', '-1', '181', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('41', '5', '135', 'dest2', '5', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('42', '1', '257', '113', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('43', '1', '354', '52', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('44', '4', '399', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', '4', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('45', '4', '90', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('46', '1', '4', '206', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('47', '18', '158', '298', '1', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('48', '1', '391', '60', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('49', '4', 'dest1', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', '-2', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('50', '37', '269', 'dest2', '2', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('51', '4', '287', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '2', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('52', '19', '391', '362', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '354', 'luckgain', 'gold', 'provisions', '234', '291'),\n"
                + "    ('53', '20', '155', '300', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '354', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('54', '1', '308', '179', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('55', '21', '7', '166', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('56', '4', '399', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('57', '15', '16', '2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '119', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('58', '1', '15', '367', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('59', '4', '150', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('60', '4', '48', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('61', '5', '375', '29', '6', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('62', '1', '6', '89', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('63', '1', '281', '10', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('64', '22', 'dest1', 'dest2', '1', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('65', '12', '293', '372', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('66', '1', '104', '99', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('67', '1', '267', '177', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('68', '4', '303', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('69', '4', '244', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('70', '4', '267', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('71', '2', '248', '301', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('72', '4', '319', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('73', '4', '218', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('74', '2', '118', '279', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('75', '37', '93', 'dest2', '14', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '3', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('76', '4', '244', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('77', '25', '345', '18', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('78', '1', '159', '237', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('79', '1', '137', '267', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('80', '26', '129', '123', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '195', 'luckgain', 'gold', 'provisions', '140', 'dest5'),\n"
                + "    ('81', '4', '205', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('82', '27', '208', '33', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '147', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('83', '2', '154', '360', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('84', '15', '204', '280', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '377', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('85', '26', '106', '373', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '318', 'luckgain', 'gold', 'provisions', '59', 'dest5'),\n"
                + "    ('86', '28', '259', '350', '7', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('87', '4', '262', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('88', '1', '216', '384', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('89', '4', '286', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('90', '29', '253', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('91', '13', '20', '131', '5', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('92', '4', '71', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('93', '4', '8', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('94', '1', '260', '329', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('95', '4', '205', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('96', '4', '374', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('97', '15', '334', '247', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '292', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('98', '4', '358', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('99', '4', '383', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5')\n");

        return sql;
    }

    private String insertPageData1_5() {
        String sql = ("INSERT INTO PAGEDATA VALUES "
                + "    ('100', '1', '346', '91', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('101', '4', '327', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('102', '15', '303', '19', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '68', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('103', '1', '252', '359', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('104', '4', '49', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('105', '38', '39', '382', '6', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '368', 'luckgain', 'gold', 'provisions', '194', '215'),\n"
                + "    ('106', '25', '152', '126', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('107', '1', '148', '197', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('108', '5', 'dest1', '185', '8', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('109', '9', '120', '212', '1', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '4', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('110', '4', '319', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', '10', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('111', '4', '249', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('112', '1', '142', '105', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('113', '2', '285', '78', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('114', '4', '359', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('115', '15', '95', '313', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '330', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('116', '30', '42', '378', '3', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('117', '1', '354', '308', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('118', '22', 'dest1', 'dest2', '2', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('119', '42', '269', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('120', '4', '197', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('121', '1', '103', '359', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('122', '15', '268', '282', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '13', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('123', '18', '184', '164', '2', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '140', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('124', '1', '138', '76', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('125', '9', '73', 'dest2', '2', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('126', '1', '152', '26', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('127', '13', '272', '188', '8', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('128', '1', '210', '58', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('129', '4', '104', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('130', '13', '280', 'dest2', '9', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('131', '13', '291', 'dest2', '10', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('132', '37', '319', 'dest2', '3', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('133', '4', '52', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('134', '15', '202', '325', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '87', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('135', '29', '360', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '2', '18', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('136', '4', '229', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('137', '4', '354', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('138', '1', '163', '351', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('139', '13', 'dest1', 'dest2', '12', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('140', '16', 'dest1', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('141', '1', '66', '111', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('142', '5', 'dest1', '396', '4', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('143', '5', '399', '44', '9', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', '-1', 'dest4', 'dest5'),\n"
                + "    ('144', '2', '217', '101', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('145', '37', '363', 'dest2', '4', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '1', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('146', '1', '366', '11', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('147', '4', '208', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '2', '1', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('148', '4', '230', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('149', '15', '181', '265', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '355', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('150', '15', '222', '297', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '133', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('151', '9', '218', '86', '3', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '158', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('152', '5', 'dest1', '371', '10', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('153', '4', '399', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', '-1', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('154', '4', '41', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('155', '40', '300', 'dest2', '19', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('156', '18', '343', '92', '3', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('157', '1', '4', '329', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('158', '31', 'dest1', '218', '11', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('159', '32', '365', '365', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '237', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('160', '4', '267', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('161', 'typeofinput', 'dest1', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('162', '1', '23', '69', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('163', '9', '351', '28', '4', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('164', '1', '129', '236', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('165', '15', '141', '66', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '249', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('166', '18', '218', '158', '4', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('167', '1', '187', '359', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('168', '15', '372', '65', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '293', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('169', '4', '400', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('170', '37', '319', 'dest2', '10', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('171', '1', '337', '187', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('172', '15', '249', '141', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '165', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('173', '16', 'dest1', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5', ''),\n"
                + "    ('174', '4', '198', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('175', '1', '177', '267', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('176', '1', '270', '375', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('177', '15', '52', '391', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '175', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('178', '4', '162', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('179', '5', '54', '258', '28', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('180', '15', '70', '329', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '22', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('181', '1', '355', '265', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', '-1', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('182', '13', 'dest1', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', '-2', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('183', '1', '266', '237', 'encounternum', 'stamgainplus', 'skillgainplus', '5', '1', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('184', '1', '322', '34', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('185', '4', '162', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '2', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('186', '4', '198', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('187', '1', '171', '308', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('188', '5', '209', '342', '13', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('189', '1', '90', '25', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('190', '1', '167', '359', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('191', '15', '308', '392', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '46', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('192', '4', '169', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('193', '1', '93', '338', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('194', '1', '142', '105', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('195', '18', '140', '164', '5', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '9', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('196', '4', '280', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', '5', 'provisions', 'dest4', 'dest5', ''),\n"
                + "    ('197', '1', '48', '295', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('198', '13', 'dest1', 'dest2', '19', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "    ('199', '10', 'dest1', '283', '4', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5')\n");

        return sql;
    }

    private String insertPageData2() {
        String sql = ("INSERT INTO PAGEDATA VALUES "
                + "     ('200', '4', '387', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('201', '37', '293', 'dest2', '13', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', '25', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('202', '4', '87', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', '-1', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('203', '37', '38', '66', '12', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '1', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('204', '13', '130', '280', '21', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '377', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('205', '1', '254', '380', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('206', '1', '284', '341', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('207', '1', '83', '154', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('208', '1', '397', '363', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('209', '18', '158', '47', '6', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('210', '1', '225', '357', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('211', '38', '173', '360', '7', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('212', '1', '369', '120', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('213', '9', '36', '314', '5', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('214', '15', '271', '104', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '99', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('215', '1', '142', '105', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('216', '29', '384', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', '4', '66', 'dest3', '66', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('217', '4', '118', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('218', '26', '3', '386', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '209', 'luckgain', 'gold', 'provisions', '316', 'dest5'),\n"
                + "     ('219', '4', '182', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('220', '4', '171', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('221', '19', '72', '132', '6', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '27', 'luckgain', 'gold', 'provisions', '110', '170'),\n"
                + "     ('222', '4', '85', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('223', '1', '53', '300', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('224', '4', '118', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('225', '1', '77', '63', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('226', '4', '267', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('227', '19', '131', '291', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '100', 'luckgain', 'gold', 'provisions', '20', '291'),\n"
                + "     ('228', '4', '85', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('229', '4', '69', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('230', '16', '64', '390', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('231', '4', '182', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('232', '4', '375', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('233', '4', '198', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('234', '7', '43', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('235', '1', '176', '5', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('236', '10', 'dest1', '395', '5', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('237', '4', '285', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('238', '15', '70', '180', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '329', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('239', '1', '88', '149', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '1', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('240', '5', 'dest1', '145', '20', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('241', '4', '90', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', '-1', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('242', '1', '379', '139', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('243', '9', '128', 'dest2', '6', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('244', '13', '143', '399', '24', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('245', '4', '198', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('246', '15', '329', '180', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '70', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('247', '4', '292', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', '-2', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('248', '5', 'dest1', '301', '21', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('249', '16', '66', '304', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('250', '4', '366', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('251', '5', '399', '344', '22', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('252', '1', '312', '226', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('253', '15', '328', '125', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '73', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('254', '26', '352', '333', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '279', 'luckgain', 'gold', 'provisions', '380', 'dest5'),\n"
                + "     ('255', '1', '193', '93', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('256', '15', '398', '297', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '114', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('257', '1', '168', '293', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('258', '37', '54', 'dest2', '15', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '2', '8', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('259', '4', '7', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', '1', 'dest3', '2', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('260', '1', '359', '329', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('261', '4', '345', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('262', '1', '199', '251', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('263', '4', '314', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '1', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('264', '38', '80', '129', '1', 'stamgainplus', 'skillgainplus', 'stamgain', '-1', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('265', '4', '88', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('266', '37', '237', 'dest2', '16', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '1', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('267', '26', '312', '246', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '79', 'luckgain', 'gold', 'provisions', '349', 'dest5'),\n"
                + "     ('268', '1', '13', '282', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('269', '4', '225', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('270', '15', '61', '394', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '375', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('271', '1', '336', '214', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('272', '4', '7', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('273', '37', '189', 'dest2', '5', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('274', '15', '324', '356', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '98', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('275', '9', '230', 'dest2', '7', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('276', '4', '182', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('277', '15', '146', '366', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '11', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('278', '1', '156', '92', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('279', '38', '380', '17', '2', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '333', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('280', '4', '311', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('281', '4', '10', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('282', '16', '115', 'dest2', '31', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('283', '4', '251', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('284', '1', '46', '392', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('285', '1', '213', '314', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('286', '26', '294', '275', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '148', 'luckgain', 'gold', 'provisions', '107', 'dest5'),\n"
                + "     ('287', '38', '32', '309', '3', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('288', '4', '182', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('289', '5', 'dest1', '396', '23', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('290', '4', '198', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('291', '15', '315', '52', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '227', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('292', '38', '239', '40', '4', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('293', '4', '113', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('294', '15', '275', '148', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '107', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('295', '7', '48', '48', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('296', '4', '42', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('297', '1', '150', '256', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('298', '18', '86', '7', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('299', '1', '260', '359', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5')\n");

        return sql;
    }

    private String insertPageData2_5() {
        String sql = ("INSERT INTO PAGEDATA VALUES "
                + "     ('300', '1', '102', '303', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('301', '1', '82', '208', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('302', '4', '198', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('303', '1', '128', '243', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('304', '14', '66', '66', '24', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '203', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('305', '33', '108', '162', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('306', '7', '291', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('307', '1', '134', '87', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('308', '26', '187', '54', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '160', 'luckgain', 'gold', 'provisions', '354', 'dest5'),\n"
                + "     ('309', '10', 'dest1', '124', '6', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('310', '4', '211', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('311', '15', '305', '178', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '108', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('312', '4', '308', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('313', '37', 'dest1', 'dest2', '6', 'stamgainplus', 'skillgainplus', 'stamgain', '1', 'dest3', '1', '8', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('314', '1', '223', '300', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('315', '1', '306', '291', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('316', '18', '151', '218', '8', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('317', '37', '303', 'dest2', '7', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('318', '1', '85', '228', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('319', '1', '221', '81', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('320', '4', '363', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('321', '4', '169', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('323', '1', '8', '255', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('324', '4', '358', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('325', '37', '87', 'dest2', '8', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('326', '1', '35', '229', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('327', '37', '380', 'dest2', '17', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '3', '30', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('328', '37', '73', '125', '11', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('329', '26', '157', '392', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '299', 'luckgain', 'gold', 'provisions', '238', 'dest5'),\n"
                + "     ('330', '4', '81', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', '6', 'skillgain', 'dest3', '1', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('331', '5', '287', '287', '25', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('332', '1', '329', '4', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('333', '16', '224', '380', '39', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('334', '39', '292', 'dest2', '21', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('335', '4', '182', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('336', '15', '66', '172', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '249', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('337', '4', '267', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('338', '5', '93', '75', '26', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('339', '18', '201', 'dest2', '9', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('340', '13', '388', '31', '41', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '241', 'luckgain', 'gold', 'provisions', '45', 'dest5'),\n"
                + "     ('341', '26', '46', '392', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '220', 'luckgain', 'gold', 'provisions', '191', 'dest5'),\n"
                + "     ('342', '4', '7', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '2', '2', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('343', '4', '92', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', '-1', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('344', '37', '56', '153', '9', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('345', '1', '381', '311', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('346', '13', '131', 'dest2', '43', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('347', '4', '182', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('348', '38', '51', '331', '5', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('349', '1', '267', '30', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('350', '13', '7', '7', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('351', '4', '76', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('352', '1', '74', '279', 'encounternum', 'stamgainplus', 'skillgainplus', '-1', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('353', '4', '314', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('354', '26', '308', '52', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '14', 'luckgain', 'gold', 'provisions', '234', 'dest5'),\n"
                + "     ('355', '1', '181', '265', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', '-1', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('356', '4', '358', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('357', '1', '269', '57', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('358', '15', '142', '105', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '389', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('359', '26', '190', '94', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '121', 'luckgain', 'gold', 'provisions', '385', 'dest5'),\n"
                + "     ('360', '4', '89', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('361', '13', '136', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('362', '4', '177', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('363', '1', '370', '42', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('364', '1', '256', '373', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('365', '10', '237', '183', '7', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('366', '1', '89', '62', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('367', '1', '235', '323', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('368', '1', '142', '105', 'encounternum', 'stamgainplus', 'skillgainplus', '-3', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('369', '4', '109', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('370', '1', '116', '42', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '3', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('371', '29', '274', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('372', '10', 'dest1', '21', '8', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('373', '4', '85', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('374', '29', '207', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '2', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('375', '4', '5', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('376', '29', '291', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '3', '4', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('377', '5', 'dest1', '196', '27', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('378', '1', '296', '42', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('379', '34', '139', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('380', '4', '37', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('381', '1', '84', '280', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('382', '4', '396', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('383', '15', '80', '264', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '129', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('384', '1', '262', '307', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('385', '15', '114', '297', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '398', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('386', '1', '55', '166', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('387', '22', 'dest1', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('388', '4', '90', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', '-1', '-1', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('389', '2', '112', '289', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('390', '25', '120', '393', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', '2', '6', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('391', '15', '52', '362', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', '48', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('392', '1', '206', '329', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('393', '1', '212', '369', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', '8', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('394', '16', 'dest1', '232', '47', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('395', '1', '322', '34', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('396', '9', '242', '242', '8', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('397', '1', '240', '363', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('398', '1', '364', '12', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('399', '4', '218', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('400', '4', '401', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('401', '35', 'dest1', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5'),\n"
                + "     ('pgnum', 'typeofinput', 'dest1', 'dest2', 'encounternum', 'stamgainplus', 'skillgainplus', 'stamgain', 'skillgain', 'dest3', 'luckgain', 'gold', 'provisions', 'dest4', 'dest5')\n");

        return sql;
    }
}
