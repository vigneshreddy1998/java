package lamdba;

import lamdba.MathOperation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AdditionTest {

    @Test
    void testAddition() {
        MathOperation addition = (a, b) -> a + b;
        assertEquals(8, addition.operate(5, 3));
    }
}
