package additional;

import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import additional.HashMapSort;
import static org.junit.Assert.assertEquals;

public class HashMapSortTest {

    @Test
    public void testSortByKeys() {
        HashMapSort hashMapSort = new HashMapSort();
        Map<String, Integer> unsortedMap = new HashMap<>();
        unsortedMap.put("John", 30);
        unsortedMap.put("Alice", 25);
        unsortedMap.put("Bob", 35);
        unsortedMap.put("Eve", 28);

        Map<String, Integer> sortedByKeyMap = hashMapSort.sortByKeys(unsortedMap);

        // Expected order based on keys: Alice, Bob, Eve, John
        assertEquals("Alice", sortedByKeyMap.keySet().toArray()[0]);
        assertEquals("Bob", sortedByKeyMap.keySet().toArray()[1]);
        assertEquals("Eve", sortedByKeyMap.keySet().toArray()[2]);
        assertEquals("John", sortedByKeyMap.keySet().toArray()[3]);
    }

    @Test
    public void testSortByValues() {
        HashMapSort hashMapSort = new HashMapSort();
        Map<String, Integer> unsortedMap = new HashMap<>();
        unsortedMap.put("John", 30);
        unsortedMap.put("Alice", 25);
        unsortedMap.put("Bob", 35);
        unsortedMap.put("Eve", 28);

        Map<String, Integer> sortedByValueMap = hashMapSort.sortByValues(unsortedMap);

        // Expected order based on values: Alice, Eve, John, Bob
        assertEquals("Alice", sortedByValueMap.keySet().toArray()[0]);
        assertEquals("Eve", sortedByValueMap.keySet().toArray()[1]);
        assertEquals("John", sortedByValueMap.keySet().toArray()[2]);
        assertEquals("Bob", sortedByValueMap.keySet().toArray()[3]);
    }
}

