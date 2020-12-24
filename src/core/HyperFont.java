package core;

import processing.core.PFont;
import processing.core.PShape;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class HyperFont extends PFont {
    public PFont injectedFont;
    Glyph testGlyph;
    public HyperFont(Font font, boolean smooth, char[] charset, boolean stream, int density) {
        super(font, smooth, charset, stream, density);
    }

    public void initInjection(){
        int pindex = index('π');
        testGlyph = glyphs[pindex];
        //testGlyph.width=30;
    }

    protected boolean isHyperChar(char c){
        return false&&injectedFont!=null && c%2==0;
    }

    @Override
    public Glyph getGlyph(char c) {
        if(isHyperChar(c))
            return testGlyph;
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
        if(isHyperChar(c))
            c = 'π';
        return super.getShape(c);
    }

    @Override
    public PShape getShape(char c, float detail) {
        if(isHyperChar(c))
            c = 'π';
        return super.getShape(c, detail);
    }

    public void debuggy(){
        System.out.println(super.getGlyph('π').width);
    }
}
