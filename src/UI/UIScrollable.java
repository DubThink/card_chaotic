package UI;

import Globals.Style;
import bpw.Util;
import core.AdvancedApplet;

public abstract class UIScrollable extends UIBase {
    private int position;
    public UIScrollable(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public UIScrollable(int x, int y, int w, int h, UILayer layer) {
        super(x, y, w, h, layer);
    }

    protected void renderScrollable(AdvancedApplet p){
        if(getScreenCapacity()>=getScrollableLineCount())
            return;

        float s = ch/(float)getScrollableLineCount();
        p.fill(Style.scrollBarColor);
        p.noStroke();
        p.rect(cx+cw- Style.scrollBarWidth,cy+position*s, Style.scrollBarWidth, getScreenCapacity()*s);

        if(position<getMaxScrollPos()) {
            p.ellipse(cx + cw / 2, cy + ch - 10, 5, 5);
            p.ellipse(cx + cw / 2 + 10, cy + ch - 10, 5, 5);
            p.ellipse(cx + cw / 2 - 10, cy + ch - 10, 5, 5);
        }
    }

    protected int getUnscrollableLineCount() {return 0;}

    protected abstract int getScrollableLineCount();

    protected abstract int getScrollableLineHeight();

    @Override
    protected boolean _handleMouseWheel(int ct, int x, int y) {
        if(!isPointOver(x,y))
            return false;
        position+=ct;
        refreshScrollPos();
        return true;
    }

    protected void refreshScrollPos(){
        if(position<0){
            position = 0;
            return;
        }
        // calc max scroll pos
        // how many lines/screen
        position = Util.min(getMaxScrollPos(), position);

    }

    protected int getMaxScrollPos(){
        return getScrollableLineCount()-getScreenCapacity();
    }

    protected int getScreenCapacity(){
        return  (int)Math.floor(ch/(float)getScrollableLineHeight())-getUnscrollableLineCount();
    }

    public int getScrollPosition(){
        return position;
    }
}
