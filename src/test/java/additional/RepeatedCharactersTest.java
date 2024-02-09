package additional;

import additional.RepeatedCharacters;
import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//import additional.RepeatedCharacters;

public class RepeatedCharactersTest {

    @Test
    void testPrintRepeatedCharacters() {
        String input = "hello world";
        System.out.print("Input string: " + input + "\n");
        RepeatedCharacters.printRepeatedCharacters(input);
    }
}
