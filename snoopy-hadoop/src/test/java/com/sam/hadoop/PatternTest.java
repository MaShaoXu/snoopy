package com.sam.hadoop;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {

    public static void main(String[] args) {
        String splitStr = "testa:fwafwae";
        String pattern = "testa:.*";
        Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(splitStr);
        while (matcher.find()) {
            String allcon = matcher.group(0);
            System.out.println(allcon);
        }
    }

}
