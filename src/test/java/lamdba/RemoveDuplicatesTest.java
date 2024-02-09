package lamdba;

import lamdba.RemoveDuplicates;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RemoveDuplicatesTest {

    @Test
    void testRemoveDuplicates() {
        List<Integer> inputList = Arrays.asList(1, 2, 3, 2, 4, 3, 5);
        List<Integer> expectedOutput = Arrays.asList(1, 2, 3, 4, 5);

        List<Integer> result = RemoveDuplicates.removeDuplicates(inputList);
        assertEquals(expectedOutput, result);
    }
}
