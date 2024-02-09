package additional;

import additional.UniqueWords;
import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;

class UniqueWordsTest {

    @Test
    void testCountUniqueWords() {
        String input = "the quick brown fox jumps over the lazy dog the quick dog";
        System.out.println("Input string: " + input);
        int uniqueWordsCount = UniqueWords.countUniqueWords(input);
        System.out.println("Number of unique words: " + uniqueWordsCount);
    }
}