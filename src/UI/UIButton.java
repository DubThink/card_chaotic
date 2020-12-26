package UI;

import Globals.Style;
import core.AdvancedApplet;
import processing.core.PConstants;

public class UIButton extends UIBase {
    String text;
    public Action onAction;
    public Action offAction;
    boolean toggle;
    boolean state;

    public UIButton(int x, int y, int w, int h, String text, Action action) {
        super(x, y, w, h);
        this.onAction = action;
        this.text = text;
    }

    public UIButton(int x, int y, int w, int h, String text, Action onAction, Action offAction, boolean toggle) {
        this(x,y,w,h,text,onAction,offAction,toggle, false);
    }

    public UIButton(int x, int y, int w, int h, String text, Action onAction, Action offAction, boolean toggle, boolean state) {
        super(x, y, w, h);
        this.text = text;
        this.onAction = onAction;
        this.offAction = offAction;
        this.toggle = toggle;
        this.state = state;
    }

    @Override
    protected void _looseFocus() {
        if(!toggle) {
            state = false;
            if(offAction!=null)offAction.action();
        }
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        if(state)
            p.fill(Style.fillColorActive);
        else
            p.fill(Style.fillColor);
        p.stroke(focus?Style.borderColorHover:Style.borderColor);
        p.rect(cx, cy, cw, ch,Style.borderRadius);
        p.fill(focus?Style.textColorHover:Style.textColor);

        Style.chooseFont(fontFamily, ch).apply(p);
        p.textAlign(PConstants.CENTER, PConstants.CENTER);
//        p.text(text, cx, cy);
        p.text(text, cx+ cw /2f, cy+ ch /2f);
    }

    @Override
    protected boolean _handleMouseInput(boolean down, int button, int x, int y) {
        if(!isPointOver(x,y))
            return false;
        if(toggle){
            if(down) {
                state = !state;
                System.out.println("eee state: "+state);
            }
        } else {
            state=down;
        }

        if(state){
            if(onAction!=null)onAction.action();
        } else {
            if(offAction!=null)offAction.action();
        }
        return true;
    }
}
