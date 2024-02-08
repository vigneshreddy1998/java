import additional.ConversionException;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ConversionExceptionTest {

    @Test
    public void testConvertStringToInt() {
        assertEquals(23, ConversionException.convertStringToInt("23"));
        assertEquals(0, ConversionException.convertStringToInt("0")); // Additional test case for zero
    }

    @Test(expected = NumberFormatException.class)
    public void testConvertStringToIntWithNonIntegerInput() {
        ConversionException.convertStringToInt("45.67");
    }

    @Test(expected = NumberFormatException.class)
    public void testConvertStringToIntWithInvalidInput() {
        ConversionException.convertStringToInt("test");
    }

    @Test(expected = NumberFormatException.class)
    public void testConvertStringToIntWithInvalidInput2() {
        ConversionException.convertStringToInt("123f");
    }
}
