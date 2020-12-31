package core;

import processing.core.PFont;
import processing.core.PShape;

import java.awt.*;

public class HyperFont extends PFont {
    public PFont boldFont;
    public PFont italicFont;
    public PFont boldItalicFont;
//    Glyph testGlyph;
    public float baseline,leading;
    public boolean bold=false;
    public boolean italic=false;

    public HyperFont(Font font, boolean smooth, char[] charset, boolean stream, int density) {
        super(font, smooth, charset, stream, density);
    }

//    public void initInjection(){
//        int pindex = index('π');
//        testGlyph = glyphs[pindex];
//    }

//    protected boolean isHyperChar(char c){
//        return injectedFont!=null && false;//%2==0;
//    }

    @Override
    public Glyph getGlyph(char c) {
        //System.out.println("getting '"+c+"', injecting = "+injecting);
        test:
        {
            if (bold) {
                if(italic){
                    if(boldItalicFont == null) break test;
                    return boldItalicFont.getGlyph(c);
                } else {
                    if(boldFont == null) break test;
                    return boldFont.getGlyph(c);
                }
            } else {
                if(!italic || italicFont == null) break test;
                return italicFont.getGlyph(c);
            }
        }
        return super.getGlyph(c);
    }

    @Override
    public float kern(char a, char b) {
        // super returns 0, so don't need to worry for now lol
        return super.kern(a, b);
    }

    @Override
    public float width(char c) {
        return 1;
//        if(isHyperChar(c))
//            return 100;//c = 'π';
//        return super.width(c);
    }

    @Override
    public Glyph getGlyph(int i) {
        //throw new RuntimeException("this needs to be looked at");
        return super.getGlyph(i);
    }

    @Override
    public PShape getShape(char c) {
        test:
        {
            if (bold) {
                if(italic){
                    if(boldItalicFont == null) break test;
                    return boldItalicFont.getShape(c);
                } else {
                    if(boldFont == null) break test;
                    return boldFont.getShape(c);
                }
            } else {
                if(!italic || italicFont == null) break test;
                return italicFont.getShape(c);
            }
        }
        return super.getShape(c);
    }

    @Override
    public PShape getShape(char c, float detail) {
        PFont activeFont = getActiveFont();
        if(activeFont == this)
            return super.getShape(c, detail);
        return activeFont.getShape(c, detail);
    }

    public PFont getActiveFont(){
        test:
        {
            if (bold) {
                if(italic){
                    if(boldItalicFont == null) break test;
                    return boldItalicFont;
                } else {
                    if(boldFont == null) break test;
                    return boldFont;
                }
            } else {
                if(!italic || italicFont == null) break test;
                return italicFont;
            }
        }
        return this;
    }
}
