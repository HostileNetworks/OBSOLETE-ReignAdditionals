package com.cosmicdan.reignadditionals.util;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import com.cosmicdan.reignadditionals.Main;

import net.minecraft.util.StatCollector;

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
    
    private final static String prfx = "\u00a7";
    public static final String BLACK = prfx + "0";
    public static final String DARK_BLUE = prfx + "1";
    public static final String DARK_GREEN = prfx + "2";
    public static final String DARK_AQUA = prfx + "3";
    public static final String DARK_RED = prfx + "4";
    public static final String PURPLE = prfx + "5";
    public static final String ORANGE = prfx + "6";
    public static final String GREY = prfx + "7";
    public static final String DARK_GREY = prfx + "8";
    public static final String INDIGO = prfx + "9";
    public static final String BRIGHT_GREEN = prfx + "a";
    public static final String AQUA = prfx + "b";
    public static final String RED = prfx + "c";
    public static final String PINK = prfx + "d";
    public static final String YELLOW = prfx + "e";
    public static final String WHITE = prfx + "f";
    public static final String BOLD = prfx + "l";
    public static final String UNDERLINE = prfx + "n";
    public static final String ITALIC = prfx + "o";
    public static final String END = prfx + "r";
    public static final String OBFUSCATED = prfx + "k";
    public static final String STRIKETHROUGH = prfx + "m";
    public static final String DEGREES = "\u00B0" + "C";
    
    public static String translate(String key) {
        return StatCollector.translateToLocal(key);
    }

    public static String removeDecimals(String name) {
        String theName;
        String[] aName = name.split("\\.");
        if (aName.length == 2) {
            theName = aName[0] + aName[1].substring(0, 1).toUpperCase() + aName[1].substring(1);
        } else {
            theName = name;
        }
        
        return theName;
    }
}
