/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package warlock;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author Michael Jones
 */
public class WarlockGame
{
    //load or create char sheet
    //start run page loop
    private final HashMap<String, CharacterSheet> savedGames;
    private final String fileName = "resources/CharacterSheets/CharacterSheetSaves.txt";

    public WarlockGame() throws InterruptedException, IOException
    {
        GameFrame gameGUI = new GameFrame();
        gameGUI.setVisible(true);
        this.savedGames = new HashMap<>();
        this.getPlayers(fileName);
        //Game Startup Text
        System.out.println(FileMethods.readText("resources/PageText/gamestart.txt"));
        GameFrame.writeToScreen(FileMethods.readText("resources/PageText/gamestart.txt"));
        //load or create char sheet
        CharacterSheet player = checkPlayer(startSaves());
        //Start page loop.
        while (true) //this loop runs the whole game page by page.
        {
            int x = player.getPagenum();
            int tempPageNum = (RunPage.runPage(x, player));
            player.setPagenum(tempPageNum);
            saveGame(player); //this loop should never end, and instead end via individual runpage methods.
        }
    }

    public void getPlayers(String fn) //this populates the list of saved games from file
    {
        FileInputStream fin;
        try
        {
            fin = new FileInputStream(fn);
            Scanner fileScanner = new Scanner(fin);

            while (fileScanner.hasNextLine())
            {
                String line = fileScanner.nextLine();
                StringTokenizer st = new StringTokenizer(line);
                CharacterSheet player = new CharacterSheet(st.nextToken(), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), st.nextToken());
                this.savedGames.put(player.getName(), player);
            }
            fin.close();
        } catch (FileNotFoundException ex)
        {
            System.out.println(ex.getMessage());
        } catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public CharacterSheet checkPlayer(String name) throws InterruptedException //checks file to see if player already exists, creates new if it does not.
    {
        CharacterSheet player;

        if (this.savedGames.containsKey(name))
        {
            player = this.savedGames.get(name);
            System.out.println("Your current page: " + player.getPagenum());
        }
        else
        {
            player = LoadCharacter.createCharacter(name);
            this.savedGames.put(name, player);
        }
        return player;
    }

    public void saveGame(CharacterSheet player)
    {
        this.savedGames.put(player.getName(), player);
        try
        {
            FileOutputStream fOut = new FileOutputStream(this.fileName);
            PrintWriter pw = new PrintWriter(fOut);
            for (CharacterSheet x : this.savedGames.values())
            {
                pw.println(FileMethods.characterToOutput(x));
            }
            pw.close();
        }
        catch (FileNotFoundException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    //write a method that lists all current characters, and prompts user to enter a name
    public String startSaves()
    {
        Scanner scan = new Scanner(System.in);
        System.out.println("Current saved games:");
        System.out.println(savedGames.keySet());
        System.out.println("Enter player name to load game, or start new game:");
        String input = scan.nextLine();
        return input;
    }
}
