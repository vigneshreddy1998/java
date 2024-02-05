package lamdba;
import java.util.Arrays;
import java.util.SortedMap;
public class CapitalizeAndSort{
    public static void main(String[] args){
        String[] name = {"ramesh", "suresh", "naresh", "sukesh"};
        CapitalizeAndSort(name);
    }
    public static void CapitalizeAndSort(String[] name){
        name = Arrays.stream(name)
                .map(s -> s.substring(0,1).toUpperCase() + s.substring(1))
                .toArray(String[]::new);
        Arrays.sort(name);
        Arrays.stream(name).forEach(System.out::println);
    }
}
