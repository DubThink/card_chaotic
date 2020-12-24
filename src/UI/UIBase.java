package UI;

import bpw.Util;
import core.AdvancedApplet;
import processing.core.PApplet;
import Globals.DebugConstants;

import java.util.ArrayList;

public class UIBase {
    int cx, cy;
    int x, y; // relative (to parent)
    int w, h;
    boolean focus;
    ArrayList<UIBase> children;
    UIBase parent;

    public UIBase(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.cx = x;
        this.cy = y;
        this.w = w;
        this.h = h;
        focus=false;
        children=new ArrayList<>();
        setParent(null);
    }

    private void setParent(UIBase base){
        parent = base;
        _updateCalculatedPosition();
    }

    public boolean updateFocus(int mouseX, int mouseY){
        boolean wasFocus = focus;
        focus=false;
        // if a child element is in focus
        for(UIBase element: children){
            focus |= element.updateFocus(mouseX,mouseY);
        }
        // overloadable test for hover
        if(!focus)
            focus = isPointOver(mouseX, mouseY);
        if(wasFocus && !focus)
            _looseFocus();
        if(!wasFocus && focus)
            _gainFocus();
        return focus;
    }

    protected void _looseFocus(){}
    protected void _gainFocus(){}

    protected void _updateCalculatedPosition(){
        if(parent == null){
            cx = x;
            cy = y;
        } else {
            cx = parent.cx + x;
            cy = parent.cy + y;
        }
        for(UIBase element: children){
            element._updateCalculatedPosition();
        }
    }

    public boolean isPointOver(int px, int py){
        return Util.in(px, cx, cx +w) && Util.in(py, cy, cy +h);
    }

    public void render(AdvancedApplet p){
        for(UIBase element: children){
            element.render(p);
        }
        p.pushStyle();
        _draw(p);
        p.popStyle();
        if(DebugConstants.renderUIDebug) {
            p.pushStyle();
            _debugDraw(p);
            p.popStyle();
        }
    }

    protected void _draw(AdvancedApplet p){
        // style will always be noStroke, noFill, line weight 1
    }

    protected void _debugDraw(AdvancedApplet p){
        // style will always be noStroke, noFill, line weight 1
        if(focus)
            p.stroke(0,255,0,100);
        else
            p.stroke(255,0,0, 100);
        p.rect(cx, cy,w,h);
    }

    public void addChild(UIBase child){
        children.add(child);
        child.setParent(this);
    }

    public boolean handleMouseInput(boolean down, int button, int x, int y) {
        for(UIBase element: children){
            if(element.handleMouseInput(down, button, x, y))
                return true;
        }
        return _handleMouseInput(down, button, x, y);
    }

    protected boolean _handleMouseInput(boolean down, int button, int x, int y) {
        return false;
    }

    public int getCalculatedX() {
        return cx;
    }

    public int getCalculatedY() {
        return cy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    public void setPos(int x, int y){
        this.x=x;
        this.y=y;
        _updateCalculatedPosition();
    }
}
