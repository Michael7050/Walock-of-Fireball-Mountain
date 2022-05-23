package warlock;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class RunPage {

    public static int runPage(int startPage, CharacterSheet player) throws InterruptedException, IOException
    {
        int destination = 0; //declare destination with error value
        int response;
        boolean luck;
        boolean combat;
        boolean result = false;
        int roll;

        //page start:
        System.out.println("Page:" + startPage);

        //read gamedata from file and create array of data
        Page page = generatePage(startPage);
        endOfPage(player, destination);//run end of page which should now be called start of page.
        //print pagetext
        System.out.println(FileMethods.readText(FileMethods.getPagePath(page.pgnum)));

        destination = page.dest1; //sets a default value if input method fails

        //use inputmethod to set destination page.
        switch (page.typeofinput) {
            case 1: //choice between two options
                destination = setDestFromInput(GameMethods.takeInput(2), page);
                break;

            case 2: //test luck
                GameMethods.enterToContinue();
                luck = Encounter.testLuck(player);
                if (luck) {
                    destination = page.dest2;
                } else {
                    destination = page.dest1;
                }
                break;

            case 3:
                response = GameMethods.takeInput(2);
                if (response == 1)
                {
                    int gold = player.getGold();
                    if (gold < 3)
                    {
                        System.out.println("You don't have enough gold! You threaten him instead.");
                        destination = page.dest2;
                    }
                    else
                    {
                        GameMethods.changeGold(player, -3);
                        destination = page.dest1;
                    }
                }
                else
                {
                    destination = page.dest2;
                }
                break;

            case 4: // move straight on
                destination = page.dest1;
                break;

            case 5: //run a combat encounter
                combat = Encounter.singleMonsterEncounter(page.encounternum, player);
                if (combat) {
                    destination = page.dest2;
                } else {
                    destination = page.dest1;
                }
                break;

            case 6: //run 161 then input2
                combat = Encounter.run161(player);
                destination = setDestFromInput(GameMethods.takeInput(2), page);
                break;

            case 7: //run 161 then move on
                boolean input7 = Encounter.run161(player);
                destination = page.dest1;
                break;

            case 8:
                luck = Encounter.testLuck(player);
                if (luck) {
                    destination = page.dest3;
                } else {
                    result = Encounter.escapeMethod(player);
                    if (result)
                    {
                        destination = page.dest2;
                    }
                    else
                    {
                        destination = page.dest1;
                    }
                }
                break;

            case 9:
                //change to stat encounter - lot of stuff that only gets run once here.
                switch (page.encounternum)
                {
                    case 1: //page 109
                        if (player.getStamina() < (player.getInitialStamina() - 2))
                        {
                            player.setStamina((player.getInitialStamina()-2));
                        }
                        if (player.getSkill() < (player.getInitialSkill()-1))
                        {
                            player.setSkill(player.getInitialSkill()-1);
                        }
                        destination = setDestFromInput(GameMethods.takeInput(2), page);
                        break;

                    case 2: //page 125
                        boolean rope = Encounter.testLuck(player);
                        while (rope)
                        {
                            GameMethods.changeStamina(player, -1);
                            rope = Encounter.testLuck(player);
                        }
                        destination = page.dest2;
                        break;

                    case 3: //page 151
                        response = GameMethods.takeInput(3);
                        if (response == 1)
                        {
                            GameMethods.changeStamina(player,-1);
                        }
                        destination = setDestFromInput(response, page);
                        break;

                    case 4: //pg 163
                        result = Encounter.testLuck(player);
                        if (!result)
                        {
                            GameMethods.changeStamina(player, -1);
                        }
                        combat = Encounter.singleMonsterEncounter(29, player);
                        if (combat)
                        {
                            destination = page.dest2;
                        }
                        else
                        {
                            destination = page.dest1;
                        }
                        break;

                    case 5: //pg 213
                        roll = GameMethods.rollD6() + GameMethods.rollD6();
                        System.out.println("You rolled: " + roll + " and your skill is " + player.getSkill() +".");
                        if (roll <= player.getSkill())
                        {
                            destination = page.dest1;
                        }
                        else
                        {
                            GameMethods.changeStamina(player,-1);
                            destination = page.dest2;
                        }
                        break;
                    case 6: //pg243
                        roll = GameMethods.printRoll();
                        if (roll == 1 || roll == 3 || roll == 5)
                        {
                            GameMethods.changeStamina(player,-1);
                            GameMethods.changeSkill(player, -3);
                        }
                        else
                        {
                            GameMethods.changeStamina(player,-2);
                            GameMethods.changeSkill(player, -1);
                        }
                        destination = page.dest1;
                        break;
                    case 7: //pg275
                        luck = Encounter.testLuck(player);
                        if (!luck)
                        {
                            GameMethods.changeStamina(player, -1);
                        }
                        destination = page.dest1;
                        break;
                    case 8: //pg 396
                        response = GameMethods.takeInput(2);
                        if (response == 1)
                        {
                            destination = page.dest1;
                        }
                        else
                        {
                            GameMethods.changeStamina(player,-5);
                            destination = page.dest1;
                        }
                        break;
                }
                break;

            case 10: //multiple monster encounter.
                switch(page.encounternum)
                {
                    case 1:
                        result = Encounter.multiMonsterEncounter(30, 31, player);
                        break;
                    case 2:
                        result = Encounter.multiMonsterEncounter(32, 35, player);
                        break;
                    case 3:
                        result = Encounter.multiMonsterEncounter(36, 37, player);
                        break;
                    case 4:
                        result = Encounter.multiMonsterEncounter(38, 39, player);
                        break;
                    case 5:
                        result = Encounter.multiMonsterEncounter(40, 42, player);
                        break;
                    case 6:
                        result = Encounter.multiMonsterEncounter(43, 45, player);
                        break;
                    case 7:
                        result = Encounter.multiMonsterEncounter(46, 50, player);
                        break;
                    case 8:
                        result = Encounter.multiMonsterEncounter(51, 52, player);
                        break;
                }
                if (result)
                {
                   destination = page.dest2;
                }
                else
                {
                    destination = page.dest1;
                }
                break;

            case 11: //change combat (empty for now)
                break;

            case 12: //choose escape
                    result = Encounter.escapeMethod(player);
                    if (result)
                        destination = page.dest2;
                    else
                        destination = page.dest1;

                break;

            case 13: //equipment encounter //add item then dice encounter //pg 361
                Encounter.giveItem(player, 18);
                roll = GameMethods.printRoll() + GameMethods.printRoll();
                if (roll > player.getSkill())
                {
                    GameMethods.changeSkill(player, -2);
                    GameMethods.changeStamina(player, -3);
                }
                destination = page.dest1;
                break;

            case 14: //escape or combat?
                result = Encounter.escapeMethod(player);
                if (!result)
                {
                    destination = page.dest1;
                    break;
                }
                else
                {
                    combat = Encounter.singleMonsterEncounter(page.encounternum, player);
                    if (combat)
                    {
                        destination = page.dest3;
                    }
                    else
                    {
                        destination = page.dest1;
                    }
                    break;
                }


            case 15: //input 3
                destination = setDestFromInput(GameMethods.takeInput(3), page);
                break;

            case 16: //enhanced combat (currently empty)
                break;

            case 17: //interrupted combat ( for pg 41 - fix this later, but for now just run normal)
                break;

            case 18: //diceroll encounters //pg 47
                destination = page.dest2;
                switch(page.encounternum)
                {
                    case 1: // pg47
                    case 6: //pg209
                    case 7: //pg298
                        if (GameMethods.printRoll() == 6)
                    {
                        destination = page.dest1;
                    }
                    break;
                    case 2: //pg 123
                        roll = GameMethods.printRoll();
                        if(roll <= 3)
                        {
                            destination = page.dest1;
                            GameMethods.changeLuck(player,2);
                        }
                        else if (roll == 6)
                        {
                            destination = page.dest3;
                        }
                        break;
                    case 3: //pg 156
                        roll = GameMethods.printRoll()+GameMethods.printRoll();
                        if (roll <= player.getSkill())
                        {
                            destination = page.dest1;
                        }
                        break;
                    case 4: //page 166
                        if(GameMethods.printRoll() < 5)
                        {
                            destination = page.dest1;
                        }
                        break;
                    case 5: //pg 195
                        roll = GameMethods.printRoll();
                        if (roll < 3)
                        {
                            destination = page.dest1;
                        }
                        else if (roll > 4)
                        {
                            destination = page.dest3;
                            GameMethods.changeLuck(player,2);
                        }
                        break;
                    case 8://pg 316
                        roll = GameMethods.printRoll() + GameMethods.printRoll();
                        if (roll <= player.getStamina())
                        {
                            destination = page.dest1;
                        }
                        else
                        {
                            eatProvisions(player);
                        }
                        break;
                    case 9: //pg 339
                        roll = GameMethods.printRoll();
                        GameMethods.changeStamina(player, -roll);
                        destination = page.dest1;
                }
                break;
            case 19: //input5
                destination = setDestFromInput(GameMethods.takeInput(5), page);
                break;

            case 20: //skill encounter (accidental duplicate)
                break;

            case 21: //luckandskill page 55
                int dice = (GameMethods.rollD6() + GameMethods.rollD6());
                System.out.println("You rolled a total of: " + dice);
                if ((dice <= player.getLuck()) && (dice <= player.getStamina()))
                {
                    destination = page.dest1;
                }
                else
                {
                    destination = page.dest2;
                }
                break;

            case 22: //run death
                String death = "x"; //put method to call death method from encounter list.
                Encounter.runDeath(player, death);
                break;

            case 23: //luck encounter
                luck = Encounter.testLuck(player);
                if(luck)
                {
                    destination = page.dest2;
                }
                else
                {
                    GameMethods.changeStamina(player, -1);
                    destination = page.dest1;
                }
                break;

            case 24: // placeholder due to reshuffling of code
                break;

            case 25: //choose to eat provisions then input2
                eatProvisions(player);
                destination = setDestFromInput(GameMethods.takeInput(2), page);
                break;

            case 26: //input4
                destination = setDestFromInput(GameMethods.takeInput(4), page);
                break;

            case 27: //pg82 choose to test luck or not
                response = GameMethods.takeInput(2);
                if (response == 2)
                {
                    luck = Encounter.testLuck(player);
                    if (luck)
                    {
                        destination = page.dest3;
                    }
                    else
                    {
                        destination = page.dest2;
                    }
                }
                else
                {
                    destination = page.dest1;
                }
                break;

            case 28: //enhanced combat? currently empty
                break;

            case 29:
                eatProvisions(player);
                destination = page.dest1;
                break;

            case 30: //enhanced combat
                break;

            case 31: //combat then provisions then move on
                combat = Encounter.singleMonsterEncounter(page.encounternum, player);
                eatProvisions(player);
                destination = page.dest1;
                break;

            case 32: //choose to test luck or not
                response = GameMethods.takeInput(2);
                if (response == 1)
                {
                    destination = page.dest1;
                }
                else
                {
                    result = Encounter.testLuck(player);

                    if (result)
                    {
                        destination = page.dest3;
                    }
                    else
                    {
                        destination = page.dest2;
                    }
                }
                break;

            case 33: //test luck 3 times page 305
                for (int x = 1; x < 3; x++)
                {
                    result = Encounter.testLuck(player);
                    if (!result)
                    {
                        destination = page.dest1;
                        break;
                    }
                }
                destination = page.dest2;
                break;

            case 34: //test luck if not, die.
                luck = Encounter.testLuck(player);
                if (luck) {
                    destination = page.dest1;
                } else {
                    Encounter.runDeath(player, "Lightning Bolt");
                }
                break;
            case 35: //win
                GameMethods.enterToContinue();
                System.out.println("Congratulations! You WIN!");
                System.out.println("Game shutting down now.");
                System.exit(0);
                break;

            case 36: //input2 then combat (choose if monster is dead or not)
                response = GameMethods.takeInput(2);
                if (response == 2)
                {
                    destination = page.dest2;
                }
                else
                {
                    combat = Encounter.singleMonsterEncounter(page.encounternum, player);
                    if (combat)
                    {
                        destination = page.dest2;
                    }
                    else
                    {
                        destination = page.dest1;
                    }
                    break;
                }
                break;
            case 37: // adding item to char inventory
                Encounter.giveItem(player, page.encounternum);
                destination = page.dest1;
                break;

            case 38: //checking items and moving on based on that
                destination = Encounter.inventoryCheck(player, page, page.encounternum);
                break;

            case 39: //buy item //this can be added to if ever needed for other items
                Encounter.buyItem(player, page.encounternum, 20);
                destination = page.dest1;
                break;

            case 40: //exchange item for another item
                Encounter.exchangeItem(player, page.encounternum);
                destination = page.dest1;
                break;

            case 41:
                Encounter.takeItem(player, page.encounternum);
                destination = page.dest1;
                break;

            case 42: //sacrifice an item (or a gold)
                Encounter.sacrificeItem(player);
                destination = page.dest1;
                break;
        }

        //now that destination has been set, we update character values:
        int temp = 0;
        //gold

        if (page.gold != 0)
        {
            GameMethods.changeGold(player, page.gold);
        }

        if (page.provisions != 0)
        {
            GameMethods.changeProvisions(player,page.provisions);
        }

        if (page.skillgain != 0)
        {
            GameMethods.changeSkill(player, page.skillgain);
        }

        if (page.luckgain != 0)
        {
            GameMethods.changeLuck(player, page.luckgain);
        }

        if (page.stamgain != 0)
        {
            GameMethods.changeStamina(player, page.stamgain);
        }

        //now that values have been updated we do end of page input
        //and finally, we return destination.
        return destination;
    }

    public static int setDestFromInput (int input, Page page)
    {
        int dest = 999; //initialise as debug result
        switch (input)
        {
            case 1:
                dest = page.dest1;
                break;
            case 2:
                dest = page.dest2;
                break;
            case 3:
                dest = page.dest3;
                break;
            case 4:
                dest = page.dest4;
                break;
            case 5:
                dest = page.dest5;
                break;
        }
        return dest;
    }

    public static void eatProvisions (CharacterSheet player) {
        int playerProvisions = player.getProvisions();
        int playerHP = player.getStamina();
        if (playerProvisions > 0) //checks to see if player has provisions
        {
            System.out.println("You can eat some provisions here. Will you? Y/N"); //check for input
            boolean answer = GameMethods.yn();
            if (answer)
            {
                playerProvisions--;
                player.setProvisions(playerProvisions);
                playerHP += 4;
                player.setStamina(playerHP);
                System.out.println("You eat some hearty provisions and regain 4 STAMINA.");
                System.out.println("Current Stamina: " + playerHP);
                return;
            }
            else
            {
                System.out.println("You decide not to eat your provisions right now.");
                return;
            }
        }
        else
        {
            System.out.println("You don't have any provisions to eat!");
            return;

        }
    }

    public static void endOfPage(CharacterSheet player, int page) throws IOException
    {
        boolean drink = false;
        //saving progress
        player.setPagenum(page);
        //CharacterSheet.saveGame;
        Scanner scanner = new Scanner(System.in);
        Boolean x = true;
        while (x) {
            System.out.println("Press C to view current character sheet,");
            System.out.println("I to view Inventory and drink potions,");
            System.out.println("H for help");
            System.out.println("X to save and exit");
            System.out.println("or anything else to continue.");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("c")) {
                player.printCharSheet();
            } else if (answer.equalsIgnoreCase("i")) {
                //print out inventory and drink potion screen
                Encounter.listCurrentInventory(player);
                drinkPotion(player, player.getPotion());

            }
            else if (answer.equalsIgnoreCase("h"))
            {
                System.out.print(FileMethods.readText("resources/PageText/help.txt")); //put help page.
            }
            else if (answer.equalsIgnoreCase("x"))
            {
                System.out.println("Saving current progress, and closing game.");
                System.exit(0);
            }
            else {
                x = false;
            }
        }
        return;
    }

    public static void drinkPotion(CharacterSheet player, int x)
    {
        boolean drink = false;
        switch (x)
        {
            case 1:
                if (player.getLuckPot() > 0)
                {
                    System.out.println("You currently have " + player.getLuckPot() + " sips of luck potion left.");
                    System.out.println("Do you take a sip now? (Y/N)");
                    drink = GameMethods.yn();
                    if (drink)
                    {
                        player.setInitialLuck(player.getInitialLuck() + 1);
                        player.setLuck(player.getInitialLuck());
                        System.out.println("You feel lucky! Current luck: " + player.getLuck());
                        player.setLuckPot((player.getLuckPot()-1));
                        return;
                    }
                    else
                    {
                        return;
                    }
                }
                else
                {
                    System.out.println("You have no potions left.");
                    return;
                }

            case 2:
                if (player.getStaminaPot() > 0)
                {
                    System.out.println("You currently have " + player.getStaminaPot() + " sips of stamina potion left.");
                    System.out.println("Do you take a sip now? (Y/N)");
                    drink = GameMethods.yn();
                    if (drink)
                    {
                        player.setStamina(player.getInitialStamina());
                        System.out.println("You feel Healthy! Current Stamina: " + player.getStamina());
                        player.setStaminaPot((player.getStaminaPot()-1));
                        return;
                    }
                    else
                    {
                        return;
                    }
                }
                else
                {
                    System.out.println("You have no potions left.");
                    return;
                }
            case 3:
                if (player.getSkillPot() > 0)
                {
                    System.out.println("You currently have " + player.getSkillPot() + " sips of skill potion left.");
                    System.out.println("Do you take a sip now? (Y/N)");
                    drink = GameMethods.yn();
                    if (drink)
                    {
                        player.setSkill(player.getInitialSkill());
                        System.out.println("You feel Skillful! Current Skill: " + player.getSkill());
                        player.setSkillPot((player.getSkillPot()-1));
                        return;
                    }
                    else
                    {
                        return;
                    }
                }
                else
                {
                    System.out.println("You have no potions left.");
                    return;
                }
        }
    }

    public static Page generatePage(int pageNumber) //method to generate a page
    {
        int[] gameData = FileMethods.getPageData(pageNumber);
        //generate page
        Page page = new Page(gameData[0], gameData[1], gameData[2], gameData[3], gameData[4], gameData[5], gameData[6], gameData[7], gameData[8], gameData[9], gameData[10], gameData[11], gameData[12], gameData[13], gameData[14]);
        return page;
    }
}
