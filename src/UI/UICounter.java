package UI;

import core.AdvancedApplet;

public class UICounter extends UICircle {
    public int value = 0;

    public UICounter(int x, int y, int w, int h, UILayer layer) {
        super(x, y, w, h, layer);
    }

    public UICounter(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Override
    protected void _draw(AdvancedApplet p) {

    }
}
