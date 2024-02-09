package additional;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import additional.RepeatedDigits;

class RepeatedDigitsFinderTest {

    @Test
    void testFindRepeatedDigits() {
        int number = 12235466;
        System.out.println("Input number: " + number);
        String repeatedDigits = RepeatedDigits.findRepeatedDigits(number);
        System.out.println("Repeated digits: " + repeatedDigits);
    }
}
