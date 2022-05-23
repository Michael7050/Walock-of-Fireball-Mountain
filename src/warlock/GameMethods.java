package warlock;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class GameMethods
{
    public static int rollD6()
    {

        Random rng = new Random();

        int rollD6 = rng.nextInt(6) + 1;

        return rollD6;
    }

    public static int printRoll()//rolls dice and prints it out.
    {
        Random rng = new Random();
        int rollD6 = rng.nextInt(6) + 1;
        System.out.println("You rolled a dice and got: " + rollD6);
        return rollD6;
    }



    //method to take in input, and ensure it is within given parameters.
    public static int takeInput(int parameter)
    {
        int input = 0;
        int validate = parameter;
        Scanner scan = new Scanner(System.in);
        boolean isNumber = false;
        while (true)
        {
            System.out.println("Enter your response as a number between 1 and " + parameter +":");
            while (true)
            {
                try
                {
                    input = scan.nextInt();
                    break;
                } catch (InputMismatchException e)
                {
                    System.out.println("This needs to be a number.");
                    scan.nextLine();
                }
            }

            if (input > 0 && input <= validate)
            {
                return input;
            }

            System.out.println("Not a valid option! Please enter a number between 0 and " + parameter + ".");
        }
    }

    public static void enterToContinue(){
        System.out.println("Press ENTER to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    public static boolean yn()
    {
        Boolean answer = null;
        while (true) //check this loop?
        {
            String response;
            Scanner scan = new Scanner(System.in);
            response = scan.nextLine().trim().toLowerCase();
            if (response.equals("y"))
            {
                answer = true;
                break;
            }
            else if (response.equals("n"))
            {
                answer = false;
                break;
            }
            else
            {
                System.out.println("Sorry, I didn't catch that. Please answer y/n");
            }
        }
        return answer;
    }

    public static void changeSkill(CharacterSheet player, int x)
    {
        int temp = (player.getSkill() + x);
        if (temp > player.getInitialSkill())
        {
            player.setSkill(player.getInitialSkill());
        }
        else
        {
            player.setSkill(temp);
        }
    }

    public static void changeStamina(CharacterSheet player, int x)
    {
        int temp = (player.getStamina() + x);
        if (temp > player.getInitialStamina())
        {
            player.setStamina(player.getStamina());
        }
        else
        {
            player.setStamina(temp);
        }
    }

    public static void changeLuck(CharacterSheet player, int x)
    {
        int temp = (player.getLuck() + x);
        if (temp > player.getInitialLuck())
        {
            player.setSkill(player.getInitialLuck());
        }
        else
        {
            player.setLuck(temp);
        }
    }
    public static void changeGold(CharacterSheet player, int x)
    {
        player.setGold((player.getGold() + x));
    }

    public static void changeProvisions(CharacterSheet player, int x)
    {
        player.setProvisions((player.getProvisions() + x));
    }

}