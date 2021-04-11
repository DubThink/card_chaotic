package Debug;

import Globals.Style;
import core.AdvancedGraphics;

import static Globals.GlobalEnvironment.modifierShift;

public class PerfView {
    public float lastCardRenderMS;
    public SpikeGraph drawTimeGraph;
    public SpikeGraph cardRendersGraph;
    private float timeSinceLastRefresh;

    public PerfView(){
        drawTimeGraph = new SpikeGraph();
        cardRendersGraph = new SpikeGraph(true);
    }

    public void render(AdvancedGraphics p){
        p.fill(0,200);
        p.noStroke();
        p.rect(0,0, 400,700);
        p.fill(255);
        Style.getFont(Style.F_CODE, Style.FONT_12).apply(p);
        printMS(p, 0, "last full card bake", lastCardRenderMS);
        print(p, 20, "draw() ms");
        print(p, 19, "shift"+modifierShift);
        drawTimeGraph.render(p,10, ly(21));

        print(p, 27, "Card render total ms");
        cardRendersGraph.render(p,10, ly(28));
    }

    private static void printMS(AdvancedGraphics p, int pos, String label, float ms){
        print(p,pos,String.format("%s: %.3fms",label, ms));
    }

    private static void print(AdvancedGraphics p, int pos, String s){
        p.text(s,10,ly(pos)+8);
    }

    private static float ly(int pos){
        return 40 + 15*pos;
    }

    public void nextFrame(float dt){
        drawTimeGraph.advanceFrame();
        cardRendersGraph.advanceFrame();

        timeSinceLastRefresh+=dt;
        if(timeSinceLastRefresh>1000){
            timeSinceLastRefresh-=1000;
            drawTimeGraph.reMinMax();
            cardRendersGraph.reMinMax();
        }
    }
}
