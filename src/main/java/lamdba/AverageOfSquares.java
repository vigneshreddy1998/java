package lamdba;
import java.util.Arrays;

public class AverageOfSquares{
    public static double averageOfSquares() {//main(String[] args) {
        Integer[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        double averageOfSquares = (double)Arrays.stream(numbers)
                .filter(num -> num % 2 != 0)
                .mapToDouble(num -> Math.pow(num, 2))
                .average()
                .orElse(0.0);

        System.out.println("Average of squares of odd numbers: " + averageOfSquares);
        return averageOfSquares;
    }
}
