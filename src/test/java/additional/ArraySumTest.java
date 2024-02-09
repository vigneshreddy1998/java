package additional;

import org.junit.Test;
import additional.ArraySum;
import static org.junit.Assert.assertEquals;

public class ArraySumTest {

    @Test
    public void testCalculateSum() {
        ArraySum arraySum = new ArraySum();
        int[] testArray = {1, 2, 3, 4, 5};
        int expectedSum = 15;

        int actualSum = arraySum.calculateSum(testArray);

        assertEquals(expectedSum, actualSum);
    }

    @Test
    public void testCalculateSumWithNegativeNumbers() {
        ArraySum arraySum = new ArraySum();
        int[] testArray = {-1, -2, -3, -4, -5};
        int expectedSum = -15;

        int actualSum = arraySum.calculateSum(testArray);

        assertEquals(expectedSum, actualSum);
    }

    @Test
    public void testCalculateSumWithEmptyArray() {
        ArraySum arraySum = new ArraySum();
        int[] testArray = {};
        int expectedSum = 0; // The sum of an empty array is 0

        int actualSum = arraySum.calculateSum(testArray);

        assertEquals(expectedSum, actualSum);
    }
}


