package additional;

public class RepeatedDigits {

    public static String findRepeatedDigits(int number) {
        String numberStr = String.valueOf(number);
        StringBuilder repeatedDigits = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int digitCount = 0;
            for (int j = 0; j < numberStr.length(); j++) {
                if (Character.getNumericValue(numberStr.charAt(j)) == i) {
                    digitCount++;
                }
            }
            if (digitCount > 1) {
                repeatedDigits.append(i).append(" ");
            }
        }

        return repeatedDigits.toString().trim();
    }
}
