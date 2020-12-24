package UI;

import core.AdvancedApplet;
import processing.core.PApplet;

public class UIButton extends UIBase {
    public Action onAction;
    public Action offAction;
    boolean toggle;
    boolean state;
    String text;

    public UIButton(int x, int y, int w, int h, String text, Action action) {
        super(x, y, w, h);
        this.onAction = action;
        this.text = text;
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
        p.fill(state?Style.fillColorHover:Style.fillColor);
        p.stroke(focus?Style.borderColorHover:Style.borderColor);
        p.rect(cx, cy,w,h,Style.borderRadius);
        p.fill(focus?Style.borderColorHover:Style.borderColor);
        Style.font32.apply(p);
        p.text(text, cx, cy);
    }

    @Override
    protected boolean _handleMouseInput(boolean down, int button, int x, int y) {
        if(!isPointOver(x,y))
            return false;
        if(toggle){
            state = !state;
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
