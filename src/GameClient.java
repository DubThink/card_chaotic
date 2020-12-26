import Globals.DebugConstants;
import Globals.Style;
import UI.*;
import core.AdvancedApplet;
import core.AdvancedGraphics;
import core.Symbol;
import core.SymbolInjector;
import processing.core.PApplet;
import processing.core.PImage;

import static com.jogamp.newt.event.KeyEvent.*; // use this for p2d-based graphics (not KeyEvent)
import static processing.core.PConstants.*;


public class GameClient extends AdvancedApplet {

    public void settings() {
        size(1600, 900, "core.AdvancedGraphics");
        smooth(4);
//        fullScreen(P3D,-2);
    }

    float ax, ay, az;
    int lastMillis = 0;
    UIBase root;

    void loadAndCreateSymbol(String file, String bind) {
        PImage ti = loadImage(file);
        Symbol cayde = SymbolInjector.createSymbol(ti);
        cayde.setMSize(28);
        SymbolInjector.addKey(bind, cayde);
        System.out.println("Binding '" + file + "' to char 0x%02x".formatted((int) cayde.c));
    }

    @Override
    public void setup() {
        UIBase.app = this;
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

        root = new UIBase(10, 10, width / 2, height / 2);
        UIBase testButtons = root.addChild(new UIBase(0, 0, 0, 0));
        Action test = new Action() {
            @Override
            public void action() {
                testButtons.setEnabled(false);
            }
        };
        testButtons.addChild(new UIButton(10, 10, 1500, 50, hyperText("Hello World. The 4 elements are /E/A/F/W. We also have /Ccool, /Llove, /Ppower, and /Ddeath. /Y and Cayde."), test));
        testButtons.addChild(new UIButton(10, 10 + 60 * 1, 1500, 50, hyperText("Earth time /E/E/E"), test)).setFontFamily(Style.F_IMPACT);
        testButtons.addChild(new UIButton(10, 10 + 60 * 2, 1500, 32, hyperText("/X //E \\/E"), test, null, true)).setFontFamily(Style.F_SCRIPT);

        testButtons.addChild(new UIButton(10, 10 + 60 * 4, 1500, 50, hyperText("Testi^^ng. /E/E/F: cast ^cool ability^ to ^gen^erate /P/P.\n The sunset ^looks\\^ nice."), test)).setFontFamily(Style.F_STANDARD);
        testButtons.addChild(new UIButton(10, 10 + 60 * 5, 1500, 50, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P.\n The sunset looks nice."), test)).setFontFamily(Style.F_FLAVOR);
        testButtons.addChild(new UIButton(10, 10 + 60 * 6, 1500, 50, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P.\n The sunset looks nice."), test)).setFontFamily(Style.F_HEAVY);
        testButtons.addChild(new UIButton(10, 10 + 60 * 7, 1500, 50, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P.\n The sunset looks nice."), test)).setFontFamily(Style.F_SCRIPT);
        testButtons.addChild(new UIButton(10, 10 + 60 * 8, 1500, 50, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P.\n The sunset looks nice."), test)).setFontFamily(Style.F_IMPACT);

        testButtons.addChild(new UIButton(10, 550 + 40 * 4, 1500, 30, hyperText("Testing. /E/E/F: cast cool ability to ge^nerate /P/P.\n The s^unset looks nice."), test)).setFontFamily(Style.F_STANDARD);
        testButtons.addChild(new UIButton(10, 550 + 40 * 5, 1500, 30, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P.\n The sunset looks nice."), test)).setFontFamily(Style.F_FLAVOR);
        testButtons.addChild(new UIButton(10, 550 + 40 * 6, 1500, 30, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P.\n The sunset looks nice."), test)).setFontFamily(Style.F_HEAVY);
        testButtons.addChild(new UIButton(10, 550 + 40 * 7, 1500, 30, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P.\n The sunset looks nice."), test)).setFontFamily(Style.F_SCRIPT);
        testButtons.addChild(new UIButton(10, 550 + 40 * 8, 1500, 30, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P.\n The sunset looks nice."), test)).setFontFamily(Style.F_IMPACT);

        //root.addChild(new UIImage(10, 10, -10, -10, loadImage("data/user/inner-well-being.jpg")));
        root.addChild(new UITextBox(10, 10, -10, -10));

        //Style.font32.font.initInjection();
        ((AdvancedGraphics) g).initializeInjector();

        lastMillis = millis();
    }

    public void draw() {
        background(240, 225, 200);
        int dt = millis() - lastMillis;
        lastMillis = millis();
        //fill(10,10,30,10);
        //rect(0,0,width,height);
//        noFill();
//        pushMatrix();
//        translate(width/2,height/2);
//        rotateY(frameCount/255f);
//        stroke(255);
//        box(200);
//        if(frameCount%63==0){
//            ax=round(random(-1,1));
//            ay=round(random(-1,1));
//            az=round(random(-1,1));
//        }
//        pushMatrix();
//
//        stroke(255,255-5*(frameCount%63));
//        for(int i=1;i<10;i++){
//            rotate((i/10f)*0.005f*(frameCount%63),ax,ay,az);
//            stroke(255,(i%10f)+(i/10f)*(255-5*(frameCount%63)));
//            box(200+(i/1f)*(frameCount%63));
//        }
//
//        popMatrix();
//        popMatrix();
        noFill();
        noStroke();
        strokeWeight(2);
        root.updateFocus(mouseX, mouseY);
        root.updateLogic(dt);
        root.render(this);
    }

    @Override
    public void keyPressed() {
        println(key, (int) key, keyCode);
        if (keyCode == VK_F3)
            DebugConstants.renderUIDebug = !DebugConstants.renderUIDebug;
        else if (keyCode == VK_F12)
            DebugConstants.breakpoint = !DebugConstants.breakpoint;
        else root.handleKeyPress(true, key, keyCode);
    }

    @Override
    public void keyReleased() {
        root.handleKeyPress(false, key, keyCode);
    }

    @Override
    public void mousePressed() {
        root.handleMouseInput(true, mouseButton, mouseX, mouseY);
    }

    @Override
    public void mouseReleased() {
        root.handleMouseInput(false, mouseButton, mouseX, mouseY);
    }

    public static void main(String... args) {
        PApplet.main("GameClient");
    }
}