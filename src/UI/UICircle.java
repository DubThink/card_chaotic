package UI;

import aew.Util;
import core.AdvancedApplet;

public class UICircle extends  UIBase {
    int ccx, ccy;
    int cr = 0;

    public UICircle(int x, int y, int w, UILayer layer) {
        super(x, y, w, w, layer);
        cr = w/2;
        ccx = x + cr;
        ccy = y + cr;
    }

    public UICircle(int x, int y, int w) {
        super(x, y, w,w);
        cr = w/2;
        ccx = x + cr;
        ccy = y + cr;
    }

    @Override
    protected void _updateCalculatedLayout() {
        super._updateCalculatedLayout();
        cr = Util.min(cw/2,ch/2);
        ccx = cx + cr;
        ccy = cy + cr;
    }

    @Override
    public boolean isPointOver(int px, int py) {
        return Util.dist2(px, py, ccx, ccy)<=cr*cr;
    }

    @Override
    protected void _debugDraw(AdvancedApplet p) {
        if (focus)
            p.stroke(0, 255, 0);
        else
            p.stroke(255, 0, 0);
        p.ellipse(ccx, ccy, cr*2, cr*2);
    }
}
