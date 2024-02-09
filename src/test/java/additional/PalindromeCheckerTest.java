package additional;

import org.junit.Test;
import additional.PalindromeChecker;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class PalindromeCheckerTest {

    @Test
    public void testIsPalindromeForPalindrome() {
        assertTrue(PalindromeChecker.isPalindrome(121));
        assertTrue(PalindromeChecker.isPalindrome(1221));
        assertTrue(PalindromeChecker.isPalindrome(12321));
    }

    @Test
    public void testIsPalindromeForNonPalindrome() {
        assertFalse(PalindromeChecker.isPalindrome(123));
        assertFalse(PalindromeChecker.isPalindrome(12345));
        assertFalse(PalindromeChecker.isPalindrome(987654));
    }

    @Test
    public void testIsPalindromeForSingleDigit() {
        assertTrue(PalindromeChecker.isPalindrome(0));
        assertTrue(PalindromeChecker.isPalindrome(5));
        assertTrue(PalindromeChecker.isPalindrome(9));

    }
}
