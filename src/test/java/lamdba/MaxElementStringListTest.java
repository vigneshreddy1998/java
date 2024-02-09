package lamdba;

import lamdba.MaxElementStringList;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MaxElementStringListTest {

    @Test
    void testFindMaxElement() {
        List<String> inputList = Arrays.asList("apple", "orange", "banana", "grape");
        String expectedOutput = "orange";

        String result = MaxElementStringList.findMaxElement(inputList);
        assertEquals(expectedOutput, result);
    }
}
