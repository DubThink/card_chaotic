package Debug;

import Globals.Style;
import core.AdvancedGraphics;

import static Globals.GlobalEnvironment.modifierShift;

public class PerfView extends DebugPanel {
    public float lastCardRenderMS;
    public float cardBackRenderMS;
    public SpikeGraph drawTimeGraph;
    public SpikeGraph cardRendersGraph;
    public SpikeGraph imageTXRXGraph;
    private float timeSinceLastRefresh;

    public PerfView(){
        drawTimeGraph = new SpikeGraph();
        cardRendersGraph = new SpikeGraph(true);
        imageTXRXGraph = new SpikeGraph(true);
    }

    @Override
    protected void _render(AdvancedGraphics p){
        p.fill(0,200);
        p.noStroke();
        p.rect(0,0, 400,700);
        p.fill(255);
        Style.getFont(Style.F_CODE, Style.FONT_12).apply(p);
        printMS(p, 0, "last full card bake", lastCardRenderMS);
        printMS(p, 1, "card back render", cardBackRenderMS);
        print(p, 20, "draw() ms");
        print(p, 19, "shift"+modifierShift);
        drawTimeGraph.render(p,10, ly(21));

        print(p, 27, "Card render total ms");
        cardRendersGraph.render(p,10, ly(28));
        print(p, 35, "Image TX/RX total ms");
        imageTXRXGraph.render(p,10, ly(36));
    }

    public void nextFrame(float dt){
        drawTimeGraph.advanceFrame();
        cardRendersGraph.advanceFrame();
        imageTXRXGraph.advanceFrame();

        timeSinceLastRefresh+=dt;
        if(timeSinceLastRefresh>1000){
            timeSinceLastRefresh-=1000;
            drawTimeGraph.reMinMax();
            cardRendersGraph.reMinMax();
            imageTXRXGraph.reMinMax();
        }
    }
}
