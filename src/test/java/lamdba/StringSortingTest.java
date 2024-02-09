package lamdba;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;

public class StringSortingTest {


    @Test
    public void testSortStringsSameLength() {
        String[] inputArray = {"banana", "apple", "kiwi", "grape"};
        String[] expectedOutput = {"kiwi", "apple", "grape", "banana"};

        assertArrayEquals(expectedOutput, StringSorting.sortStrings(inputArray));
    }

    @Test
    public void testSortStringsEmptyArray() {
        String[] inputArray = {};
        String[] expectedOutput = {};

        assertArrayEquals(expectedOutput, StringSorting.sortStrings(inputArray));
    }

    @Test
    public void testSortStringsSingleElementArray() {
        String[] inputArray = {"apple"};
        String[] expectedOutput = {"apple"};

        assertArrayEquals(expectedOutput, StringSorting.sortStrings(inputArray));
    }

}