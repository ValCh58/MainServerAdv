package processingdata;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Valeriy
 */
public class DataFromHeaterMeterTest {
    
    public DataFromHeaterMeterTest(){
        
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of setDataMeter method, of class DataFromHeaterMeter.
     */
    @Test
    public void testSetDataMeter() {
        System.out.println("setDataMeter");
        String message = "KARAT(offline)";
        DataFromHeaterMeter dfhm = new DataFromHeaterMeter();
        boolean expResult = true;
        boolean result = dfhm.setDataMeter(message);
        assertEquals(expResult, result);
                
        fail("The test case is a prototype.");
    }
    
}
