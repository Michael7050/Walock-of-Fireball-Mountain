/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package warlock;

import java.util.ArrayList;

/**
 *
 * @author Michael Jones
 */
public class CharacterSheet extends CombatSheet {

    private int luck;
    private int initialSkill;
    private int initialLuck;
    private int initialStamina;
    private int gold;
    private int provisions;
    private int pagenum = 1;
    private int luckPot = 0;
    private int staminaPot = 0;
    private int skillPot = 0;
    public ArrayList<String> inventory;
    private String inventoryFile;
    private boolean shield = false;
    private boolean helmet = false;
    private int potion = 0; //1 for luck pot, 2 for stampot, 3 for skill pot

    public CharacterSheet(String name, int stamina, int skill, int escape, int provisions, int luck, int initialLuck, int initialSkill, int initialStamina, int gold, int pageNum, String inventoryFile)
    {
        super(name, stamina, skill, escape);
        this.luck = luck;
        this.initialLuck = initialLuck;
        this.initialSkill = initialSkill;
        this.initialStamina = initialStamina;
        this.gold = gold;
        this.pagenum = pageNum;
        this.inventoryFile = inventoryFile;
        this.provisions = provisions;

    }

    public CharacterSheet(String name, int stamina, int skill, int escape, int provisions, int luck, int initialLuck, int initialSkill, int initialStamina, int gold, int pageNum, int luckPot, int staminaPot, int skillPot, String inventoryFile)
    {
        super(name, stamina, skill, escape);
        this.luck = luck;
        this.initialLuck = initialLuck;
        this.initialSkill = initialSkill;
        this.initialStamina = initialStamina;
        this.gold = gold;
        this.pagenum = pageNum;
        this.inventoryFile = inventoryFile;
        this.provisions = provisions;
        this.luckPot = luckPot;
        this.staminaPot = staminaPot;
        this.skillPot = skillPot;

    }

    public void printCharSheet()
    {
        //System.out.println("Current Character Sheet:");
        GameFrame.writeToScreen("Current Character Sheet:");
        //System.out.println("Name: " + this.getName());
        GameFrame.writeToScreen("Name: " + this.getName());
        //System.out.println("Current Stamina: " + this.getStamina());
        GameFrame.writeToScreen("Current Stamina: " + this.getStamina());
        //System.out.println("Current Skill " + this.getSkill());
        GameFrame.writeToScreen("Current Skill " + this.getSkill());
        //System.out.println("Current Luck " + this.getLuck());
        GameFrame.writeToScreen("Current Luck " + this.getLuck());
        //System.out.println("Gold: " + this.getGold());
        GameFrame.writeToScreen("Gold: " + this.getGold());
        //System.out.println("Provisions: " + this.getProvisions());
        GameFrame.writeToScreen("Provisions: " + this.getProvisions());
        //System.out.println("Current Page: " + this.getPagenum());
        GameFrame.writeToScreen("Current Page: " + this.getPagenum());
        return;
    }


    //public int jewels;
    //public Hashmap potions;


    //public  items;

    //use multiple items to make this char sheet
    //extends combatsheet
    //adds inventory object
    //inventory object contains gold, etc
    //adds playerdata object

    //or start initialise as 0
    public String getInventoryFile()
    {
    return inventoryFile;
    }
    public void setLuck(int luck)
    {
        if (luck <= 0)
        {
            this.luck = 0;
        }
        else
        {
            this.luck = luck;
        }
    }

    public int getLuck()
    {
        return luck;
    }

    public int getInitialSkill()
    {
        return initialSkill;
    }

    public void setInitialSkill(int initialSkill)
    {
        this.initialSkill = initialSkill;
    }

    public int getInitialLuck()
    {
        return initialLuck;
    }

    public void setInitialLuck(int initialLuck)
    {
        this.initialLuck = initialLuck;
    }

    public int getInitialStamina()
    {
        return initialStamina;
    }

    public void setInitialStamina(int initialStamina)
    {
        this.initialStamina = initialStamina;
    }

    public int getGold()
    {
        return gold;
    }

    public void setGold(int gold)
    {
            this.gold = gold;
    }

    public int getProvisions()
    {
        return provisions;
    }

    public void setProvisions(int provisions)
    {
            this.provisions = provisions;
    }

    public int getSkillPot()
    {
        return skillPot;
    }

    public void setSkillPot(int skillPot)
    {
        this.skillPot = skillPot;
    }

    public int getPagenum()
    {
        return pagenum;
    }

    public void setPagenum(int pagenum)
    {
        this.pagenum = pagenum;
    }

    public int getLuckPot()
    {
        return luckPot;
    }

    public void setLuckPot(int luckPot)
    {
        this.luckPot = luckPot;
    }

    public int getStaminaPot()
    {
        return staminaPot;
    }

    public void setStaminaPot(int staminaPot)
    {
        this.staminaPot = staminaPot;
    }

    public boolean isShield()
    {
        return shield;
    }

    public void setShield(boolean shield)
    {
        this.shield = shield;
    }

    public boolean isHelmet()
    {
        return helmet;
    }
    public void setHelmet(boolean helmet)
    {
        this.helmet = helmet;
    }

    public int getPotion()
    {
        return potion;
    }

    public void setPotion(int potion)
    {
        this.potion = potion;
    }
}
