package lamdba;

@FunctionalInterface
interface MathOperation {
    int operate(int a, int b);
}

public class Addition {

    public static void main(String[] args) {
        MathOperation addition = (a, b) -> a + b;
        System.out.println("Result of addition: " + addition.operate(5, 3));
    }
}
