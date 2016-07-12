package com.cosmicdan.reignadditionals.util;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class TextUtils {
    public static List<String> splitTextString(String text, int maxLineLength) {
        StringTokenizer tok = new StringTokenizer(text, " ");
        StringBuilder output = new StringBuilder(text.length());
        int lineLen = 0;
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();

            while(word.length() > maxLineLength){
                output.append(word.substring(0, maxLineLength-lineLen) + "\n");
                word = word.substring(maxLineLength-lineLen);
                lineLen = 0;
            }

            if (lineLen + word.length() > maxLineLength) {
                output.append("\n");
                lineLen = 0;
            }
            output.append(word + " ");

            lineLen += word.length() + 1;
        }
        // output.split();
        // return output.toString();
        return Arrays.asList(output.toString().split("\n"));
    }
}
