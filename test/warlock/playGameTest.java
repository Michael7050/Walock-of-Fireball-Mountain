/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package warlock;

import java.sql.Connection;
import java.sql.Statement;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Michael Jones
 */
public class playGameTest {
    
    private playGame gameTest;
    
    public playGameTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        gameTest = new playGame();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class playGame.
     */
    
    @Test
    public void testInventoryGet() throws Exception {
        System.out.println("grabbing an item from stored items");
        int inventoryItem = 7;
        String result = FileMethods.getInventoryItem(inventoryItem);
        String expectedResult = "Cheese";
        System.out.println(result);
        System.out.println(expectedResult);
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void testLoadPageText() throws Exception {
        System.out.println("printing out a page of text");
        int pgnum = 86;
        String result = (FileMethods.readText(FileMethods.getPagePath(pgnum)));
        System.out.println(result);
        assertNotNull(result);
    }
    
    @Test
    public void testYesNoButtons() throws Exception {
       GameFrame gameGUI = new GameFrame();
       gameGUI.setVisible(true);
       boolean answer = GameFrame.ynInput();
       System.out.println(answer);
       assertNotNull(answer);
    }
    
    @Test
    public void testInputButton() throws Exception {
       GameFrame gameGUI = new GameFrame();
       gameGUI.setVisible(true);
       int answer = GameFrame.intInput(5);
       System.out.println(answer);
       assertNotNull(answer);
    }
    
    @Test
    public void testCharSelect() throws Exception {
       chooseChar2 gameGUI = new chooseChar2();
       gameGUI.setVisible(true);
       String answer = chooseChar2.getChar();
       System.out.println(answer);
       assertNotNull(answer);
    }
    
}
