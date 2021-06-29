import Gamestate.CardDefinition;
import Globals.Config;
import Globals.Debug;
import Globals.Style;
import Render.AutomataCardBackRenderer;
import Render.CardBackRenderer;
import Render.ExampleCardBackRenderer;
import Render.TracersCardBackRenderer;
import Schema.AsyncIOHandler;
import Schema.SchemaEditDefinition;
import UI.*;
import core.*;
import processing.core.PApplet;
import processing.event.MouseEvent;

import static Globals.GlobalEnvironment.*;
import static com.jogamp.newt.event.KeyEvent.*; // use this for p2d-based graphics (not KeyEvent)


public abstract class GameBase extends AdvancedApplet {

    public void settings() {
        smooth(8);
    }

    int lastMillis = 0;

    @Override
    public void setup() {
        DEV_MODE = checkArg("dev");

        asyncIOHandler = new AsyncIOHandler();
        asyncIOHandler.start();

        UIBase.app = this;
        imageLoader = new ImageLoader(this);
        Style.loadFonts(this);

        loadAndCreateSymbol("cayde32.png", "Y");
        loadAndCreateSymbol("earth.png", "E");
        loadAndCreateSymbol("air.png", "A");
        loadAndCreateSymbol("fire.png", "F");
        loadAndCreateSymbol("water.png", "W");
        loadAndCreateSymbol("cool.png", "C");
        loadAndCreateSymbol("love.png", "L");
        loadAndCreateSymbol("power.png", "P");
        loadAndCreateSymbol("death.png", "D");
        loadAndCreateSymbol("folder.png", "{");
        loadAndCreateSymbol("check.png", "[");
        loadAndCreateSymbol("warn.png", "!");
        loadAndCreateSymbol("x.png", "]");

        uiRoot = new UIBase(0, 0, width, height);

        openSchemaHandler = (schema, readonly, openSchema1, x, y) -> {
            if(schema==null)return;
            System.out.println("x="+x+" y="+y);
            new SchemaEditDefinition(schema,
                    uiRoot.addChild(new UIWindowPanel(x,y,600,900,schema.toString()),UILayer.POPUP).getInnerPanel(),
                    readonly,
                    openSchema1);
        };

        CardBackRenderer cardBackRenderer = new ExampleCardBackRenderer();
        cardBackRenderer.initialize(this);
        CardDefinition.setCardBackRenderer(cardBackRenderer);
    }

    protected void finalizeSetup() {
        lastMillis = millis();

    }

    /* called before the ui is rendered */
    public abstract void _update(int dt);
    /* called after the ui is rendered */
    public void _draw(int dt){}

    public void draw() {
        background(Style.fillColorPanel);
        int dt = millis() - lastMillis;
        lastMillis = millis();
        float drawStartTime = Debug.perfTimeMS();
        CardDefinition.updateCardBack(this,dt);

        _update(dt);

        noFill();
        noStroke();
        strokeWeight(1);
        if(width != uiRoot.getWidth() || height != uiRoot.getHeight()){
            uiRoot.setSize(width, height);
        }

        uiRoot.updateFocus(mouseX, mouseY);
        uiRoot.updateLogic(dt);
        textAlign(LEFT);
        uiRoot.render(this);
        uiRoot.deferredRender(this);
        UITooltip.renderTooltips(this);


        _draw(dt);

        pushStyle();
        Style.getFont(Style.F_CODE, Style.FONT_12).apply(this);
        textAlign(RIGHT,TOP);
        fill(Style.textColor);
        if(DEV_MODE)
            text("DEV MODE Shift+F12 to exit. ver="+ Config.GAME_VERSION+" net="+Config.NET_VERSION, width-3, 3);
        else
            text("Shift+F12 to exit. ver="+ Config.GAME_VERSION+" net="+Config.NET_VERSION,width-3,3);
        popStyle();

        if(Debug.renderUIDebug){
            stroke(127);
            line(0,mouseY,width,mouseY);
            line(mouseX,0,mouseX,height);
        }

        Debug.perfView.drawTimeGraph.addVal(Debug.perfTimeMS()-drawStartTime);
        Debug.perfView.nextFrame(dt);
        Debug.stateDebug.nextFrame(dt);
        if(Debug.renderPerfView)
            Debug.perfView.render(this.getAdvGraphics());
        if(Debug.renderStateDebug)
            Debug.stateDebug.render(this.getAdvGraphics());
    }


    @Override
    public void keyPressed() {
        if(key == ESC)
            key=0;
        if(keyCode == VK_F12 && modifierShift)
            exit();
        super.keyPressed();
        if(keyCode == CONTROL)
            modifierCtrl = true;
        if(keyCode == SHIFT)
            modifierShift = true;

        if (keyCode == VK_F3)
            Debug.renderUIDebug = !Debug.renderUIDebug;
        if (keyCode == VK_F7)
            Debug.renderPerfView = !Debug.renderPerfView;
        if (keyCode == VK_F8)
            Debug.renderStateDebug = !Debug.renderStateDebug;

//        else if (keyCode == VK_F4)
//            DebugConstants.printUIDebug = !DebugConstants.renderUIDebug;
        if (keyCode == VK_F12)
            Debug.debugBreakpoint = !Debug.debugBreakpoint;

        if(keyCode == VK_F11){
            // toggle dev
            if(DEV_MODE)
                DEV_MODE=false;
            else
                DEV_MODE=checkArg("dev");
        }

        uiRoot.handleKeyPress(true, key, keyCode);
    }

    @Override
    public void keyReleased() {
        super.keyReleased();
        if(keyCode == CONTROL)
            modifierCtrl = false;
        if(keyCode == SHIFT)
            modifierShift = false;
        uiRoot.handleKeyPress(false, key, keyCode);
    }

    @Override
    public void mousePressed() {
        uiRoot.handleMouseInput(true, mouseButton, mouseX, mouseY);
    }

    @Override
    public void mouseReleased() {
        MouseInputHandler.mouseReleased(mouseButton);
        uiRoot.handleMouseInput(false, mouseButton, mouseX, mouseY);
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        int ct = event.getCount();
        uiRoot.handleMouseWheel(ct, mouseX, mouseY);
    }

    public static void run(String className, String... args) {
        // dumb hack
        // for some reason, PApplet.main concats the args after the sketch class name
        // even though any args after the sketch name are ignored
        // ...
        if(args.length>0) {
            String[] newargs = new String[args.length];
            int newargidx=0;

            String name_tm=null;

            for (String arg : args) {
                if (arg.startsWith("--"))
                    if(name_tm==null)
                        name_tm=arg;
                    else
                        newargs[newargidx++] = arg;
            }

            if(name_tm==null)
                name_tm=className;
            else
                newargs[newargidx++] = className;

            for (String arg : args) {
                if (!arg.startsWith("--"))
                    newargs[newargidx++] = arg;
            }


            PApplet.main(name_tm, newargs);
        } else {
            PApplet.main(className);
        }
    }

    public String getArgParameter(String key, String defaultVal){
        if(args==null)
            return defaultVal;
        key+="=";
        for (String arg : args) {
            if (arg.startsWith(key))
                return arg.substring(key.length());
        }
        return defaultVal;
    }

    public boolean checkArg(String key){
        if(args==null)
            return false;
        for (String arg : args) {
            if (arg.equals(key))
                return true;
        }
        return false;
    }
}