package additional;

import java.util.HashMap;
import java.util.Map;

public class UniqueWords {

    public static int countUniqueWords(String input) {
        Map<String, Integer> wordCountMap = new HashMap<>();

        // Split the input string into words
        String[] words = input.split("\\s+");

        // Count occurrences of each word
        for (String word : words) {
            wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
        }

        // Count the number of unique words
        return (int) wordCountMap.keySet().stream().filter(word -> wordCountMap.get(word) == 1).count();
    }
}
