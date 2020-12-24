package UI;

import bpw.Util;
import core.AdvancedApplet;
import core.HyperFont;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PShape;

import java.awt.*;

public class Style {
    public static class FontSpec{
        public FontSpec(AdvancedApplet p, String name, int size) {
            font = (HyperFont)p.createFont(name, size);
            font.injectedFont=p.createFont("Comic Sans MS", size);
            this.size = size;
        }
        public void apply(AdvancedApplet p){
            p.textFont(font, size);
        }

        public HyperFont font;
        public int size;
    }
    public static int borderColor = Util.pColor(Color.lightGray);
    public static int borderColorHover = Util.pColor(new Color(120,140,200));
    public static int fillColor = Util.pColor(Color.darkGray,100);
    public static int fillColorHover = Util.pColor(Color.gray,100);
    public static int borderRadius = 4;
    public static FontSpec font32;

    public static void loadFonts(AdvancedApplet p){
        //p.println(PFont.list());
        font32 = new FontSpec(p,"Candara", 32);//
    }
}
