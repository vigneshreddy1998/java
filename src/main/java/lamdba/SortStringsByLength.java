package lamdba;
import java.util.Arrays;

public class SortStringsByLength {
    public static void main(String[] args) {
        String[] strings = {"Apple", "Banana", "Cherry", "Date", "Fig"};

        // Sorting based on lengths in ascending order using streams and lambda
        String[] sortedStrings = Arrays.stream(strings)
                .sorted((str1, str2) -> Integer.compare(str1.length(), str2.length()))
                .toArray(String[]::new);

        // Printing the sorted array
        System.out.println("Sorted Strings by Length (Ascending): " + Arrays.toString(sortedStrings));
    }
}
