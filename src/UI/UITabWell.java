package UI;

import Globals.Style;
import core.AdvancedApplet;

import java.util.ArrayList;

public class UITabWell extends UIBase {

    protected ArrayList<UIPanel> tabs;
    protected int nextButtonPos;
    protected final static int BUTTON_WIDTH = 120;

    public UITabWell(int x, int y, int w, int h) {
        super(x, y, w, h);
        tabs = new ArrayList<>();
    }

    public UITabWell(int x, int y, int w, int h, UILayer layer) {
        super(x, y, w, h, layer);
        tabs = new ArrayList<>();
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        p.stroke(Style.borderColor);
        p.fill(Style.fillColorPanel);
        p.rect(cx, cy, cw, ch, Style.borderRadius);
    }

    public UIPanel addTab(String name){
        UIPanel p = addChild(new UIPanel(0,30,0,0));
        UIButton b = addChild(new UIButton(nextButtonPos, 0, BUTTON_WIDTH, 30, name, () -> {hideAll();p.setEnabled(true);}));
        if(!tabs.isEmpty())
            p.setEnabled(false); // hide all but the first tab
        tabs.add(p);
        nextButtonPos+=BUTTON_WIDTH;
        return p;
    }

    protected void hideAll(){
        for (UIPanel tab : tabs) {
            tab.setEnabled(false);
        }
    }
}
