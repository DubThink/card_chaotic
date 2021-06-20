package UI;

import Globals.Style;
import aew.Util;
import core.AdvancedApplet;
import core.AdvancedGraphics;

import java.util.ArrayList;

public class UILogView extends UIBase {
    ArrayList<String> lines;

    public UILogView(int x, int y, int w, int h){
        super(x, y, w, h);
        lines = new ArrayList<>();
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        AdvancedGraphics g = p.getAdvGraphics();
        g.pushMatrix();

        p.stroke(Style.borderColor);
        p.fill(Style.fillColorInputField);
        p.rect(cx, cy, cw, ch, Style.borderRadius);

        g.translate(cx+Style.textMargin,cy+Style.textMargin);
        p.fill(Style.textColor);
        p.noStroke();
        //Style.getFont(fontFamily, Style.FONT_SMALL).apply(p);
        Style.getFont(Style.F_CODE, Style.FONT_12).apply(p);
        int maxLines = Util.min((int)Math.floor(ch/g.textLeading),lines.size());

        for(int i=0;i<maxLines;i++){

            //p.textAlign(PConstants.LEFT, PConstants.TOP);
            int idx = lines.size()-maxLines+i;
            p.text(lines.get(idx),0,p.textAscent()+(i)*g.textLeading);

        }
        g.popMatrix();
    }

    public void addLine(String line){
        lines.add(line);
    }

}
