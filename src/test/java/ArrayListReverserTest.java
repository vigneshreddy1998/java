import additional.ArrayListReverser;
import org.testng.annotations.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ArrayListReverserTest {

    @Test
    void testReverseArrayList() {
        List<Integer> originalList = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> reversedList = ArrayListReverser.reverseArrayList(originalList);
        List<Integer> expectedList = Arrays.asList(5, 4, 3, 2, 1);
        assertEquals(expectedList, reversedList);
    }
}
