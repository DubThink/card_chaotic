package Globals;

import bpw.Util;
import core.AdvancedApplet;
import core.HyperFont;
import processing.core.PFont;

import java.awt.*;
import java.util.Arrays;

public class Style {
    public static class FontSpec{
        public FontSpec(AdvancedApplet p, String name, int size, float baseline, float leading) {
            font = (HyperFont)p.createFont(name, size);
            if(name.equals(FONT_FAMILY_NAMES[F_STANDARD]))
                font.injectedFont=p.createFont(name+" Bold", size);
            this.size = size;
            font.baseline = baseline;
            font.leading = leading;
        }
        public void apply(AdvancedApplet p){
            p.textFont(font, size);
        }

        public HyperFont font;
        public int size;
    }
    public static int borderColor = Util.pColor(new Color(135, 135, 135));
    public static int borderColorHover = Util.pColor(new Color(0, 0, 0));

    public static int textColor = Util.pColor(new Color(54, 55, 61));
    public static int textColorHover = Util.pColor(new Color(0, 0, 0));

    public static int fillColor = Util.pColor(new Color(189, 189, 189));
    //public static int fillColorHover = Util.pColor(new Color(127,127,127,200));
    public static int fillColorActive = Util.pColor(new Color(123, 123, 123));
    //public static int fillColorActiveHover = Util.pColor(new Color(65, 65, 65,200));
    public static int fillColorInputField = Util.pColor(new Color(226, 226, 226));
    public static int fillColorPanel = Util.pColor(new Color(226, 226, 226));

    public static int borderRadius = 0; // for some reason having a border radius causes rects to be offset by -0.5,-0.5

    public static int textMargin = 5;

    // font wowe

    public static final int FONT_SMALL = 0;
    public static final int FONT_MEDIUM = 1;
    public static final int FONT_MEDLARGE = 2;
    public static final int FONT_LARGE = 3;
    public static final int FONT_XLARGE = 4;
    public static final int FONT_HUGE = 5;
    static final int[] FONT_SIZES = {16, 18, 22, 32, 36, 48};

    public static final int F_STANDARD = 0;
    public static final int F_FLAVOR = 1;
    public static final int F_CODE = 2;
    public static final int F_SCRIPT = 3;
    public static final int F_IMPACT = 4;
    private static final int FAMILY_COUNT=5;

    static String[] FONT_FAMILY_NAMES = {
            "Palatino Linotype",//"Palatino Linotype",
            "Palatino Linotype Italic",
            "Lucida Console",//"Palatino Linotype Bold",
            "Segoe Print",
            "Century Gothic Bold"
    };

    static float[] FONT_FAMILY_BASELINES = {
            .1f,
            .1f,
            .1f,
            .3f,
            .18f
    };

    static float[] FONT_FAMILY_LEADINGS = {
            .95f,
            .95f,
            .9f,
            .5f,
            .75f
    };

    static FontSpec[][] fontKit;

    public static void loadFonts(AdvancedApplet p){
        System.out.println(Arrays.toString(PFont.list()));
        float startTime = p.millis();
        fontKit = new FontSpec[FAMILY_COUNT][FONT_SIZES.length];
        for(int family=0;family<FAMILY_COUNT;family++){
            for(int size=0;size<FONT_SIZES.length;size++){
                fontKit[family][size]=new FontSpec(p, FONT_FAMILY_NAMES[family], FONT_SIZES[size], FONT_FAMILY_BASELINES[family], FONT_FAMILY_LEADINGS[family]);
            }
        }
        System.out.println("Fonts loaded in "+(p.millis()-startTime)+"ms.");
    }

//    boolean isFontValid(String s){
//        for(String t: PFont.list()){
//            if(s.equals(t))
//                return true;
//        }
//        throw new RuntimeException("Can't find font '"+s+"'");
//    }

    public static FontSpec getFont(int family, int size){
        return fontKit[family][size];
    }

    public static FontSpec chooseFont(int family, float scale){
        float goalSize = Util.max(scale*.6f,scale-20);
//        System.out.println("Goal size: "+goalSize);
        int actualSize=0;
        for(;actualSize<FONT_SIZES.length;actualSize++){
            if(FONT_SIZES[actualSize]>goalSize) {
                actualSize=Util.max(actualSize-1,0);
                break;
            }
        }
//        System.out.println("Chosen size: "+FONT_SIZES[actualSize]);
        return getFont(family,Util.min(actualSize,FONT_SIZES.length-1));
    }
}
