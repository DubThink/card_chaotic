package UI;

import Globals.Style;
import bpw.Util;
import core.AdvancedApplet;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.Collections;

public class UIMultibox extends UIBase {
    ArrayList<String> options;
    public UIUpdateNotify<UIMultibox> selectionChangedAction;
    int selection;
    int focusSelection;
    final int rowH = 30;

    public UIMultibox(int x, int y, int w, int h) {
        this(x, y, w, h,null);
    }

    public UIMultibox(int x, int y, int w, int h, UIUpdateNotify<UIMultibox> action) {
        super(x, y, w, h);
        selectionChangedAction = action;
        options = new ArrayList<>();
    }

    @Override
    public boolean updateFocus(int mouseX, int mouseY) {
        boolean result = super.updateFocus(mouseX, mouseY);
        if(result) {
            updateFocusSelection(mouseY-cy);
        } else {
            focusSelection = -1;
        }
        return result;
    }

    protected void updateFocusSelection(int y){
        focusSelection = y/rowH; // floor
        focusSelection = Util.clamp(focusSelection,0, options.size()-1);
    }

    @Override
    protected void _draw(AdvancedApplet p) {
//        if(state)
//
//        else
//        p.stroke(focus?Style.borderColorHover:Style.borderColor);
        p.stroke(focus?Style.borderColorHover:Style.borderColor);
        p.fill(Style.fillColor);
        p.rect(cx, cy, cw, ch, Style.borderRadius);
        Style.chooseFont(fontFamily, rowH).apply(p);
        p.textAlign(PConstants.LEFT,PConstants.CENTER);
        for(int i = 0; i< options.size(); i++) {
            if(i==selection){
                p.fill(Style.fillColorActive);
                p.rect(cx, i*rowH + cy, cw, rowH, Style.borderRadius);
                p.fill(Style.textColorHover);
            } else if(i==focusSelection) {
                p.fill(Style.textColorHover);
            } else {
                p.fill(Style.textColor);
            }
            p.text(options.get(i), cx + Style.textMargin, i*rowH + cy + rowH/2);
        }
    }

    @Override
    protected boolean _handleMouseInput(boolean down, int button, int x, int y) {
        if(!isPointOver(x,y))
            return false;
        updateFocusSelection(y-cy);
        boolean updated = selection != focusSelection;
        selection=focusSelection;
        if(updated && selectionChangedAction!=null)
            selectionChangedAction.notify(this);
        return true;
    }

    public String getSelection() {
        return options.get(selection);
    }

    public int getSelectionIndex() {
        return selection;
    }

    public int getOptionCount(){
        return options.size();
    }

    public UIMultibox addOption(String string){
        options.add(string);
        return this;
    }

    public UIMultibox setOption(int idx, String string){
        options.set(idx, string);
        return this;
    }

    public void clearOptions(){
        options.clear();
        selection=0;
    }

    public UIMultibox addOptions(String ... strings){
        Collections.addAll(options, strings);
        return this;
    }
}
