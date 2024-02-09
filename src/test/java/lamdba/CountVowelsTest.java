package lamdba;

import lamdba.CountVowels;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CountVowelsTest {

    @Test
    public void testCountVowels() {
        assertEquals(3, CountVowels.countVowels("Elephant"));
        assertEquals(1, CountVowels.countVowels("Dog"));
        assertEquals(1, CountVowels.countVowels("cat"));
        assertEquals(2, CountVowels.countVowels("rabbit"));
    }

    @Test
    public void testCountVowelsEmptyString() {
        assertEquals(0, CountVowels.countVowels(""));
    }

    @Test
    public void testCountVowelsNoVowels() {
        assertEquals(0, CountVowels.countVowels("xyz"));
    }

    @Test
    public void testCountVowelsMixedCase() {
        assertEquals(3, CountVowels.countVowels("ELePhAnt"));
    }
}
