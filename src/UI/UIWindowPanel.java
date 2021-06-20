package UI;

import Globals.Style;
import UI.UIPanel;
import bpw.Util;
import core.AdvancedApplet;
import core.MouseInputHandler;
import processing.core.PConstants;

public class UIWindowPanel extends UIBase {
    protected String title;
    protected UIPanel innerPanel;
    protected UIButton minMaxButton;

    protected int fullH;

    protected boolean grabbed;
    int lastMouseX, lastMouseY;

    public UIWindowPanel(int x, int y, int w, int h, String title) {
        this(x, y, w, h, title, UILayer.POPUP);
    }

    public UIWindowPanel(int x, int y, int w, int h, String title, UILayer layer) {
        super(x, y, w, h, layer);
        this.title = title;
        addChild(new UIButton(-26,4,22,22,"X",this::actionClose));
        minMaxButton = addChild(new UIButton(-26-30,4,22,22,"_",this::actionMinimize));
        innerPanel = addChild(new UIPanel(0,30,0,0));
        fullH=h;
    }


    public void actionClose(){
        parent.removeChild(this);
    }

    public void actionMinimize(){
        System.out.println("minimize");
        innerPanel.setEnabled(false);
        minMaxButton.text = "V";
        minMaxButton.onAction = this::actionMaximize;
        h=30;
        _updateCalculatedLayout();
    }

    public void actionMaximize(){
        System.out.println("max");

        innerPanel.setEnabled(true);
        minMaxButton.text = "_";
        minMaxButton.onAction = this::actionMinimize;
        h=fullH;
        _updateCalculatedLayout();
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        p.stroke(Style.borderColor);
        p.fill(Style.fillColorPanel);
        p.rect(cx,cy,cw,30);
        p.fill(Style.textColor);
        p.noStroke();
        p.textAlign(PConstants.CENTER,PConstants.CENTER);
        Style.getFont(Style.F_STANDARD,Style.FONT_14).apply(p);
        p.getAdvGraphics().textLineClipped(title,cx+cw/2-30,cy+15,cw-70,true);
    }

    public UIPanel getInnerPanel() {
        return innerPanel;
    }

    public boolean isPointOverTitle(int px, int py) {
        return Util.in(px, cx, cx + cw) && Util.in(py, cy, cy + 30);
    }

    void mouseUp(){
        grabbed = false;
    }

    @Override
    public boolean _handleMouseInput(boolean down, int button, int x, int y) {
        if(down && isPointOverTitle(x,y) && !grabbed){
            MouseInputHandler.registerMouseUse(button,this::mouseUp);
            grabbed=true;
            lastMouseX=x;
            lastMouseY=y;
            return true;
        } else return isPointOverTitle(x,y);
    }

    @Override
    protected void _logicStep(int dt) {
        if(!grabbed)return;
        int dx = app.mouseX-lastMouseX;
        int dy = app.mouseY-lastMouseY;
        this.x+=dx;
        this.y+=dy;
        lastMouseX = app.mouseX;
        lastMouseY = app.mouseY;
        _updateCalculatedLayout();
    }
}
