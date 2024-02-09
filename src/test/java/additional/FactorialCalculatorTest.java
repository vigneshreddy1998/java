package additional;

import org.junit.Test;
import additional.FactorialCalculator;

import static org.junit.Assert.assertEquals;

public class FactorialCalculatorTest {

    @Test
    public void testCalculateFactorialForZero() {
        long result = FactorialCalculator.calculateFactorial(0);
        assertEquals(1, result);
    }

    @Test
    public void testCalculateFactorialForPositiveNumber() {
        long result = FactorialCalculator.calculateFactorial(5);
        assertEquals(120, result);
    }

    @Test
    public void testCalculateFactorialForNegativeNumber() {
        try {
            FactorialCalculator.calculateFactorial(-3);
            // The line above should throw an IllegalArgumentException
        } catch (IllegalArgumentException e) {
            assertEquals("Factorial is not defined for negative numbers.", e.getMessage());
        }
    }
}

