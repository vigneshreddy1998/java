
import additional.StringRotation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StringRotationTest {

    @Test
    void testAreRotations() {
        String str1 = "abcd";
        String str2 = "cdab";
        assertTrue(StringRotation.areRotations(str1, str2));
    }
}
