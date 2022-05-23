/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package warlock;

/**
 *
 * @author Michael Jones
 */
public class CombatSheet{
    private String name;
    private int stamina;
    private int skill;
    private int escape;

    public  CombatSheet (String name, int stamina, int skill, int escape)
    {
        this.name = name;
        this.stamina = stamina;
        this.skill = skill;
        this.escape = escape;
    }


    public void setStamina(int stamina)
    {
        if (stamina <= 0) //stamina can't be a negative.
        {
            this.stamina = 0;
        }
        else
        {
            this.stamina = stamina;
        }
    }

    public void setSkill(int skill)
    {
        if (skill <= 0)
        {
            this.skill = 0;
        }
        else
        {
            this.skill = skill;
        }
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getSkill()
    {
        return this.skill;
    }

    public int getStamina()
    {
        return this.stamina;
    }

    public String getName()
    {
        return this.name;
    }

    public int getEscape()
    {
        return this.escape;
    }

    public void setEscape(int escape)
    {
        this.escape=escape;
    }

}
