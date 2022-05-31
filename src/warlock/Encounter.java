/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package warlock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Thread.sleep;

/**
 * @author Michael Jones
 */
public class Encounter
{
    static ArrayList<String> characterInventory = new ArrayList<>();

    //do i store methods here? yes
    //test luck method
    //eat provisions

    //runs through multiple monsters.
    public static boolean multiMonsterEncounter(int start, int finish, CharacterSheet player) throws InterruptedException
    {
        int x = start;
        while (x <= finish)
        {
            CombatSheet monster = generateSingleMonster(x);
            boolean result = stdCombat(monster, player);
            if (result == false)
            {
                return false;
            }
            x++;
        }
        return true;
    }

    //combat encounter takes in monster and runs combat
    //running combat encounter for one monster.
    // true = win, false = escape.
    public static boolean singleMonsterEncounter(int monNum, CharacterSheet player) throws InterruptedException
    {
        CombatSheet monster = generateSingleMonster(monNum);
        boolean result = stdCombat(monster, player);
        return result;
        //run stdCombat
    }

    public static CombatSheet generateSingleMonster(int x) //generates a monster from file
    {
        String monsterData = FileMethods.readLineData(x, "resources/SingleMonsterList.txt");
        String[] data = monsterData.split(" ");
        CombatSheet monster = new CombatSheet(data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4]));
        return monster;
    }

    //method to run randomised combat (as seen in page 161)
    public static boolean run161(CharacterSheet player) throws InterruptedException
    {
        int diceRoll = GameMethods.rollD6() + 13;
        //System.out.println(FileMethods.readText(FileMethods.getPagePath(161)));
        GameFrame.writeToScreen(FileMethods.readText(FileMethods.getPagePath(161)));
        //System.out.println("You rolled a: " + diceRoll);
        GameFrame.writeToScreen("You rolled a: " + diceRoll);
        boolean result = singleMonsterEncounter(diceRoll, player);
        return result;
    }


    public static boolean stdCombat(CombatSheet monster, CharacterSheet player) throws InterruptedException
    {
        int monsterAtk;
        int playerAtk;
        int monsterHP = monster.getStamina();
        int playerHP = player.getStamina();
        int roundNum = 0;
        int escape = monster.getEscape();
        String monsterName = monster.getName();
        String playerName = player.getName();
        boolean inCombat = true;
        boolean run;

        //Start combat encounter
        //System.out.println("COMBAT ENCOUNTER!\n" + playerName + " Vs. " + monsterName + "!\n\n");
        GameFrame.writeToScreen("COMBAT ENCOUNTER!\n" + playerName + " Vs. " + monsterName + "!\n\n");

        //put loop or something here
        while (inCombat)
        {

            playerAtk = combatAtk(player.getName(), player.getSkill());
            monsterAtk = combatAtk(monster.getName(), monster.getSkill());

            if (monsterAtk < playerAtk)
            {
                //result for if player wins roll
                //System.out.println("You wounded " + monsterName + ", and dealt two damage!\n");
                GameFrame.writeToScreen("You wounded " + monsterName + ", and dealt two damage!\n");
                //choose whether or not to use luck
                boolean luck = chooseLuck(player);
                if (luck)
                {
                    //test your luck?
                    boolean luckResult = testLuck(player);
                    if (luckResult)
                    {
                        //System.out.println("You have inflicted a *severe wound!*\n Two more damage dealt to " + monsterName + ".");
                        GameFrame.writeToScreen("You have inflicted a *severe wound!*\n Two more damage dealt to " + monsterName + ".");
                        monsterHP -= 4;
                    }
                    else
                    {
                        //System.out.println("The wound was a mere graze! You only dealt one damage to " + monsterName + " after all!");
                        GameFrame.writeToScreen("The wound was a mere graze! You only dealt one damage to " + monsterName + " after all!");
                        monsterHP -= 1;
                    }
                }
                else
                    //wound for 2
                    monsterHP -= 2;
            }

            //result for losing round
            else if (playerAtk < monsterAtk)
            {
                //System.out.println(monsterName + " wounded you, dealing two damage!");
                GameFrame.writeToScreen(monsterName + " wounded you, dealing two damage!");
                //wound for two and choose whether or not to use luck.
                boolean luck = chooseLuck(player);
                if (luck)
                {
                    boolean luckResult = testLuck(player);
                    if (luckResult)
                    {
                        //System.out.println("You managed to avoid the full damage of the blow!\n You only took one damage after all.");
                        GameFrame.writeToScreen("You managed to avoid the full damage of the blow!\n You only took one damage after all.");
                        playerHP -= 1;
                    }
                    else
                    {
                        //System.out.println("You took a more serious blow than you expected!\n Three damage taken instead.");
                        GameFrame.writeToScreen("You took a more serious blow than you expected!\n Three damage taken instead.");
                        playerHP -= 3;
                    }
                }
                playerHP -= 2;
            }

            else if (playerAtk == monsterAtk)
            {
                //System.out.println("A tie! You avoid each other's blows. Next round!");
                GameFrame.writeToScreen("A tie! You avoid each other's blows. Next round!");
            }

            roundNum += 1;
            //System.out.println("End of round " + roundNum + ".");
            GameFrame.writeToScreen("End of round " + roundNum + ".");
            //System.out.println("Your Stamina: " + playerHP + ". " + monsterName + "'s Stamina: " + monsterHP);
            GameFrame.writeToScreen("Your Stamina: " + playerHP + ". " + monsterName + "'s Stamina: " + monsterHP);
            player.setStamina(playerHP);
            checkPlayerDeath(player, monsterName); //checks if player has died.

            //System.out.println("escape = " + escape + " roundnum: " + roundNum); //this is a debug line please ignore

            if (roundNum >= escape)
            //possible to escape?
            {
                run = escapeMethod(player);
                if (!run)
                    return false;
            }

            if (monsterHP <= 0)
            {
                //System.out.println(playerName + " has defeated " + monsterName + " in glorious combat!");
                GameFrame.writeToScreen(playerName + " has defeated " + monsterName + " in glorious combat!");
                //System.out.println("Your final Stamina: " + playerHP);
                GameFrame.writeToScreen("Your final Stamina: " + playerHP);
                player.setStamina(playerHP);
                return true;
            }
        }
        return true;
    }

    //roll a d6

//method for rolling 2d6, adding skill, and printing out

    public static int combatAtk(String name, int skill) throws InterruptedException
    {
        int dice1;
        int dice2;
        int totalAtk;
        dice1 = GameMethods.rollD6();
        dice2 = GameMethods.rollD6();
        totalAtk = (dice1 + dice2 + skill);
        //System.out.println(name + "'s skill is " + skill + " and " + name + " rolls " + dice1 + " plus " + dice2 + ".\nTotal Attack: " + totalAtk + "\n\n");
        GameFrame.writeToScreen(name + "'s skill is " + skill + " and " + name + " rolls " + dice1 + " plus " + dice2 + ".\nTotal Attack: " + totalAtk + "\n\n");
        sleep(1000);
        return totalAtk;
    }
//method for invoking Test your Luck

    public static boolean chooseLuck(CharacterSheet player)
    {
        int luck = player.getLuck();
        //System.out.println(luck); //more debug code
        if (luck <= 0)
        {
            //make sure that player has luck before asking.
            return false;
        }
        //System.out.println("Do you choose to press your luck? Y/N");
        GameFrame.writeToScreen("Do you choose to press your luck? Y/N");
        boolean answer = GameMethods.yn();
        return answer;
    }


    //method for Test your Luck
    public static boolean testLuck(CharacterSheet player) throws InterruptedException
    {
        String name = player.getName();
        boolean lucky = true;
        int luck = player.getLuck();
        int dice1 = GameMethods.rollD6();
        int dice2 = GameMethods.rollD6();
        int luckResult = dice1 + dice2;
        //System.out.println("*You test your luck!*");
        GameFrame.writeToScreen("*You test your luck!*");
        sleep(1000);
        //System.out.println("Rolling the dice...:");
        GameFrame.writeToScreen("Rolling the dice...:");
        sleep(1000);
        //System.out.println("Dice 1. . .");
        GameFrame.writeToScreen("Dice 1. . .");
        sleep(1000);
        //System.out.println(dice1);
        GameFrame.writeToScreen(String.valueOf(dice1));
        sleep(1000);
        //System.out.println("Dice 2. . .");
        GameFrame.writeToScreen("Dice 2. . .");
        sleep(1000);
        //System.out.println(dice2);
        GameFrame.writeToScreen(String.valueOf(dice2));
        sleep(1000);
        //System.out.println(name + "'s luck is " + luck + " and " + name + " rolled a total of " + luckResult + "\n\n");
        GameFrame.writeToScreen(name + "'s luck is " + luck + " and " + name + " rolled a total of " + luckResult + "\n\n");

        if (luckResult <= luck)
        {
            lucky = true;
            luck -= 1;
            player.setLuck(luck);
            //System.out.println("Congratulations! You feel lucky!\n One luck point spent.");
            GameFrame.writeToScreen("Congratulations! You feel lucky!\n One luck point spent.");
        }
        else
        {
            lucky = false;
            luck -= 1;
            player.setLuck(luck);
            //System.out.println("Unfortunate! You feel unlucky!\n One luck point spent.");
            GameFrame.writeToScreen("Unfortunate! You feel unlucky!\n One luck point spent.");

        }
        //System.out.println("Current luck: " + player.getLuck());
        GameFrame.writeToScreen("Current luck: " + player.getLuck());
        return lucky;
    }

    //method to call escape
    public static boolean escapeMethod(CharacterSheet player)
    {
        int playerHP = player.getStamina();
        //System.out.println("You see a chance to escape! But be warned, you will get hit if you choose to run.");
        GameFrame.writeToScreen("You see a chance to escape! But be warned, you will get hit if you choose to run.");
        //System.out.println("Do you choose to run? Y/N");
        GameFrame.writeToScreen("Do you choose to run? Y/N");
        boolean answer = GameMethods.yn();
        if (answer)
        {
            playerHP -= 2;
            //System.out.println("When danger reared it's ugly head, you bravely turned your tail and fled.");
            GameFrame.writeToScreen("When danger reared it's ugly head, you bravely turned your tail and fled.");
            //System.out.println("Current Stamina: " + playerHP);
            GameFrame.writeToScreen("Current Stamina: " + playerHP);
            player.setStamina(playerHP);
            checkPlayerDeath(player, "running away like a coward");
            return false;
        }
        else
        {
            //System.out.println("You bravely charge into the fray!");
            GameFrame.writeToScreen("You bravely charge into the fray!");
            return true;
        }
    }

    //i should put this in pageMethods
    //checks to see if player has died.
    public static void checkPlayerDeath(CharacterSheet player, String death)
    {
        if (player.getStamina() <= 0)
        {
            runDeath(player, death);
        }
        else
        {
            return;
        }
    }

    public static void runDeath(CharacterSheet player, String death) //player dies
    {
        //System.out.println("Oh dear, you are dead.");
        GameFrame.writeToScreen("Oh dear, you are dead.");
        //System.out.println("You made it to page: " + player.getPagenum() + " and died because of " + death + ".");
        GameFrame.writeToScreen("You made it to page: " + player.getPagenum() + " and died because of " + death + ".");
        //System.out.println("Better luck next time!");
        GameFrame.writeToScreen("Better luck next time!");
        System.exit(0);
    }

    //method to buy an item for gold
    public static void buyItem(CharacterSheet player, int itemNum, int cost) throws IOException
    {
        boolean result = false;
        loadInventory(player);
        //System.out.println("You can buy an item here, will you?");
        GameFrame.writeToScreen("You can buy an item here, will you?");
        boolean response = GameMethods.yn();
        if (response)
        {
            if (player.getGold() < cost)
            {
                //System.out.println("You don't have enough money!");
                GameFrame.writeToScreen("You don't have enough money!");
            }
            else
            {
                GameMethods.changeGold(player, (-cost));
                addItem(characterInventory, itemNum);
                //System.out.println("Paid " + cost + " gold for: " + FileMethods.getInventoryItem(itemNum) + "!");
                GameFrame.writeToScreen("Paid " + cost + " gold for: " + FileMethods.getInventoryItem(itemNum) + "!");
                result = true;
            }
        }
        else
        {
            //System.out.println("You decide not to buy the item.");
            GameFrame.writeToScreen("You decide not to buy the item.");
        }
        saveInventory(player);
    }

    //takes an item from player.
    public static void takeItem(CharacterSheet player, int encounterNum) throws IOException
    {
        loadInventory(player);
        if (characterInventory.contains(FileMethods.getInventoryItem(encounterNum)))
        {
            removeItem(encounterNum);
        }
        else
        {
            //System.out.println("Item not found in inventory!");
            GameFrame.writeToScreen("Item not found in inventory!");
        }
        saveInventory(player);
    }

    public static void sacrificeItem(CharacterSheet player) throws IOException
    {
        loadInventory(player);
        if (characterInventory.size() < 0)
        {
            //System.out.println("You don't have any items to sacrifice, so you throw a gold instead.");
            GameFrame.writeToScreen("You don't have any items to sacrifice, so you throw a gold instead.");
            GameMethods.changeGold(player,-1);
        }
        else
        {
            listInventory();
            int response = chooseItem();
            characterInventory.remove(response);
        }
        saveInventory(player);
    }

    //method to remove item
    public static void removeItem(int encounterNum)
    {
        characterInventory.remove((encounterNum - 1));
    }


    // checks inventory for item - encounter. return page dest
    public static int inventoryCheck(CharacterSheet player, Page runningPage, int encounterNum) throws IOException
    {
        loadInventory(player);
        boolean hasItem = false;
        int dest = 0;
        boolean loop = true;
        switch (encounterNum)
        {
            case 1: //check for boathouse key
                dest = moveBasedOnItem(runningPage, 12);
                break;

            case 2: //vampire attack
                while (loop)
                {
                    int response = GameMethods.takeInput(3);
                    switch (response)
                    {
                        case 1:
                            hasItem = (checkItem(6) || checkItem(10));
                            if (hasItem)
                            {
                                dest = runningPage.dest1;
                                loop = false;
                                break;
                            }
                            else
                            {
                                //System.out.println("You do not have a crucifix! Try a different option!");
                                GameFrame.writeToScreen("You do not have a crucifix! Try a different option!");
                                break;
                            }
                        case 2:
                        {
                            hasItem = (checkItem(5));
                            if (hasItem)
                            {
                                dest = runningPage.dest2;
                                loop = false;
                                break;
                            }
                            else
                            {
                                //System.out.println("You do not have any stakes! Try a different option!");
                                GameFrame.writeToScreen("You do not have any stakes! Try a different option!");
                                break;
                            }
                        }
                        case 3:
                            dest = runningPage.dest3;
                            loop = false;
                            break;
                    }
                }
                break;

            case 3: // check for CHEESE
                dest = moveBasedOnItem(runningPage, 7);
                break;

            case 4: //check for blue candle
                dest = moveBasedOnItem(runningPage, 21);
                break;

            case 5: //check for invis pot
                dest = moveBasedOnItem(runningPage, 23);
                break;

            case 6: //page 105 options
                loop = true;
                while (loop)
                {
                    int response = GameMethods.takeInput(5);
                    switch (response)
                    {
                        case 1: //invis pot
                            if (checkItem(23))
                            {
                                dest = runningPage.dest1;
                                loop = false;
                                break;
                            }
                            else
                            {
                                //System.out.println("You don't have an Invisibility Potion! Try a different option.");
                                GameFrame.writeToScreen("You don't have an Invisibility Potion! Try a different option.");
                                break;
                            }
                        case 2: //check for Eye of cyclops
                        {
                            if (checkItem(22))
                            {
                                dest = runningPage.dest2;
                                loop = false;
                                break;
                            }
                            else
                            {
                                //System.out.println("You don't have the Jewel! Try a different option.");
                                GameFrame.writeToScreen("You don't have the Jewel! Try a different option.");
                                break;
                            }
                        }
                        case 3: //check for cheese
                        {
                            if (checkItem(7))
                            {
                                dest = runningPage.dest3;
                                loop = false;
                                break;
                            }
                            else
                            {
                                //System.out.println("You don't have any Cheese! Try a different option.");
                                GameFrame.writeToScreen("You don't have any Cheese! Try a different option.");
                                break;
                            }
                        }
                        case 4: //check for Bow
                        {
                            if (checkItem(16))
                            {
                                dest = runningPage.dest4;
                                loop = false;
                                break;
                            }
                            else
                            {
                                //System.out.println("You don't have the bow! Try a different option.");
                                GameFrame.writeToScreen("You don't have the bow! Try a different option.");
                                break;
                            }
                        }
                        case 5: //check for stick
                        {
                            if (checkItem(11))
                            {
                                dest = runningPage.dest5;
                                loop = false;
                                break;
                            }
                            else
                            {
                                //System.out.println("You don't have any sticks! Try a different option.");
                                GameFrame.writeToScreen("You don't have any sticks! Try a different option.");
                                break;
                            }
                        }
                    }
                }
                break;

            case 7: //check for silver weaponry
            {
                hasItem = (checkItem(6) || checkItem(10) || checkItem(16) || checkItem(18));
                if (hasItem)
                {
                    dest = runningPage.dest1;
                    break;
                }
                else
                {
                    GameMethods.changeStamina(player, -2);
                    dest = runningPage.dest2;
                    break;
                }
            }
        }
        saveInventory(player); //save inventory and return destination.
        return dest;
    }

    public static int moveBasedOnItem(Page page, int item)
    {
        Boolean hasItem = (checkItem(item));
        if (hasItem)
        {
            return page.dest1;
        }
        else
        {
            return page.dest2;
        }
    }

    //checks if player has item.
    public static boolean checkItem(int itemNum)
    {
        boolean hasItem = characterInventory.contains(FileMethods.getInventoryItem(itemNum));
        return hasItem;
    }

    //exchanges something for a new item
    public static void exchangeItem(CharacterSheet player, int encounterNum) throws IOException
    {
        boolean result = false;
        loadInventory(player);
        //System.out.println("Do you wish to sacrifice an item to pick up a new one?");
        GameFrame.writeToScreen("Do you wish to sacrifice an item to pick up a new one?");
        Boolean answer = GameMethods.yn();
        if (answer)
        {
            listInventory();
            if (characterInventory.size() < 0)
            {
                //System.out.println("You don't have any items to sacrifice!");
                GameFrame.writeToScreen("You don't have any items to sacrifice!");
            }
            else
            {
                int response = chooseItem();
                characterInventory.remove(response);
                addItem(characterInventory, encounterNum);
                result = true;
            }
        }
        else
        {
            //System.out.println("You decided not to give up any of your stuff.");
            GameFrame.writeToScreen("You decided not to give up any of your stuff.");
        }
        saveInventory(player);
    }

    //adds an item to characters inventory
    public static void giveItem(CharacterSheet player, int encounterNum) throws IOException
    {
        //open inventory from file
        loadInventory(player);

        if (encounterNum <= 12) //add item to inventory
        {
            addItem(characterInventory, encounterNum);
        }

        else if (encounterNum == 13) //add two items
        {
            addTwoItems(characterInventory, encounterNum);
        }

        else if (encounterNum == 14) //add two items then eat provisions
        {
            addTwoItems(characterInventory, encounterNum);
            RunPage.eatProvisions(player);
        }

        else if (encounterNum >= 15 && encounterNum <= 17) //add one item then eat provisions
        {
            addItem(characterInventory, encounterNum);
            RunPage.eatProvisions(player);
        }
        else if (encounterNum == 18)
        {
            addItem(characterInventory, 20);
        }
        //save updated inventory back into file
        saveInventory(player);
    }


    public static void addItem(ArrayList<String> inventory, int x)
    {
        inventory.add(FileMethods.getInventoryItem(x));
    }

    public static void addTwoItems(ArrayList<String> inventory, int x)
    {
        inventory.add(FileMethods.getInventoryItem(x));
        inventory.add(FileMethods.getSecondItem(x));
    }

    //load inventory into arraylist from file
    public static void loadInventory(CharacterSheet player) throws FileNotFoundException
    {
        Scanner s = new Scanner(new File(player.getInventoryFile()));
        while (s.hasNext())
        {
            characterInventory.add(s.next());
        }
        s.close();
    }

    //saves inventory from arraylist into file
    public static void saveInventory(CharacterSheet player) throws IOException
    {
        FileWriter writer = new FileWriter(player.getInventoryFile());
        for (String str : characterInventory)
        {
            writer.write(str + System.lineSeparator());
        }
        writer.close();
    }

    public static void listInventory()
    {
        //System.out.println("Current Inventory");
        GameFrame.writeToScreen("Current Inventory");
        for (int i = 0; i < characterInventory.size(); i++)
        {
            //System.out.println((i + 1) + ": " + characterInventory.get(i));
            GameFrame.writeToScreen((i + 1) + ": " + characterInventory.get(i));
        }
    }

    public static void listCurrentInventory(CharacterSheet player) throws IOException
    {
        loadInventory(player);
        //System.out.println("Current Inventory");
        GameFrame.writeToScreen("Current Inventory");
        for (int i = 0; i < characterInventory.size(); i++)
        {
            //System.out.println((i + 1) + ": " + characterInventory.get(i));
            GameFrame.writeToScreen((i + 1) + ": " + characterInventory.get(i));
        }
        saveInventory(player);
    }

    public static int chooseItem()
    {
        //System.out.println("Select item:");
        GameFrame.writeToScreen("Select item:");
        int response = GameMethods.takeInput(characterInventory.size());
        return (response - 1);
    }

}
