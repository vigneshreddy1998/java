package lamdba;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AverageOfSquaresTest {

    @Test
    public void testAverageOfSquaresOfOddNumbers() {
        int[] inputArray = {1, 2, 3, 4, 5};
        double expectedOutput = (1 * 1 + 3 * 3 + 5 * 5) / 3.0;

        assertEquals(expectedOutput, AverageOfSquares.averageOfSquares(inputArray), 0.0001);
    }

    @Test
    public void testAverageOfSquaresEmptyArray() {
        int[] inputArray = {};

        assertEquals(0.0, AverageOfSquares.averageOfSquares(inputArray), 0.001);
    }

    @Test
    public void testAverageOfSquaresNoOddNumbers() {
        int[] inputArray = {2, 4, 6, 8};

        assertEquals(0.0, AverageOfSquares.averageOfSquares(inputArray), 0.001);
    }
}
