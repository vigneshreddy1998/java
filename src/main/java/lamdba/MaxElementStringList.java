package lamdba;

import java.util.List;

public class MaxElementStringList {

    public static String findMaxElement(List<String> strings) {
        return strings.stream()
                .max(String::compareTo)
                .orElse(null);
    }


}
