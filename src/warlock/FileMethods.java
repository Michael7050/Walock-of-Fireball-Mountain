package warlock;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

public class FileMethods
{
    //Method that takes in a linenumber and a filename and returns that string
    public static String readLineData(int line, String filename) {
        //open file reader
        String lineIWant = "start";
        try {
            FileInputStream fs = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            for (int i = 1; i < line; ++i)
                br.readLine();
            lineIWant = br.readLine();
            br.close();
        }
        catch (
                FileNotFoundException e)
        {
            System.out.println("File not found.");
        } catch (
                IOException e)
        {
            System.out.println("Error reading from file: " + filename);
        }
       return lineIWant;
    }

    //Method to read in page text:
    public static String readText(String fileName)
    {
        String everything = null;
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null)
            {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();
            br.close();
        } catch (
                FileNotFoundException e)
        {
            System.out.println("File not found.");
        } catch (
                IOException e)
        {
            System.out.println("Error reading from file" + fileName);
        }

        return everything;
    }
    public static int[] getPageData(int pgnum)
    {
        String rawData = FileMethods.readLineData(pgnum, "resources/pageData.txt");
        String[] parts = rawData.split(" ");
        int[] data = pageStringToPageData(parts);
        return data;
    }

    public static int[] pageStringToPageData(String[] x) {
        int[] data = new int[15];
        for (int i = 0; i < x.length; i++) {
            try {
                data[i] = Integer.parseInt(x[i]);
            } catch (NumberFormatException e) {
                //replaces empty variables with error value
                if (x[i].equals("dest1") || x[i].equals("dest2") || x[i].equals("encounternum") || x[i].equals("dest3") || x[i].equals("dest4") || x[i].equals("dest5") || x[i].equals("pgnum")) {
                    data[i] = 1;
                }
                //replaces empty variables with empty value
                else if (x[i].equals("typeofinput") || x[i].equals("stamgainplus") || x[i].equals("skillgainplus") || x[i].equals("stamgain") || x[i].equals("skillgain") || x[i].equals("luckgain") || x[i].equals("gold") || x[i].equals("provisions")) {
                    data[i] = 0;
                } else {
                    data[i] = 0;
                }
            }
        }
        return data;
    }

    public static String getPagePath(int pgnum)
    {
        String filePath = "resources/PageText/pg" + pgnum + ".txt";
        return filePath;
    }

    public static String characterToOutput(CharacterSheet player)
    {
        String output = "";

        //string to save in file stores following values in order
        //name stamina skill escape luck intskill intluck intstam gold provisions pagenum luckpot stampot skillpot

        output += player.getName() + " ";
        output += player.getStamina() + " ";
        output += player.getSkill() + " ";
        output += player.getEscape() + " ";
        output += player.getLuck() + " ";
        output += player.getInitialSkill() + " ";
        output += player.getInitialLuck() + " ";
        output += player.getInitialStamina() + " ";
        output += player.getGold() + " ";
        output += player.getProvisions() + " ";
        output += player.getPagenum() + " ";
        output += player.getLuckPot() + " ";
        output += player.getStaminaPot() + " ";
        output += player.getSkillPot() + " ";
        output += player.getInventoryFile() + " ";

        return output;
    }

    public static String getInventoryItem(int line)
    {
        String rawData = FileMethods.readLineData(line, "resources/InventoryEncounter.txt");
        String[] parts = rawData.split(" ");
        String item = parts[1];
        return item;
    }

    public static String getSecondItem(int line)
    {
        String rawData = FileMethods.readLineData(line, "resources/InventoryEncounter.txt");
        String[] parts = rawData.split(" ");
        String item = parts[2];
        return item;
    }
}