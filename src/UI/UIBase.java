package UI;

import bpw.Util;
import processing.core.PApplet;
import Globals.DebugConstants;

import java.util.ArrayList;

public class UIBase {
    int x, y, w, h;
    boolean focus;
    ArrayList<UIBase> children;

    public UIBase(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        focus=false;
        children=new ArrayList<>();
    }

    public boolean updateFocus(int mouseX, int mouseY){
        focus=false;
        // if a child element is in focus
        for(UIBase element: children){
            focus = element.updateFocus(mouseX,mouseY);
            if(focus)
                return true;
        }
        // overloadable test for hover
        focus = isPointOver(mouseX, mouseY);
        return focus;
    }

    boolean isPointOver(int px, int py){
        return Util.in(px, x, x+w) && Util.in(py, y, y+h);
    }

    public void render(PApplet p){
        for(UIBase element: children){
            element.render(p);
        }
        p.pushStyle();
        draw(p);
        p.popStyle();
        if(DebugConstants.renderUIDebug) {
            p.pushStyle();
            debugDraw(p);
            p.popStyle();
        }
    }

    protected void draw(PApplet p){
        // style will always be noStroke, noFill, line weight 1
    }

    protected void debugDraw(PApplet p){
        // style will always be noStroke, noFill, line weight 1
        if(focus)
            p.stroke(0,255,0);
        else
            p.stroke(255,0,0);
        p.rect(x,y,w,h);
    }

    public boolean handleMouseInput(boolean down, int button, int x, int y) {
        return false;
    }
}
