package additional;

import java.util.Scanner;
public class PalindromeChecker {
    public static void main(String[] args) {
        // Create a Scanner object for user input
        Scanner scanner = new Scanner(System.in);

        // Prompt the user to enter a number
        System.out.print("Enter a number: ");
        int number = scanner.nextInt();

        // Check if the number is a palindrome
        if (isPalindrome(number)) {
            System.out.println(number + " is a palindrome.");
        } else {
            System.out.println(number + " is not a palindrome.");
        }

        // Close the Scanner to prevent resource leak
        scanner.close();
    }

    // Method to check if a number is a palindrome
    public static boolean isPalindrome(int num) {
        int originalNumber = num;
        int reversedNumber = 0;

        // Reverse the digits of the number
        while (num != 0) {
            int digit = num % 10;
            reversedNumber = reversedNumber * 10 + digit;
            num /= 10;
        }

        // Check if the reversed number is equal to the original number
        return originalNumber == reversedNumber;
    }
}
