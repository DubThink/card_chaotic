package core;

import processing.core.PFont;
import processing.core.PShape;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class HyperFont extends PFont {
    public PFont injectedFont;
//    Glyph testGlyph;
    public float baseline,leading;
    public boolean injecting=false;
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
        if(injecting && injectedFont!=null)
            return injectedFont.getGlyph(c);
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
        if(injecting&& injectedFont!=null)
            return injectedFont.getShape(c);
        return super.getShape(c);
    }

    @Override
    public PShape getShape(char c, float detail) {
        if(injecting&& injectedFont!=null)
            return injectedFont.getShape(c, detail);
        return super.getShape(c, detail);
    }
}
