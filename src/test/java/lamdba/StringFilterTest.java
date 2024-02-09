package lamdba;

import lamdba.StringFilter;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StringFilterTest {

    @Test
    void testFilterStrings() {
        List<String> inputList = Arrays.asList("Apple", "Banana", "Orange", "Grape");
        List<String> expectedOutput = Arrays.asList("Apple");

        List<String> result = StringFilter.filterStrings(inputList, s -> s.startsWith("A"));
        assertEquals(expectedOutput, result);
    }
}
