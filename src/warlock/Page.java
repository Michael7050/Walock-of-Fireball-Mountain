/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package warlock;


import java.lang.reflect.Array;

/**
 *
 * @author Michael Jones
 */
public class Page {
  public int pgnum;
  public int typeofinput;
  public int dest1;
  public int dest2;
  public int encounternum;
  public int stamgainplus;
  public int skillgainplus;
  public int stamgain;
  public int skillgain;
  public int dest3;
  public int luckgain;
  public int gold;
  public int provisions;
  public int dest4;
  public int dest5;

  public Page(int pgnum, int typeofinput, int dest1, int dest2, int encounternum, int stamgainplus, int skillgainplus, int stamgain, int skillgain, int dest3, int luckgain, int gold, int provisions, int dest4, int dest5)
  {
    this.pgnum = pgnum;
    this.typeofinput = typeofinput;
    this.dest1 = dest1;
    this.dest2 = dest2;
    this.encounternum = encounternum;
    this.stamgainplus = stamgainplus;
    this.skillgainplus = skillgainplus;
    this.stamgain = stamgain;
    this.skillgain = skillgain;
    this.dest3 = dest3;
    this.luckgain = luckgain;
    this.gold = gold;
    this.provisions = provisions;
    this.dest4 = dest4;
    this.dest5 = dest5;
  }
}