package core;

import processing.core.PFont;
import processing.opengl.PGL;
import processing.opengl.PGraphics2D;
import processing.opengl.PGraphicsOpenGL;

import java.awt.*;
import java.io.InputStream;
import java.util.Queue;
import java.util.ArrayDeque;

public class AdvancedGraphics extends PGraphics2D {
    class SymbolRender{
        float x,y;
        Symbol symbol;

        public SymbolRender(Symbol symbol, float x, float y) {
            this.x = x;
            this.y = y;
            this.symbol = symbol;
        }

        public void draw(){
            float renderWidth = textSize*symbol.mWidth;
            float renderHeight=textSize*symbol.mWidth;
            image(symbol.image, x+ Symbol.wPad*textSize, y-renderHeight+Symbol.vOffset*textSize, renderWidth,renderHeight);
//            stroke(255,0,0);
//            line(x-5,y,x+5,y);
//            stroke(0,255,0);
//            line(x,y-5,x,y+5);
        }
    }
    Queue<SymbolRender> symbolsToRender;

    public HyperFont getHyperFont(){
        return ((HyperFont)textFont);
    }

    protected AdvancedPJOGL getAdvPGL(){
        return (AdvancedPJOGL)pgl;
    }

    public void initializeInjector(){
        symbolsToRender = new ArrayDeque<>();
    }

    @Override
    public void text(String str, float x, float y) {
        HyperFont hfont = getHyperFont();
        float saveLeading = textLeading;
        y-=hfont.baseline*textSize;
        textLeading*=hfont.leading;

        super.text(str, x, y);

        while(!symbolsToRender.isEmpty()){
            symbolsToRender.poll().draw();
        }
        textLeading = saveLeading;
        hfont.bold=false;
        hfont.italic=false;
    }

    @Override
    protected void textCharImpl(char ch, float x, float y) {
//        System.out.println("looking up %c(0x%02x). val: %b".formatted(ch, (int)ch, SymbolInjector.isCharSymbol(ch)));

        if (!SymbolInjector.isCharSymbol(ch)) {
            if (ch == AdvancedApplet.CC_BOLD)
                getHyperFont().bold = !getHyperFont().bold;
            else if (ch == AdvancedApplet.CC_ITALIC)
                getHyperFont().italic = !getHyperFont().italic;
            else
                super.textCharImpl(ch, x, y);
        } else {
            // enqueue a symbol render
            symbolsToRender.add(new SymbolRender(SymbolInjector.lookupSymbol(ch),x,y));
        }
    }

    @Override
    protected float textWidthImpl(char[] buffer, int start, int stop) {
        // TODO optimization for stop-start==1 case
        boolean saveBold = getHyperFont().bold;
        boolean saveItalic = getHyperFont().italic;
        float wide = 0;
        int beginning = start;
        Object font = getHyperFont().getActiveFont().getNative();
        for (int i = start; i < stop; i++) {
            if (buffer[i] == AdvancedApplet.CC_BOLD || buffer[i] == AdvancedApplet.CC_ITALIC) {

                if(buffer[i] == AdvancedApplet.CC_ITALIC)
                    getHyperFont().italic = !getHyperFont().italic;
                else if(buffer[i] == AdvancedApplet.CC_BOLD)
                    getHyperFont().bold = !getHyperFont().bold;

                // refresh font ref
                font = getHyperFont().getActiveFont().getNative();

                if(i>beginning) // catch up
                    wide += getAdvPGL().getTextWidth(font, buffer, beginning, i);
                beginning=i+1; // skip the special char

            } else if(SymbolInjector.isCharSymbol(buffer[i])) {
                if(i>beginning) // catch up
                    wide += getAdvPGL().getTextWidth(font, buffer, beginning, i);
                // inject special char spacing
                wide += (SymbolInjector.lookupSymbol(buffer[i]).mWidth + Symbol.wPad*2) * textSize;
                beginning=i+1;
            }
        }
        if(beginning<stop) {
            wide += getAdvPGL().getTextWidth(font, buffer, beginning, stop);
        }
        getHyperFont().bold = saveBold;
        getHyperFont().italic = saveItalic;
        return wide;
    }

    @Override
    protected PGL createPGL(PGraphicsOpenGL pg) {
        return new AdvancedPJOGL(pg);
    }

    @Override
    protected PFont createFont(String name, float size, boolean smooth, char[] charset) {
        String lowerName = name.toLowerCase();
        Font baseFont = null;

        try {
            InputStream stream = null;
            if (!lowerName.endsWith(".otf") && !lowerName.endsWith(".ttf")) {
                baseFont = PFont.findFont(name);
            } else {
                stream = this.parent.createInput(name);
                if (stream == null) {
                    System.err.println("The font \"" + name + "\" " + "is missing or inaccessible, make sure " + "the URL is valid or that the file has been " + "added to your sketch and is readable.");
                    return null;
                }

                baseFont = Font.createFont(0, this.parent.createInput(name));
            }

            return new HyperFont(baseFont.deriveFont(size * (float)this.parent.pixelDensity), smooth, charset, stream != null, this.parent.pixelDensity);
        } catch (Exception var8) {
            System.err.println("Problem with createFont(\"" + name + "\")");
            var8.printStackTrace();
            return null;
        }
    }
}
