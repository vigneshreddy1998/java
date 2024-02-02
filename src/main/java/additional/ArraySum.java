package additional;
public class ArraySum {
    public static void main(String[] args) {
        // Example array
        int[] numbers = {1, 2, 3, 4, 5};

        // Calculate the sum
        int sum = calculateSum(numbers);

        // Print the sum
        System.out.println("Sum of array elements: " + sum);
    }

    public static int calculateSum(int[] array) {
        int sum = 0;

        // Iterate through each element in the array and add it to the sum
        for (int num : array) {
            sum += num;
        }

        return sum;
    }
}


