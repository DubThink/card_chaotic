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
import java.util.Collections;

public class UITextBox extends UIBase {
    ArrayList<String> lines;
    int cursorPos=0;
    int currentLine=0;// TODO CHANGE
    int lastInputMillis=0; // used to flash cursor
    char lastInputKey = 0;
    int lastInputKeyCode = 0;
    boolean lastInputLive=false;
    int repeatTimer=0; // yes this should be float, but precision of everything is in millis so /shrug

    float lastLeading=10;

    int textSize=Style.FONT_SMALL;

    boolean fieldBox;

    public UIUpdateNotify<UITextBox> textUpdated;
    public UIUpdateNotify<UITextBox> textSubmitted;

    public UITextBox(int x, int y, int w, int h, boolean fieldBox) {
        super(x, y, w, h);
        this.lines = new ArrayList<>();
        this.fieldBox = fieldBox;
        lines.add("");
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        AdvancedGraphics g = p.getAdvGraphics();
        g.pushMatrix();

        g.translate(cx,cy);
        p.stroke(focus?Style.borderColorHover:Style.borderColor);
        p.fill(Style.fillColorInputField);
        p.rect(0, 0, cw, ch,Style.borderRadius);

        g.translate(Style.textMargin, Style.textMargin);
        applyTextSettings();
        g.noStroke();
        g.fill(textFocus?Style.textColorHover:Style.textColor);

        for(int i=0;i<lines.size();i++){

            //p.textAlign(PConstants.LEFT, PConstants.TOP);
            p.text(lines.get(i),0,p.textAscent()+i*g.textLeading);
        }
        // draw cursor
        int deltaMillis=p.millis()-lastInputMillis;
        if(textFocus && deltaMillis%(Config.CURSOR_BLINK_RATE*2)<Config.CURSOR_BLINK_RATE){
            float over = p.textWidth(lines.get(currentLine).substring(0,cursorPos));//g.simpleTextWidthImpl(lines.get(currentLine),0,cursorPos);
            g.strokeWeight(2);
            g.stroke(Style.textColorHover);
            p.line(over,currentLine*g.textLeading,over,p.textAscent()+p.textDescent()+currentLine*g.textLeading);
        }
        lastLeading = g.textLeading;

        g.popMatrix();
    }

    protected void applyTextSettings(){
        Style.getFont(fontFamily, textSize).apply(app);
    }

    public UITextBox setFontSize(int size){
        textSize=size;
        return this;
    }

    public UITextBox setTextUpdatedCallback(UIUpdateNotify<UITextBox> callback){
        textUpdated=callback;
        return this;
    }

    public UITextBox setTextSubmittedCallback(UIUpdateNotify<UITextBox> callback){
        textSubmitted=callback;
        return this;
    }

    @Override
    protected boolean _handleMouseInput(boolean down, int button, int x, int y) {
        if(down){
            if(isPointOver(x, y)) {
                claimTextFocus();
                if(textFocus)
                    positionCursorAtCoord(x,y);
                return true;
            }
            else
                disposeTextFocus();
        }
        return false;
    }

    protected void positionCursorAtCoord(int x, int y){
        // calc line
        y -= cy;
        y -= Style.textMargin;

        x -= cx;
        x -= Style.textMargin;

        currentLine = Util.clamp((int)Math.floor(y/lastLeading), 0, lines.size()-1);
        applyTextSettings();
        char[] current = lines.get(currentLine).toCharArray();
        float lastw = -100000;
        float nw = 0;
        cursorPos = -1;
        for(int i=0; i<current.length;i++){
            nw=app.textWidth(current, 0,i);
            if(nw>x){
                cursorPos=i;
                break;
            }
            lastw=nw;
        }
        if(cursorPos==-1)
            cursorPos=lineLength(currentLine);
        else{
            if(Util.abs(nw-x)>Util.abs(lastw-x))
                cursorPos--;
        }

    }

    protected int lineLength(int line){
        return lines.get(line).length();
    }

    protected char charBeforeCursor(){
        if(cursorPos==0){
            if(currentLine==0)
                return 0;
            return '\n';
//            int lastLineLength = lines.get(currentLine-1).length();
//            return lastLineLength>0 ? lines.get(currentLine-1).charAt(lastLineLength-1) : 0;
        }
        return lines.get(currentLine).charAt(cursorPos-1);
    }

    protected char charAfterCursor(){
        if(cursorPos==lineLength(currentLine)){
            if(currentLine==lines.size()-1)
                return 0;
            return '\n';
//            int nextLineLength = lines.get(currentLine+1).length();
//            return nextLineLength>0 ? lines.get(currentLine-1).charAt(0) : 0;
        }
        return lines.get(currentLine).charAt(cursorPos);
    }

    @Override
    protected void _logicStep(int dt) {
        if(lastInputLive){
            repeatTimer-=dt;
            if(repeatTimer<0){
                repeatTimer+=Config.KEY_REPEAT_RATE;
                _textBoxHandleKeyAndUpdate(lastInputKey, lastInputKeyCode);
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
                boolean res = _textBoxHandleKeyAndUpdate(key, keyCode);
                if (res) {
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
    protected boolean _textBoxHandleKeyAndUpdate(char key, int keyCode) {
        boolean ret = _textBoxHandleKey(key, keyCode);
        if(textUpdated != null && ret)
            textUpdated.notify(this);
        if(ret)
            lastInputMillis = app.millis();
        return ret;
    }

    protected boolean _textBoxHandleKey(char key, int keyCode) {
        if (key != PConstants.CODED) {
//            System.out.println("Uncoded = '" + (int) key + "'(" + key + ")");
            if (Util.isTextChar(key)) {
                lines.set(currentLine, Util.insertChar(lines.get(currentLine), key, cursorPos));
                cursorPos++;
                return true;
            }
            if (key == '\n') {
                if(fieldBox){
                    if(textSubmitted != null)
                        textSubmitted.notify(this);
                    return true;
                }
                String oldLine = lines.get(currentLine);
                lines.set(currentLine, oldLine.substring(0, cursorPos));
                currentLine++;
                lines.add(currentLine, oldLine.substring(cursorPos));
                cursorPos = 0;
                return true;
            }
            if (key == BACKSPACE) {
                do
                    _actionBackspace();
                while(app.keyControlDown && Util.isExtendedAlphanumChar(charBeforeCursor()));
                return true;
            }
            if (key == DELETE) {
                do
                    _actionDelete();
                while(app.keyControlDown && Util.isExtendedAlphanumChar(charAfterCursor()));
                return true;
            }
            if(key==0) {
                // idk why these keys use key=0 but such is life
                if (keyCode == VK_HOME) {
                    if(app.keyControlDown)
                        currentLine=0;
                    cursorPos = 0;
                    return true;
                }
                if (keyCode == VK_END) {
                    if(app.keyControlDown)
                        currentLine=lines.size()-1;
                    cursorPos = lineLength(currentLine);
                    return true;
                }
            }
            return false;

        } else {
//            System.out.println("Coded = " + keyCode);

            // CODED
            if (keyCode == LEFT) {
                do
                    _actionNavLeft();
                while(app.keyControlDown && Util.isExtendedAlphanumChar(charBeforeCursor()));
                return true;
            }
            if (keyCode == RIGHT) {
                do
                    _actionNavRight();
                while(app.keyControlDown && Util.isExtendedAlphanumChar(charAfterCursor()));
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

    private void _actionDelete() {
        if (cursorPos == lineLength(currentLine)) {
            if (currentLine < lines.size()-1) {
                lines.set(currentLine, lines.get(currentLine) + lines.get(currentLine+1));
                lines.remove(currentLine + 1);
            }
        } else {
            lines.set(currentLine, Util.removeChar(lines.get(currentLine), cursorPos));
        }
    }

    protected void _actionBackspace(){
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
    }

    protected void _actionNavLeft(){
        if (cursorPos > 0) {
            cursorPos--;
        } else if (currentLine > 0) {
            currentLine--;
            cursorPos = lineLength(currentLine);
        }
    }

    protected void _actionNavRight(){
        if (cursorPos < lineLength(currentLine)) {
            cursorPos++;
        } else if (currentLine < lines.size() - 1) {
            currentLine++;
            cursorPos = 0;
        }
    }

    public String getText(){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i< lines.size();i++){
            builder.append(lines.get(i));
            if(i< lines.size()-1)
                builder.append('\n');
        }
        return builder.toString();
    }

    public UITextBox clearText(){
        lines.clear();
        lines.add("");
        cursorPos=0;
        currentLine=0;
        return this;
    }

    public UITextBox setText(String s){
        String[] newLines = s.split("\n");
        if(fieldBox){
            lines.set(0, newLines[0]);
        } else {
            lines.clear();
            Collections.addAll(lines, newLines);
        }
        return this;
    }
}
