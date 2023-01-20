package util;

import com.badlogic.gdx.math.Vector3;

public class StringUtils
{

    public static String humanJavaTypeName(String camelCase) {
        String result = "";
        boolean prevUpperCase = false;
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (!Character.isLetter(c)) return camelCase;
            if (Character.isUpperCase(c) && !prevUpperCase) {
                if (i > 0) result += " ";
                result += c;
                prevUpperCase = true;
            }
            else {
                result += c;
                prevUpperCase = false;
            }
        }
        return result;
    }

    public static String camelCaseToUnderScoreUpperCase(String camelCase) {
        String result = "";
        boolean prevUpperCase = false;
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (!Character.isLetter(c)) return camelCase;
            if (Character.isUpperCase(c)) {
                if (prevUpperCase) return camelCase;
                result += "_" + c;
                prevUpperCase = true;
            }
            else {
                result += Character.toUpperCase(c);
                prevUpperCase = false;
            }
        }
        return result;
    }

    public static String trimVector3(Vector3 v) {
        return trimFloat(v.x) + ", " + trimFloat(v.y) + ", " + trimFloat(v.z);
    }

    //null safe trim float to 2 decimal places
    public static String trimFloat(float f) {
        if (f == (int) f) return String.valueOf((int) f);
        String string = String.format("%(-2.2f" , f);
        //ensure 5 digits
        if (string.length() < 5) string += "0";
        return string;
    }

    public static boolean matchesSuffix(String string , String suffix) {
        int suffixLength = suffix.length();
        int stringLength = string.length();
        String stringSuffix = string.substring(stringLength - suffixLength);
        return stringSuffix.equals(suffix);

    }

}
