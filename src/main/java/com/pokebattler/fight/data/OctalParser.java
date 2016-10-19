package com.pokebattler.fight.data;

import java.util.ArrayList;
import java.util.List;

public class OctalParser {
    public static List<Integer> parseRepeatedInt32(String s) {
        
        final List<Integer> allIntegers = new ArrayList<>();
        for (int i = 0; i < s.length();) {
            int parseInt = 0;
            if (s.charAt(i) == '\\') {
                if (s.charAt(i + 1) >= '0' && s.charAt(i + 1) <= '9') {
                    // todo handle numbers bigger than 512
                    final String substring = s.substring(i + 1, i + 4);
                    parseInt = Integer.parseInt(substring, 8);
                    if (substring.charAt(0) == '0' || substring.charAt(0) == '1') {
                        i += 4;
                    } else {
                        i += 8;
                    }
                } else {
                    // just an escaped character such as \' or \\
                    parseInt = s.substring(i + 1, i + 2).getBytes()[0];
                    i += 2;
                }
            } else {
                parseInt = s.substring(i, i + 1).getBytes()[0];
                i += 1;
            }
            allIntegers.add(parseInt);

        }
        return allIntegers;

    }
}