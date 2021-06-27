package UI;

import Globals.Config;
import Globals.Style;
import aew.Util;
import core.AdvancedApplet;
import core.AdvancedGraphics;
import processing.core.PConstants;

import static com.jogamp.newt.event.KeyEvent.*;
import static  processing.core.PConstants.*;

import java.util.ArrayList;
import java.util.Collections;

public class UITextBox extends UIScrollable {
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

    // TODO switch to using interactable
    boolean editable=true;

    public UIUpdateNotify<UITextBox> textUpdated;
    public UIUpdateNotify<UITextBox> textSubmitted;

    public UITextBox(int x, int y, int w, int h, boolean fieldBox) {
        super(x, y, w, h);
        this.lines = new ArrayList<>();
        this.fieldBox = fieldBox;
        if(fieldBox)
            textSize=Style.FONT_MEDIUM;
        lines.add("");
    }

    public UITextBox setEditable(boolean editable) {
        if(!editable && this.editable && textFocus) {
            UIBase navRoot = findRoot();
            navRoot.navigatePrevious();
            if(textFocus) // if we didn't successfully hand off focus
                navRoot.navigateNext();
            if(textFocus){
                disposeTextFocus();
                findNavRoot().claimTextFocus(); // just make the nav root claim focus to keep it within
            }
        }
        this.editable = editable;
        return this;
    }

    @Override
    protected void _debugDraw(AdvancedApplet p) {
        super._debugDraw(p);
        p.text(lines.get(0).length(),cx+10,cy+10);
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        AdvancedGraphics g = p.getAdvGraphics();
        g.pushMatrix();

        g.translate(cx,cy);
        p.stroke(focus&&editable?Style.borderColorHover:Style.borderColor);
        p.fill(editable?Style.fillColorInputField:Style.fillColor);
        p.rect(0, 0, cw, ch,Style.borderRadius);

        applyTextSettings();
        g.noStroke();
        g.fill(textFocus&&editable?Style.textColorHover:Style.textColor);

        refreshScrollPos();
        int offset = getScrollPosition();
        int screenCapacity = getScreenCapacity();
        if(fieldBox) {
            g.translate(Style.textMargin, 0);
            p.textAlign(LEFT, CENTER);
            g.textLineClipped(lines.get(0), 0,ch/2f, cw-2*Style.textMargin,true);
            g.translate(0, Style.textMargin);
        } else {
            g.translate(Style.textMargin, Style.textMargin);
            for (int drawline = 0; drawline < Util.min(screenCapacity,lines.size()); drawline++) {
                int line = drawline+offset;
                //p.textAlign(PConstants.LEFT, PConstants.TOP);
                g.textLineClipped(lines.get(line), 0, p.textAscent() + drawline * g.textLeading, cw-2*Style.textMargin);
            }
        }
        // draw cursor
        int deltaMillis=p.millis()-lastInputMillis;
        if(textFocus &&
                editable &&
                currentLine>=offset && currentLine<offset+screenCapacity &&
                deltaMillis%(Config.CURSOR_BLINK_RATE*2)<Config.CURSOR_BLINK_RATE){
            float over = p.textWidth(lines.get(currentLine).substring(0,cursorPos));//g.simpleTextWidthImpl(lines.get(currentLine),0,cursorPos);
            g.noStroke();
            g.fill(Style.textColorHover);
            int renderLine = currentLine-offset;
            p.rect(over,renderLine*g.textLeading, 2, p.textAscent()+p.textDescent());
        }
        lastLeading = g.textLeading;

        g.popMatrix();
        renderScrollable(p);
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
        if(down&&editable){
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
        currentLine+=getScrollPosition();
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
        if(!textFocus||!editable) return false;
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
        ensureCursorOnScreen();
        if(textUpdated != null && ret)
            textUpdated.notify(this);
        if(ret)
            lastInputMillis = app.millis();
        return ret;
    }

    @Override
    protected void _notifyGainFocus() {
        lastInputMillis = app.millis();
    }

    @Override
    protected void claimTextFocus() {
        super.claimTextFocus();
        lastInputMillis = app.millis();
    }

    @Override
    public boolean canAcceptTextInput() {
        return isLineageEnabled() && editable;
    }

    protected boolean _textBoxHandleKey(char key, int keyCode) {
        if (key != PConstants.CODED) {
//            System.out.println("Uncoded = '" + (int) key + "'(" + key + ")");

            if (app.keyControlDown){
                if (keyCode=='c' || keyCode=='C') {
                    ClipboardUtil.setClipboardContents(getText());
                    return true;
                }
                if (keyCode=='x' || keyCode=='X') {
                    ClipboardUtil.setClipboardContents(getText());
                    clearText();
                    refreshScrollPos();
                    if(textUpdated!=null)
                        textUpdated.notify(this);
                    return true;
                }
                if (keyCode=='v' || keyCode=='V') {
                    String s = ClipboardUtil.getClipboardContents();
                    refreshScrollPos();
                    if(s!=null){
                        setText(s);
                        if(textUpdated!=null)
                            textUpdated.notify(this);
                    }
                    return true;
                }
            }
            // processing can't render tabs, and besides we use them for navigation
            if (key!='\t' && Util.isTextChar(key) && !app.keyControlDown) {
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
        setScrollPosition(0);
        return this;
    }

    public UITextBox setText(String s){
        String[] newLines = s.split("\n");
        if(fieldBox){
            lines.set(0, newLines[0]);
            cursorPos=Util.min(newLines[0].length(),cursorPos);
        } else {
            lines.clear();
            Collections.addAll(lines, newLines);
            currentLine=Util.min(currentLine, newLines.length-1);
            cursorPos=Util.min(newLines[currentLine].length(),cursorPos);
        }
        ensureCursorOnScreen();
        return this;
    }

    @Override
    protected int getScrollableLineCount() {
        return lines.size();
    }

    @Override
    protected float getScrollableLineHeight() {
        applyTextSettings();
        return app.getAdvGraphics().textLeading;
    }

    protected void ensureCursorOnScreen() {
        if(currentLine<getScrollPosition())
            setScrollPosition(currentLine);
        else if(currentLine>=getScrollPosition()+getScreenCapacity())
            setScrollPosition(currentLine+getScreenCapacity()-1);
    }
}
