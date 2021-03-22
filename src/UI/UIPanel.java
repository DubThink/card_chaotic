package UI;

import Globals.Style;
import core.AdvancedApplet;
import processing.core.PConstants;

public class UIPanel extends UIBase {


    public UIPanel(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public UIPanel(int x, int y, int w, int h, UILayer layer) {
        super(x, y, w, h, layer);
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        p.stroke(Style.borderColor);
        p.fill(Style.fillColorPanel);
        p.rect(cx, cy, cw, ch, Style.borderRadius);
    }
}
