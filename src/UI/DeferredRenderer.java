package UI;

import core.AdvancedApplet;
import processing.core.PStyle;

import java.util.ArrayDeque;
import java.util.Queue;

public class DeferredRenderer {
    Queue<DeferredRenderCall> renderCalls;

    public DeferredRenderer() {
        renderCalls = new ArrayDeque<>();
    }

    public void renderAllDeferred(AdvancedApplet p){
        p.pushStyle();
        while (!renderCalls.isEmpty())
            renderCalls.poll().render(p);
        p.popStyle();
    }

    private abstract static class DeferredRenderCall {
        PStyle style;

        DeferredRenderCall(AdvancedApplet p){
            style = p.getGraphics().getStyle();
        }

        abstract void render(AdvancedApplet p);
    }

    public void text(AdvancedApplet p, String s, float x, float y) {
        renderCalls.add(new DeferredTextCall(p, s, x, y));
    }

    private static class DeferredTextCall extends DeferredRenderCall{
        String text;
        float x;
        float y;

        public DeferredTextCall(AdvancedApplet p, String text, float x, float y) {
            super(p);
            this.text = text;
            this.x = x;
            this.y = y;
        }

        @Override
        void render(AdvancedApplet p) {
            p.style(style);
            p.text(text,x,y);
        }
    }

}
