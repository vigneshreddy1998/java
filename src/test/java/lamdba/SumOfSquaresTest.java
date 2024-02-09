package lamdba;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SumOfSquaresTest {

    @Test
    void testSumOfSquares() {
        List<Integer> inputList = Arrays.asList(1, -2, 3, -4, 5);
        int expectedOutput = 35;

        int result = SumOfSquares.sumOfSquares(inputList);
        assertEquals(expectedOutput, result);
    }
}
