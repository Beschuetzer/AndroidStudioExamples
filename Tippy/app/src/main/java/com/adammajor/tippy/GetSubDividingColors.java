package com.adammajor.tippy;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GetSubDividingColors {
    private String startColor;
    private String endColor;

    public GetSubDividingColors(String startColor, String endColor) throws InvalidParameterException{
        boolean areColorsValid = validateColors(startColor, endColor);
        if (!areColorsValid) throw new InvalidParameterException("One of the colors is not a valid hexadecimal string");
        this.startColor = startColor;
        this.endColor = endColor;
    }

    public String getStartColor() {
        return startColor;
    }

    public String getEndColor() {
        return endColor;
    }

    public List<String> colors(Integer numberOfIntermediateColorsNeeded) {
        List<String> toReturn = new ArrayList<>();

        //assuming startColor and endColor are valid hex string.
        // standardize strings (remove '#' if it exists)

        //working with standardized 6 length hexstring:
        // need to convert each red, green, and blue values to integers

        //divide starting and ending value for each color by (numberOfIntermediateColorsNeeded + 1)
        // and return a list of colors as integers (e.g. "#ffabcfe1")


        return toReturn;
    }

    private boolean validateColors(String startColor, String endColor) {
        String validChars = "[0-9a-fA-F]";
        String hexRegexp = String.format("^#*(%s{6}|%s{8})$", validChars, validChars);
        boolean startColorIsValidPattern = Pattern.matches(hexRegexp, startColor);
        boolean endColorIsValidPattern = Pattern.matches(hexRegexp, endColor);
        return startColorIsValidPattern && endColorIsValidPattern;
    }
}
