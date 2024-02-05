package lamdba;

import java.util.Arrays;

public class SortStringsByLengthAndLastChar {
    public static void main(String[] args) {
        String[] strings = {"Apple", "Banana", "Cherry", "Date", "Fig"};

        // Sorting based on lengths and last characters in descending order using streams and lambda
        String[] sortedStrings = Arrays.stream(strings)
                .sorted((str1, str2) -> {
                    int result = Integer.compare(str2.length(), str1.length()); // First, compare by length in descending order
                    return (result != 0) ? result : Character.compare(str2.charAt(str2.length() - 1), str1.charAt(str1.length() - 1));
                })
                .toArray(String[]::new);

        // Printing the sorted array
        System.out.println("Sorted Strings by Length and Last Char (Descending): " + Arrays.toString(sortedStrings));
    }
}
