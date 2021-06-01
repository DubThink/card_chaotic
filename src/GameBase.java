import Gamestate.CardDefinition;
import Globals.Config;
import Globals.Debug;
import Globals.GlobalEnvironment;
import Globals.Style;
import UI.*;
import core.*;
import processing.core.PApplet;

import static Globals.GlobalEnvironment.*;
import static com.jogamp.newt.event.KeyEvent.*; // use this for p2d-based graphics (not KeyEvent)


public abstract class GameBase extends AdvancedApplet {

    public void settings() {
        smooth(8);
    }

    int lastMillis = 0;

    @Override
    public void setup() {
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

    }

    protected void finalizeSetup() {
        lastMillis = millis();

    }


    public abstract void _draw(int dt);

    public void draw() {
        background(Style.fillColorPanel);
        int dt = millis() - lastMillis;
        lastMillis = millis();
        float drawStartTime = Debug.perfTimeMS();
        CardDefinition.updateCardBack(this,dt);

        _draw(dt);

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

        pushStyle();
        Style.getFont(Style.F_CODE, Style.FONT_12).apply(this);
        textAlign(RIGHT,TOP);
        fill(Style.textColor);
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
        else if (keyCode == VK_F12)
            Debug.debugBreakpoint = !Debug.debugBreakpoint;
        else uiRoot.handleKeyPress(true, key, keyCode);
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
        uiRoot.handleMouseInput(false, mouseButton, mouseX, mouseY);
    }

    public static void run(String className, String... args) {
        // dumb hack
        // for some reason, PApplet.main concats the args after the sketch class name
        // even though any args after the sketch name are ignored
        // ...
        if(args.length>0) {
            String name_tm = args[0];
            for (int i = 0;i<args.length-1;i++){
                args[i]=args[i+1];
            }
            args[args.length-1]= className;
            PApplet.main(name_tm,args);
        } else {
            PApplet.main(className);
        }
    }
}