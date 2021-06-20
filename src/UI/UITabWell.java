package UI;

import Globals.Style;
import core.AdvancedApplet;

import java.util.ArrayList;

public class UITabWell extends UIBase {

    protected ArrayList<UIPanel> tabs;
    protected ArrayList<UIButton> buttons;
    protected int nextButtonPos;
    protected final static int BUTTON_WIDTH = 120;

    public UITabWell(int x, int y, int w, int h) {
        super(x, y, w, h);
        tabs = new ArrayList<>();
        buttons = new ArrayList<>();
    }

    public UITabWell(int x, int y, int w, int h, UILayer layer) {
        super(x, y, w, h, layer);
        tabs = new ArrayList<>();
        buttons = new ArrayList<>();
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        p.stroke(Style.borderColor);
        p.fill(Style.fillColorPanel);
        p.rect(cx, cy, cw, ch, Style.borderRadius);
    }

    public UIPanel addTab(String name){
        UIPanel p = addChild(new UIPanel(0,30,0,0));
        UIButton b = addChild(new UIButton(nextButtonPos, 0, BUTTON_WIDTH, 30, name,
                () -> hideAllExcept(p), null, true));

        if(!tabs.isEmpty())
            p.setEnabled(false); // hide all but the first tab
        else
            b.state=true; // show the first tab
        tabs.add(p);
        buttons.add(b);
        nextButtonPos+=BUTTON_WIDTH;
        return p;
    }

    protected void hideAllExcept(UIPanel panel){
         for(int i=0;i<tabs.size();i++){
             if(tabs.get(i)==panel) {
                 tabs.get(i).setEnabled(true);
                 buttons.get(i).state = true;
             } else {
                 tabs.get(i).setEnabled(false);
                 buttons.get(i).state = false;
             }
         }
    }
}
