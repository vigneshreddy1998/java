package lamdba;

import java.util.Arrays;

public class SortStrings {

    public static void main(String[] args) {
        String[] strings = {"apple", "banana", "cherry", "date", "fig"};

        Arrays.sort(strings, (s1, s2) ->
                s1.length() == s2.length() ?
                        Character.compare(s2.charAt(s2.length() - 1), s1.charAt(s1.length() - 1)) :
                        Integer.compare(s1.length(), s2.length()));

        // Print the sorted array
        System.out.println(Arrays.toString(strings));
    }
}
