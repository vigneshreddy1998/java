package additional;

import java.util.HashMap;
import java.util.Map;

public class RepeatedCharacters {

    public static void printRepeatedCharacters(String input) {
        Map<Character, Integer> charCountMap = new HashMap<>();

        // Count occurrences of each character
        for (char ch : input.toCharArray()) {
            charCountMap.put(ch, charCountMap.getOrDefault(ch, 0) + 1);
        }

        // Print repeated characters
        System.out.print("Repeated characters in '" + input + "': ");
        for (Map.Entry<Character, Integer> entry : charCountMap.entrySet()) {
            if (entry.getValue() > 1) {
                System.out.print(entry.getKey() + " ");
            }
        }
        System.out.println();
    }
}
