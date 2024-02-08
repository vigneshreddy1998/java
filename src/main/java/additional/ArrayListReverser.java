package additional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrayListReverser {

    public static List<Integer> reverseArrayList(List<Integer> originalList) {
        List<Integer> reversedList = new ArrayList<>(originalList);
        Collections.reverse(reversedList);
        return reversedList;
    }
}
