/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package warlock;

import java.io.*;
import java.nio.Buffer;

/**
 *
 * @author Michael Jones
 */
public class playGame
{

   public static void main(String[] args) throws InterruptedException, IOException
   {
       //grabs a singleton instance of warlock game.
    WarlockGame game = WarlockGame.getInstance();
   }
}

//following this is a bunch of text I used for debugging
//       System.out.println(FileMethods.getInventoryItem(7));


//        String page1 = FileMethods.openPage(1);
//        String page5 = FileMethods.openPage(5);
//        String page50 = FileMethods.openPage(50);
//        System.out.println(page1);
//        System.out.println(page5);
//        System.out.println(page50);
//        have page number as variable, run continuous loop using variable to run each page, have page method run through page and code

        //placeholder debugging code:

        //test a combat encounter
//        CharacterSheet Mike = new CharacterSheet("Mike", 10, 10, 0, 0, 0, 0,0,0,0);
//        Mike.setStamina(10);
//        Mike.setLuck(10);
//        Mike.setSkill(10);
//        Mike.setName("Mike");
//
//        CombatSheet Ogre = new CombatSheet("Ogre", 10, 10, 2);
//
//        Encounter test = new Encounter();
//        boolean combat = test.stdCombat(Ogre, Mike);
//        System.out.println(combat);
//
        //test take input

//        int test = GameMethods.takeInput(1);
//        System.out.println(test);


            //test create character
 //CharacterCreation.createCharacter("Michael");
         //}
        //test input
//        int test = GameMethods.takeInput(3);
//        System.out.println(test);

        //test read file
      //System.out.println(GameMethods.readText("resources/PageText/pg86.txt"));

        //debugging for reading pagedata
//      CharacterSheet Mike = new CharacterSheet("Mike", 10, 10, 0, 0, 0, 0,0,0,0, "resources/CharacterSheets/MichaelInventory");
//        int pageres = RunPage.runPage(1, Mike);
//        System.out.print(pageres);
//        System.out.println(RunPage.getPagePath(1));
//        System.out.println(RunPage.getPagePath(5));
//        System.out.println(RunPage.getPagePath(104));
//        System.out.println(RunPage.getPagePath(205));
//        int test = RunPage.runPage(49, Mike);
//        System.out.print(test);




        //Start game - print intro
        //List savefiles
        //create new or load savefile
        //create new loads up new char sheet, start on page one
        // load loads char sheet, and then loads current page

        //run page
        //input includes show char sheet, or else
        //do i put string in file and hashmap for data?

            //test gui y/n
//       GameFrame gameGUI = new GameFrame();
//       gameGUI.setVisible(true);
//       boolean answer = gameGUI.ynInput();
//       System.out.println(answer);
//       answer = gameGUI.ynInput();
//       System.out.println(answer);
//       answer = gameGUI.ynInput();
//       System.out.println(answer);
//       answer = gameGUI.ynInput();
//       System.out.println(answer);
//       answer = gameGUI.ynInput();
//       System.out.println(answer);
//       answer = gameGUI.ynInput();
//       System.out.println(answer);

        //things to do in pages:
        //text
        //input

        //remember to put potions drinkable at any time

        //use override for different luck methods?
    
