package UI;

import Globals.Style;
import core.AdvancedApplet;
import processing.core.PConstants;

import java.util.ArrayList;

public class UITooltip extends UIBase {
    private String text;

    private int cMouseX, cMouseY;

    private static final int HEIGHT = 24;

    private boolean active;

    private static ArrayList<UITooltip> gTooltips;
    static {
        gTooltips = new ArrayList<>();
    }

    private static void registerTooltip(UITooltip tooltip){
        gTooltips.add(tooltip);
    }

    private static void unregisterTooltip(UITooltip tooltip){
        gTooltips.remove(tooltip);
    }

    public static void renderTooltips(AdvancedApplet a){
        for (UITooltip tooltip : gTooltips) {
            if (tooltip.active)
                tooltip.drawTooltip(a);
        }
    }

    public UITooltip(String text) {
        this(0, 0, 0, 0, text);
    }

    public UITooltip(int x, int y, int w, int h, String text) {
        super(x, y, w, h,UILayer.OVERLAY);
        this.text=text;
        registerTooltip(this);
    }

    @Override
    protected void cleanup() {
        unregisterTooltip(this);
    }

    protected void drawTooltip(AdvancedApplet p) {
        if(!active || !isPointOver(cMouseX,cMouseY))
            return;
        Style.getFont(Style.F_CODE, Style.FONT_12).apply(p);
        float textWidth = p.textWidth(text);
        int width = (int)textWidth+10;
        p.fill(Style.fillColor);
        p.stroke(Style.borderColor);

        int rx = cMouseX<width?cMouseX:cMouseX-width;
        int ry = cMouseY<HEIGHT?cMouseY:cMouseY-HEIGHT;

        p.rect(rx,ry,width,HEIGHT);

        p.fill(Style.textColor);

        p.textAlign(PConstants.LEFT,PConstants.CENTER);
        p.text(text,rx+5,ry+HEIGHT/2);
        active=false;
    }

    @Override
    protected void _debugDraw(AdvancedApplet p) {
        p.stroke(0, 255, 255, 127);
        p.rect(cx+3, cy+3, cw-6, ch-6);
        Style.getFont(Style.F_CODE, Style.FONT_12).apply(p);
        p.textAlign(PConstants.RIGHT,PConstants.BOTTOM);
        p.text("TT:"+text,cx+cw-2,cy+ch-2);
    }

    @Override
    public boolean updateFocus(int mouseX, int mouseY) {
        cMouseX = mouseX;
        cMouseY = mouseY;
        active = true;
        return false;
    }

    @Override
    protected boolean _handleMouseInput(boolean down, int button, int x, int y) {
        return false;
    }
}
