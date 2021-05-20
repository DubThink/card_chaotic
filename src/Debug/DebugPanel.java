package Debug;

import Globals.Style;
import core.AdvancedGraphics;

public abstract class DebugPanel {
    public void render(AdvancedGraphics p){
        p.fill(0,200);
        p.noStroke();
        p.rect(0,0, 400,700);
        p.fill(255);
        Style.getFont(Style.F_CODE, Style.FONT_12).apply(p);
        _render(p);
    }

    protected abstract void _render(AdvancedGraphics p);

    protected static void printMS(AdvancedGraphics p, int pos, String label, float ms){
        print(p,pos,String.format("%s: %.3fms",label, ms));
    }

    protected static void print(AdvancedGraphics p, int pos, String s){
        p.text(s,10,ly(pos)+8);
    }

    protected static float ly(int pos){
        return 15+15*pos;
    }

    public void nextFrame(float dt){}
}
