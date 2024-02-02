package additional;
import java.util.*;
public class HashMapSort {

    public static void main(String[] args) {
        // Create a HashMap
        Map<String, Integer> unsortedMap = new HashMap<>();
        unsortedMap.put("John", 30);
        unsortedMap.put("Alice", 25);
        unsortedMap.put("Bob", 35);
        unsortedMap.put("Eve", 28);

        // Sort and print by keys
        System.out.println("Sorted by Keys:");
        Map<String, Integer> sortedByKeyMap = sortByKeys(unsortedMap);
        printMap(sortedByKeyMap);

        // Sort and print by values
        System.out.println("\nSorted by Values:");
        Map<String, Integer> sortedByValueMap = sortByValues(unsortedMap);
        printMap(sortedByValueMap);
    }

    // Method to sort a HashMap by keys
    public static Map<String, Integer> sortByKeys(Map<String, Integer> unsortedMap) {
        Map<String, Integer> sortedMap = new TreeMap<>(unsortedMap);
        return sortedMap;
    }

    // Method to sort a HashMap by values
    public static Map<String, Integer> sortByValues(Map<String, Integer> unsortedMap) {
        List<Map.Entry<String, Integer>> entryList = new LinkedList<>(unsortedMap.entrySet());

        // Custom comparator to sort by values
        Collections.sort(entryList, Comparator.comparing(Map.Entry::getValue));

        // Create a LinkedHashMap to store the sorted entries
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    // Method to print a Map
    public static void printMap(Map<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
