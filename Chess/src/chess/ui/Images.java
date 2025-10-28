package chess.ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Images {
    private static final Map<String, Image> cache = new HashMap<>();

    public static Image getImage(String code) {
        // code like "WP" (white pawn), "BK" (black king)
        if (!cache.containsKey(code)) {
        	String file = "/images/" + code + ".png";
        	ImageIcon icon = new ImageIcon(Images.class.getResource(file));
            cache.put(code, icon.getImage());
        }
        return cache.get(code);
    }
}
