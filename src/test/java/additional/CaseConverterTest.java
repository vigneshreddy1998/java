package additional;

import additional.CaseConverter;
import org.junit.Test;
import static org.junit.Assert.*;

public class CaseConverterTest {

    @Test
    public void testConvertCase() {
        assertEquals("HELLO world", CaseConverter.convertCase("hello WORLD"));
        assertEquals("java PROGRAMMING", CaseConverter.convertCase("JAVA programming"));
        assertEquals("123!@#", CaseConverter.convertCase("123!@#"));
    }
}
