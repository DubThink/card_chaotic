package core;

import Globals.Assert;
import Globals.Debug;
import Globals.GlobalEnvironment;
import aew.Util;
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
    Queue<SymbolRender> symbolsToRender = new ArrayDeque<>();

    class DeferredLineRender {
        int start;
        int end;
        float dx,dy;

        public DeferredLineRender(int start, int end, float dx, float dy) {
            this.start = start;
            this.end = end;
            this.dx = dx;
            this.dy = dy;
        }
    }

    Queue<DeferredLineRender> linesToRender = new ArrayDeque<>();

    public HyperFont getHyperFont(){
        return ((HyperFont)textFont);
    }

    protected AdvancedPJOGL getAdvPGL(){
        return (AdvancedPJOGL)pgl;
    }

    public void initializeInjector(){}

    @Override
    public void text(String str, float x, float y) {
        float saveLeading = textLeading;

        HyperFont hfont = getHyperFont();

        y-=hfont.baseline*textSize;
        textLeading*=hfont.leading;

        super.text(str, x, y);

        while(!symbolsToRender.isEmpty()){
            symbolsToRender.poll().draw();
        }

        hfont.bold=false;
        hfont.italic=false;

        textLeading = saveLeading;
    }

    public int textClipped(String str, int start, float x, float y, float width, float height) {
        int length = str.length();
        if (length > this.textBuffer.length) {
            this.textBuffer = new char[length + 10];
        }

        str.getChars(0, length, this.textBuffer, 0);
        return textClippedImpl(this.textBuffer, start, length, x, y, width, height, false);
    }

    @Override
    public void strokeWeight(float weight) {
        if(GlobalEnvironment.DEV_MODE && weight != 1){
            throw new RuntimeException("strokeWeight perf blows, don't touch. Use expertStrokeWeight() if you know what you're doing.");
        }
        this.strokeWeight = weight;
    }

    public void expertStrokeWeight(float weight) {
        this.strokeWeight = weight;
    }



    protected int textClippedImpl(char[] chars, int start, int end, float x, float y, float width, float height, boolean charClipping) {

        float saveLeading = textLeading;

        HyperFont hfont = getHyperFont();

        y-=hfont.baseline*textSize;
        textLeading*=hfont.leading;

        // calculate the number of lines to render
//        float high = 0.0F;
//        int renderedLineCount=1;
//
//        for(int index = start; index < chars.length && high<height; ++index) {
//            if (chars[index] == '\n') {
//                high += this.textLeading;
//                renderedLineCount++;
//            }
//        }
//        if (high>height){
//            // the last line puts us over, so remove 1
//            renderedLineCount--;
//            high-=this.textLeading;
//        }

        // other text aligns not supported at the moment, since computing the height beforehand is a pain

        int lineStart = start;

        float renderedHeight=0;
        while (lineStart<end && renderedHeight+this.textLeading<=height) {
            int eol = Util.findIndexOfNext(chars, lineStart, '\n');
            if (eol!=-1)
                eol = Util.min(eol, end);
            else
                eol = end;
            lineStart = textLineClippedQueueRender(chars,lineStart, eol,0,renderedHeight, width, charClipping);
            renderedHeight+=this.textLeading;
            if(lineStart<end && Util.isWhitespaceChar(chars[lineStart]))
                lineStart++;
        }

//        int eol = Util.findIndexOfNext(chars, lineStart, '\n');
//        if (eol!=-1)
//            end = Util.min(eol, end);
//
//        lineStart = textLineClippedImpl(chars, lineStart, end, x, y, width, charClipping);

        if (this.textAlignY == 3) {
            y += (this.textAscent() - renderedHeight) / 2.0F;
        } else if (this.textAlignY == 101) {
            y += this.textAscent();
        } else if (this.textAlignY == 102) {
            y -= this.textDescent() + renderedHeight;
        }

        renderQueuedLines(chars, x,y);


        while(!symbolsToRender.isEmpty()){
            symbolsToRender.poll().draw();
        }

        hfont.bold=false;
        hfont.italic=false;

        textLeading = saveLeading;

        return lineStart;
    }


    public int textLineClipped(String s, float x, float y, float maxWidth){
        return textLineClipped(s,0, x, y, maxWidth);
    }

    public int textLineClipped(String s, float x, float y, float maxWidth, boolean charClipping){
        return textLineClipped(s,0, x, y, maxWidth,charClipping);
    }

    public int textLineClipped(String s, int start, float x, float y, float maxWidth) {
        return textLineClipped(s,start,x,y,maxWidth,false);
    }

    public int textLineClipped(String s, int start, float x, float y, float maxWidth, boolean charClipping) {
        float saveLeading = textLeading;

        HyperFont hfont = getHyperFont();

        y-=hfont.baseline*textSize;
        textLeading*=hfont.leading;

        int length = s.length();



        if (length > textWidthBuffer.length) {
            textWidthBuffer = new char[length + 10];
        }

        s.getChars(start, length, textWidthBuffer, start);

        int count = textLineClippedImpl(textWidthBuffer,start,length,x,y,maxWidth,charClipping);

        while(!symbolsToRender.isEmpty()){
            symbolsToRender.poll().draw();
        }

        hfont.bold=false;
        hfont.italic=false;

        textLeading = saveLeading;
        return count;
    }

    /**
     * @param charClipping only clip by character rather than by word
     * @return the index of the first unrendered character
     */
    protected int textLineClippedImpl(char[] chars, int start, int end, float x, float y, float maxWidth, boolean charClipping) {
        int res = textLineClippedQueueRender(chars, start, end, 0,0, maxWidth, charClipping);
        renderQueuedLines(chars, x, y);
        return res;
    }

    protected int textLineClippedQueueRender(char[] chars, int start, int end, float x, float y, float maxWidth, boolean charClipping) {
        if (this.textAlignY == 3) {
            y += this.textAscent() / 2.0F;
        } else if (this.textAlignY == 101) {
            y += this.textAscent();
        } else if (this.textAlignY == 102) {
            y -= this.textDescent();
        }

        int eol = Util.findIndexOfNext(chars, start, '\n');
        // if we have an eol char, pretend we're shorter
        // do this early so no unnecessary copy
        if(eol!=-1)
            end = Util.min(eol,end);

        float renderedTextWidth = 0;
        int renderedTextEnd = start;

        boolean saveBold = getHyperFont().bold;
        boolean saveItalic = getHyperFont().italic;


        // figure out safe width
        int nextSpace = Util.findIndexOfNext(chars, start, ' ');
        if (nextSpace == -1 || nextSpace>end)
            nextSpace = end;
        if (charClipping||textWidthImpl(chars, start, nextSpace)>maxWidth) {
            // first word is too long
            int endIdx = start;
            float totalWidth = 0;
            float lastTotalWidth = 0;
            while (totalWidth < maxWidth && endIdx<end){
                lastTotalWidth = totalWidth;
                totalWidth += textWidthUnsafe(chars, endIdx, endIdx+1);
                endIdx++;
            }
            renderedTextWidth = lastTotalWidth;
            renderedTextEnd = totalWidth>maxWidth?endIdx-1:endIdx;
        } else {
            // rendering words
            int endIdx = start;
            int lastIdx = start;
            float totalWidth = 0;
            float lastTotalWidth = 0;

            while (totalWidth < maxWidth && endIdx<end){
                lastTotalWidth = totalWidth;
                lastIdx = endIdx;

                nextSpace = Util.findIndexOfNext(chars, lastIdx+1, ' ');

                if (nextSpace == -1 || nextSpace>end)
                    nextSpace = end;

                totalWidth += textWidthUnsafe(chars, endIdx, nextSpace);
                endIdx=nextSpace;
            }
            if(endIdx == end && totalWidth < maxWidth) {
                renderedTextWidth = totalWidth;
                renderedTextEnd = endIdx;
            } else {
                renderedTextWidth = lastTotalWidth;
                renderedTextEnd = lastIdx;
            }

        }

        getHyperFont().bold = saveBold;
        getHyperFont().italic = saveItalic;

        // actually render

        if (this.textAlign == CENTER) {
            x -= renderedTextWidth / 2.0f;
        } else if (this.textAlign == RIGHT) {
            x -= renderedTextWidth;
        }

        linesToRender.add(new DeferredLineRender(start, renderedTextEnd, x, y));
        return renderedTextEnd;
    }

    protected void renderQueuedLines(char[] chars, float x, float y) {
        while(!linesToRender.isEmpty()) {
            DeferredLineRender lineRender = linesToRender.poll();
            textLineImpl(chars, lineRender.start, lineRender.end, x+lineRender.dx, y+lineRender.dy);
        }
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
        boolean saveBold = getHyperFont().bold;
        boolean saveItalic = getHyperFont().italic;
        float wide = textWidthUnsafe(buffer, start, stop);
        getHyperFont().bold = saveBold;
        getHyperFont().italic = saveItalic;
        return wide;
    }

    protected float textWidthUnsafe(char[] buffer, int start, int stop) {
        // TODO optimization for stop-start==1 case
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
