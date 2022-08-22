package de.craftsblock.craftscore.buildin.animate.templates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypingAnimation {

    public static List<String> create(String text, int stay) {
        List<String> animation = new ArrayList<>(Collections.singleton(""));
        String[] tiles = text.split("");
        StringBuilder last = new StringBuilder();
        for (String o : tiles) {
            last.append(o);
            animation.add(last.toString());
        }
        for (int i = 0; i < stay; i++)
            animation.add(text);
        int sub = tiles.length;
        for (String tile : tiles) {
            sub -= 1;
            animation.add(text.substring(0, sub));
        }
        return animation;
    }

}
