package UI;

import Globals.Config;
import Globals.Style;
import bpw.Util;
import core.AdvancedApplet;
import core.AdvancedGraphics;
import processing.core.PConstants;

import static com.jogamp.newt.event.KeyEvent.*;
import static  processing.core.PConstants.*;

import java.util.ArrayList;

public class UITextBox extends UIBase{
    ArrayList<String> lines;
    int cursorPos=0;
    int currentLine=1;// TODO CHANGE
    int lastInputMillis=0; // used to flash cursor
    char lastInputKey = 0;
    int lastInputKeyCode = 0;
    boolean lastInputLive=false;
    int repeatTimer=0; // yes this should be float, but precision of everything is in millis so /shrug

    public UITextBox(int x, int y, int w, int h) {
        super(x, y, w, h);
        this.lines = new ArrayList<>();
        lines.add("abc");
        lines.add("deF");
        lines.add("ghi");
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        Style.getFont(Style.F_STANDARD, Style.FONT_SMALL).apply(p);
        AdvancedGraphics g = p.getAdvGraphics();
        g.noStroke();
        g.fill(textFocus?Style.textColorHover:Style.textColor);
        for(int i=0;i<lines.size();i++){

            //p.textAlign(PConstants.LEFT, PConstants.TOP);
            p.text(lines.get(i),cx,cy+p.textAscent()+i*g.textLeading);
        }
        // draw cursor
        int deltaMillis=p.millis()-lastInputMillis;
        if(textFocus && deltaMillis%(Config.CURSOR_BLINK_RATE*2)<Config.CURSOR_BLINK_RATE){
            float over = p.textWidth(lines.get(currentLine).substring(0,cursorPos));//g.simpleTextWidthImpl(lines.get(currentLine),0,cursorPos);
            g.strokeWeight(1);
            g.stroke(Style.textColorHover);
            p.line(cx+over,cy+currentLine*g.textLeading,cx+over,cy + p.textAscent()+p.textDescent()+currentLine*g.textLeading);
        }
    }

    @Override
    protected boolean _handleMouseInput(boolean down, int button, int x, int y) {
        if(down){
            if(isPointOver(x, y)) {
                claimTextFocus();
                return true;
            }
            else
                disposeTextFocus();
        }
        return false;
    }

    protected int lineLength(int line){
        return lines.get(line).length();
    }

    @Override
    protected void _logicStep(int dt) {
        if(lastInputLive){
            repeatTimer-=dt;
            if(repeatTimer<0){
                repeatTimer+=Config.KEY_REPEAT_RATE;
                _textBoxHandleKey(lastInputKey, lastInputKeyCode);
            }
        }
    }

    @Override
    protected boolean _handleKeyPress(boolean down, char key, int keyCode) {
        if(!textFocus) return false;
        if(key==CODED &&( keyCode==CONTROL || keyCode == SHIFT || keyCode == ALT)){
            // ignore these for now
        } else {
            if (down) {
                boolean res = _textBoxHandleKey(key, keyCode);
                if (res) {
                    lastInputMillis = app.millis();
                    lastInputKey = key;
                    lastInputKeyCode = keyCode;
                    lastInputLive = true;
                    repeatTimer = Config.KEY_REPEAT_DELAY;
                }
                return res;

            } else {
                lastInputLive = false;
            }
        }
        return false;
    }

    protected boolean _textBoxHandleKey(char key, int keyCode) {
        if (key != PConstants.CODED) {

            System.out.println("Uncoded = '" + (int) key + "'(" + key + ")");
            if (Util.isTextChar(key)) {
                lines.set(currentLine, Util.insertChar(lines.get(currentLine), key, cursorPos));
                cursorPos++;
                return true;
            }
            if (key == '\n') {
                String oldLine = lines.get(currentLine);
                lines.set(currentLine, oldLine.substring(0, cursorPos));
                currentLine++;
                lines.add(currentLine, oldLine.substring(cursorPos));
                cursorPos = 0;
                return true;
            }
            if (key == BACKSPACE) {
                if (cursorPos == 0) {
                    if (currentLine > 0) {
                        currentLine--;
                        int newCursor = lineLength(currentLine);
                        lines.set(currentLine, lines.get(currentLine) + lines.get(currentLine + 1));
                        lines.remove(currentLine + 1);
                        cursorPos = newCursor;
                    }
                } else {
                    cursorPos--;
                    lines.set(currentLine, Util.removeChar(lines.get(currentLine), cursorPos));
                }
                return true;
            }
            if (key == DELETE) {
                if (cursorPos == lineLength(currentLine)) {
                    if (currentLine < lines.size()-1) {
                        lines.set(currentLine, lines.get(currentLine) + lines.get(currentLine+1));
                        lines.remove(currentLine + 1);
                    }
                } else {
                    lines.set(currentLine, Util.removeChar(lines.get(currentLine), cursorPos));
                }
                return true;
            }
            if (key == VK_HOME){
                cursorPos=0;
                return true;
            }
            if (key == VK_END){
                cursorPos=lineLength(currentLine);
                return true;
            }
            return false;

        } else {
            System.out.println("Coded = " + keyCode);

            // CODED
            if (keyCode == LEFT) {
                if (cursorPos > 0) {
                    cursorPos--;
                } else if (currentLine > 0) {
                    currentLine--;
                    cursorPos = lineLength(currentLine);
                }
                return true;
            }
            if (keyCode == RIGHT) {
                if (cursorPos < lineLength(currentLine)) {
                    cursorPos++;
                } else if (currentLine < lines.size() - 1) {
                    currentLine++;
                    cursorPos = 0;
                }
                return true;
            }
            if (keyCode == UP) {
                if (currentLine > 0) {
                    currentLine--;
                    cursorPos = Util.min(cursorPos, lineLength(currentLine));
                }
                return true;
            }
            if (keyCode == DOWN) {
                if (currentLine < lines.size() - 1) {
                    currentLine++;
                    cursorPos = Util.min(cursorPos, lineLength(currentLine));
                }
                return true;
            }
        }
        return false;
    }
}
