package additional;

public class ConversionException {

    public static int convertStringToInt(String str) throws NumberFormatException {
        return Integer.parseInt(str);
    }

    public static void main(String[] args) {
        String[] inputs = {"23", "45.67", "test", "123f"};

        for (String input : inputs) {
            try {
                int result = convertStringToInt(input);
                System.out.println("Input: " + input + ", Output: " + result);
            } catch (NumberFormatException e) {
                System.out.println("Input: " + input + ", Exception: " + e.getMessage());
            }
        }
    }
}
