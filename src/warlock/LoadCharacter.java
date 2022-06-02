package warlock;

import java.io.File;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class LoadCharacter
{

    public static CharacterSheet createCharacter(String name) throws InterruptedException
    {
        //intro
        //System.out.print(FileMethods.readText("resources/PageText/intro.txt"));
        GameFrame.writeToScreen(FileMethods.readText("resources/PageText/intro.txt"));
        //roll for skill
        int dice1 = GameMethods.rollD6();
        int skill = dice1 + 6;

        //System.out.println("\nRoll for Skill:");
        GameFrame.writeToScreen("\nRoll for Skill:");
        sleep(1000);
        //System.out.println("You rolled: " + dice1);
        GameFrame.writeToScreen("You rolled: " + dice1);
        //System.out.println("Total skill = " + dice1 + " + 6.");
        GameFrame.writeToScreen("Total skill = " + dice1 + " + 6.");
        //System.out.println(name + "'s skill = " + skill + ".");
        GameFrame.writeToScreen(name + "'s skill = " + skill + ".");

        //roll for stamina
        dice1 = GameMethods.rollD6();
        int dice2 = GameMethods.rollD6();
        int stamina = dice1 + dice2 + 12;

        //System.out.println("\nRoll for Stamina:");
        GameFrame.writeToScreen("\nRoll for Stamina:");
        sleep(1000);
        //System.out.println("You rolled: " + dice1 + " and " + dice2);
        GameFrame.writeToScreen("You rolled: " + dice1 + " and " + dice2);
        //System.out.println("Total stamina = " + dice1 + " + " + dice2 + " + 12.");
        GameFrame.writeToScreen("Total stamina = " + dice1 + " + " + dice2 + " + 12.");
        //System.out.println(name + "'s stamina = " + stamina + ".");
        GameFrame.writeToScreen(name + "'s stamina = " + stamina + ".");
        //roll for luck
        dice1 = GameMethods.rollD6();
        int luck = dice1 + 6;

        //System.out.println("\nRoll for Luck:");
        GameFrame.writeToScreen("\nRoll for Luck:");
        sleep(1000);
        //System.out.println("You rolled: " + dice1 + ".");
        GameFrame.writeToScreen("You rolled: " + dice1 + ".");
        //System.out.println("Total luck = " + dice1 + " + 6.");
        GameFrame.writeToScreen("Total luck = " + dice1 + " + 6.");
        //System.out.println(name + "'s luck = " + luck + ".");
        GameFrame.writeToScreen(name + "'s luck = " + luck + ".");

        //create inventory file
        String filePath = createInventFilePath(name);
        createFile(filePath);

        //create the charsheet
        CharacterSheet newChar = new CharacterSheet(name, stamina, skill, 0, 0, luck, luck, skill, stamina, 0, 1, filePath);

        //choose equipment:
        //System.out.print(FileMethods.readText("resources/PageText/StartingEquipment.txt"));
        GameFrame.writeToScreen(FileMethods.readText("resources/PageText/StartingEquipment.txt"));

        //give choices
        //System.out.println("[1] to bring a SKILL POTION.");
        GameFrame.writeToScreen("[1] to bring a SKILL POTION.");
        //System.out.println("[2] to bring a STAMINA POTION.");
        GameFrame.writeToScreen("[2] to bring a STAMINA POTION.");
        //System.out.println("[3] to bring a LUCK POTION.\n\n");
        GameFrame.writeToScreen("[3] to bring a LUCK POTION.\n\n");
        //take answer
        int response = GameMethods.takeInput(3);

        //add potion
        switch (response)
        {
            case 1:
                newChar.setSkillPot(2);
                newChar.setPotion(3);
                break;
            case 2:
                newChar.setStaminaPot(2);
                newChar.setPotion(2);
                break;
            case 3:
                newChar.setLuckPot(2);
                newChar.setPotion(1);
                break;
        }
        //set provisions:
        newChar.setProvisions(10);
//Convert Character data to save format
        String saveChar = FileMethods.characterToOutput(newChar);
        System.out.println(saveChar);
        return newChar;
    }

    //method for creating a new file path.
    private static String createInventFilePath(String name)
    {
        String path = "resources/CharacterSheets/" + name + "Inventory.txt";
        return path;
    }

    public static void createFile(String fileName) {
            try {
                File myObj = new File(fileName);
                if (myObj.createNewFile()){
                    return;
                } else {
                    System.out.println("Error - inventory file already exists.");
                    return;
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }






//

        //save to file? Save to file.
      // CharacterSheet test = CharacterCreation.createCharacter("Michael");
        //create char sheet, initialise values, save to file?





}
