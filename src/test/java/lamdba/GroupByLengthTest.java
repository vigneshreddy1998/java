package lamdba;

import lamdba.GroupByLength;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupByLengthTest {


    @Test
    void testGroupByLengthEmptyList() {
        List<String> inputList = Arrays.asList();
        Map<Integer, List<String>> expectedOutput = new HashMap<>();

        Map<Integer, List<String>> result = GroupByLength.groupByLength(inputList);
        assertEquals(expectedOutput, result);
    }

    @Test
    void testGroupByLengthSingleElementList() {
        List<String> inputList = Arrays.asList("apple");
        Map<Integer, List<String>> expectedOutput = new HashMap<>();
        expectedOutput.put(5, Arrays.asList("apple"));

        Map<Integer, List<String>> result = GroupByLength.groupByLength(inputList);
        assertEquals(expectedOutput, result);
    }


}
