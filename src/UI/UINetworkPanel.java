package UI;

import Globals.Style;
import core.AdvancedApplet;

public class UINetworkPanel extends UIBase {


    public UINetworkPanel(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public UINetworkPanel(int x, int y, int w, int h, UILayer layer) {
        super(x, y, w, h, layer);
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        p.stroke(Style.borderColor);
        p.fill(Style.fillColorPanel);
        p.rect(cx, cy, cw, ch, Style.borderRadius);
    }
}
