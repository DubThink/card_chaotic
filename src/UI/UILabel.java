package UI;

import Globals.Style;
import core.AdvancedApplet;
import processing.core.PConstants;

public class UILabel extends UIBase {
    String text;
    boolean opaque;
    int justify = PConstants.LEFT;
    public boolean bigLabel = false;

    public UILabel(int x, int y, int w, int h, String text) {
        this(x, y, w, h, text, false);
    }

    public UILabel(int x, int y, int w, int h, String text, boolean opaque) {
        super(x, y, w, h);
        this.text = text;
        //interactable = false;
        this.opaque = opaque;
    }


    @Override
    protected void _draw(AdvancedApplet p) {
        if(opaque) {
            p.stroke(Style.borderColor);
            p.fill(Style.fillColor);
            p.rect(cx, cy, cw, ch, Style.borderRadius);
        }
        p.fill(Style.textColor);
        p.noStroke();
        if(bigLabel)
            Style.chooseFont(fontFamily, ch).apply(p);
        else
            Style.getFont(fontFamily,Style.FONT_SMALL).apply(p);
        if(bigLabel)
            p.textAlign(justify, PConstants.CENTER);
        else
            p.textAlign(justify);
        float xAlign = 0;
        if(justify==PConstants.CENTER)
            xAlign = (cw /2f);
        else if(justify==PConstants.RIGHT){
            xAlign = cw;
        }
        p.text(text, cx+xAlign, cy + (bigLabel?ch /2f : p.textAscent()));
    }

    public UILabel setJustify(int justify) {
        this.justify = justify;
        return this;
    }

    public UILabel setText(String text) {
        this.text = text;
        return this;
    }

    public UILabel setBigLabel(boolean bl) {
        this.bigLabel = bl;
        return this;
    }
}
