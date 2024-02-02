package additional;
import java.util.Scanner;
public class FactorialCalculator {
    public static void main(String[] args) {
        // Create a Scanner object for user input
        Scanner scanner = new Scanner(System.in);

        // Prompt the user to enter a number
        System.out.print("Enter a number to calculate its factorial: ");
        int number = scanner.nextInt();

        // Calculate and display the factorial
        long factorial = calculateFactorial(number);
        System.out.println("Factorial of " + number + " = " + factorial);

        // Close the Scanner to prevent resource leak
        scanner.close();
    }

    // Method to calculate the factorial of a number
    public static long calculateFactorial(int n) {
        if (n < 0) {
            // Factorial is not defined for negative numbers
            throw new IllegalArgumentException("Factorial is not defined for negative numbers.");
        }

        long result = 1;

        for (int i = 1; i <= n; i++) {
            result *= i;
        }

        return result;
    }

}