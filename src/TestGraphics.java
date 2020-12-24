import core.HyperFont;
import processing.core.PFont;
import processing.opengl.PGraphics2D;

import java.awt.*;
import java.io.InputStream;

public class TestGraphics extends PGraphics2D {
    @Override
    public void text(String str, float x, float y) {
        super.text(str, x, y);
    }
    @Override
    protected void textCharImpl(char ch, float x, float y) {
//        if(ch=='s')ch='S';
        super.textCharImpl(ch,x,y);
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
