package lamdba;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StringFilter {

    public static List<String> filterStrings(List<String> input, Predicate<String> predicate) {
        return input.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
}
