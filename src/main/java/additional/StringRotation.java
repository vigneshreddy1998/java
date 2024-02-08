package additional;

public class StringRotation {

    public static boolean areRotations(String str1, String str2) {
        // Check if lengths of both strings are the same
        if (str1.length() != str2.length()) {
            return false;
        }

        // Concatenate str1 with itself
        String concatenatedStr = str1 + str1;

        // Check if str2 is a substring of concatenatedStr
        return concatenatedStr.contains(str2);
    }
}
