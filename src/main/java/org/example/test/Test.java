package org.example.test;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    public static void main(String[] args) {
        String text = " 水晶 - 3 石头-1";
        text = StringUtils.deleteWhitespace(text);
        Pattern pattern = Pattern.compile("([^0-9\\-]+)-(\\d+)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()){
            System.out.println(matcher.group());
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
    }
}
