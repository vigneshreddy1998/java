package lamdba;

import java.util.Arrays;
import java.util.Comparator;

public class StringSorting {

    public static String[] sortStrings(String[] input) {
        return Arrays.stream(input)
                .sorted(Comparator.comparingInt(String::length)
                        .thenComparing(Comparator.comparing((String s) -> s.charAt(s.length() - 1)).reversed()))
                .toArray(String[]::new);
    }
}
