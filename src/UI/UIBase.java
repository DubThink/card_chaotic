package UI;

import Globals.Style;
import aew.Util;
import core.AdvancedApplet;
import Globals.Debug;
import Schema.SchemaAllowProtectedEdit;
import Schema.SchemaEditable;
import processing.core.PConstants;

import static Globals.GlobalEnvironment.modifierShift;


import java.util.ArrayList;

@SchemaEditable
@SchemaAllowProtectedEdit
public class UIBase {
    int x, y; // relative (to parent)
    int w, h;
    // calculated values
    int cx, cy;
    int cw, ch;
    int fontFamily = Style.F_STANDARD;
    boolean focus;
    public boolean textFocus = false;
    UIBase textFocusTarget = null;
    UILayer layer;

    public static AdvancedApplet app;

    boolean enabled = true;
    /**
     * disables element being focused
     */
    boolean interactable = true;
    ArrayList<UIBase> children;
    UIBase parent;

    // if this is a nav root, prevent walking outside of it while navigating
    boolean navRoot;

    public UIBase(int x, int y, int w, int h) {
        this(x, y, w, h, UILayer.INTERFACE);
    }

    public UIBase(int x, int y, int w, int h, UILayer layer) {
        this.x = x;
        this.y = y;
        this.cx = x;
        this.cy = y;
        this.w = w;
        this.h = h;
        this.cw = w;
        this.ch = h;
        focus = false;
        children = new ArrayList<>();
        this.layer = layer;
        //setParent(null);
    }

    private void setParent(UIBase base) {
        parent = base;
        _updateCalculatedLayout();
    }

    public boolean updateFocus(int mouseX, int mouseY) {
        if (!enabled) {
            if (focus) {
                focus = false;
                _notifyLooseFocus();
            }
            return false;
        }
        boolean wasFocus = focus;
        focus = false;
        // if a child element is in focus
        for (int i = children.size() - 1; i >= 0; i--) {
            //Debug.breakpointToggle();
            if (!focus) {
                focus = children.get(i).updateFocus(mouseX, mouseY);
            } else {
                children.get(i)._unfocus();
            }
        }
        // overloadable test for hover
        if (!focus)
            focus = interactable && isPointOver(mouseX, mouseY);
        if (wasFocus && !focus)
            _notifyLooseFocus();
        if (!wasFocus && focus)
            _notifyGainFocus();
        return focus;
    }

    protected void _unfocus() {
        if (focus) {
            _notifyLooseFocus();
            for (UIBase child : children) {
                child._unfocus();
            }
            focus = false;
        }
    }

    protected void _notifyLooseFocus() {
    }

    protected void _notifyGainFocus() {
    }

    protected void _updateCalculatedLayout() {
        if (parent == null) {
            if (x < 0 || y < 0)
                throw new RuntimeException("Cannot have a negative (relative) position without a parent.");
            cx = x;
            cy = y;
            if (w <= 0 || h <= 0)
                throw new RuntimeException("Cannot have a negative (relative) size without a parent.");
            cw = w;
            ch = h;
        } else {
            // negative vals are relative to parent
            cx = (x < 0 ? parent.cx + parent.cw : parent.cx) + x;
            cy = (y < 0 ? parent.cy + parent.ch : parent.cy) + y;
            cw = (w <= 0 ? (x < 0 ? -x : parent.cw - x) : 0) + w;
            ch = (h <= 0 ? (y < 0 ? -y : parent.ch - y) : 0) + h;
        }
        for (UIBase element : children) {
            element._updateCalculatedLayout();
        }
    }

    public boolean isPointOver(int px, int py) {
        return Util.in(px, cx, cx + cw) && Util.in(py, cy, cy + ch);
    }

    public void render(AdvancedApplet p) {
        if (!enabled) return;
        p.pushStyle();
        _draw(p);
        p.popStyle();
        if (Debug.renderUIDebug) {
            p.pushStyle();
            p.strokeWeight(1);
            _debugDraw(p);
            p.popStyle();
        }
        for (int i = 0; i < children.size(); i++) {
            children.get(i).render(p);
        }

    }

    protected void _draw(AdvancedApplet p) {
        // style will always be noStroke, noFill, line weight 1
    }

    protected void _debugDraw(AdvancedApplet p) {
        // style will always be noStroke, noFill, line weight 1
        if (focus)
            p.stroke(0, 255, 0);
        else
            p.stroke(255, 0, 0);
        p.rect(cx, cy, cw, ch);
        Style.getFont(Style.F_CODE, Style.FONT_12).apply(p);
        p.textAlign(PConstants.LEFT,PConstants.TOP);
        p.text(""+this,cx+2,cy+2);
    }


    public void updateLogic(int dt) {
        if (!enabled) return;
        for (int i = children.size() - 1; i >= 0; i--) {
            children.get(i).updateLogic(dt);
        }
        _logicStep(dt);
    }

    protected void _logicStep(int dt) {
    }

    public <T extends UIBase> T addChild(T child, UILayer layer) {
        child.layer=layer;
        return addChild(child);
    }

    public <T extends UIBase> T addChild(T child) {
        int i;
        for (i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).layer.compareTo(child.layer) <= 0)
                break;
        }
        children.add(i+1, child);

        ((UIBase) child).setParent(this);
        return child;
    }

    public void removeChild(UIBase child){
        child.cleanup();
        children.remove(child);
    }

    protected void cleanup(){};

    public boolean handleMouseInput(boolean down, int button, int x, int y) {
        if (!enabled || !interactable)
            return false;

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).handleMouseInput(down, button, x, y))
                return true;
        }
        return _handleMouseInput(down, button, x, y);
    }

    protected boolean _handleMouseInput(boolean down, int button, int x, int y) {
        return isPointOver(x,y);
    }

    public boolean handleMouseWheel(int ct, int x, int y) {
        if (!enabled || !interactable)
            return false;

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).handleMouseWheel(ct, x, y))
                return true;
        }
        return _handleMouseWheel(ct, x, y);

    }

    protected boolean _handleMouseWheel(int ct, int x, int y){return false;};

    public boolean handleKeyPress(boolean down, char key, int keyCode) {
        if (!enabled || !interactable)
            return false;
        if (textFocusTarget != null) {
            if(textFocusTarget._handleKeyPress(down, key, keyCode))
                return true;
        }

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).handleKeyPress(down, key, keyCode))
                return true;
        }
        boolean result = _handleKeyPress(down, key, keyCode);

        // only run at root level
        if(parent==null && !result && keyCode == '\t' && down) {
            // didn't eat TAB, navigate instead

            if(modifierShift)
                navigatePrevious();
            else
                navigateNext();

            if(textFocusTarget != null)
                result = true;
        }
        return result;
    }

    protected boolean _handleKeyPress(boolean down, char key, int keyCode) {
        return false;
    }

    public void navigateNext(){
        UIBase result = (textFocusTarget!=null?textFocusTarget:this).searchForward(takesTextInput);
        //System.out.println("next returned "+result);
        if(result!=null)
            result.claimTextFocus();
    }

    public void navigatePrevious(){
        UIBase result = (textFocusTarget!=null?textFocusTarget:this).searchReverse(takesTextInput);
        //System.out.println("prev returned "+result);
        if(result!=null)
            result.claimTextFocus();
    }

    interface TestUIElement{
        boolean test(UIBase base);
    }

    TestUIElement takesTextInput = UIBase::canAcceptTextInput;

    protected UIBase searchForward(TestUIElement test){
        return _ascendingSearchForward(test,0);
    }

    protected UIBase searchReverse(TestUIElement test){
        return _ascendingSearchReverse(test,children.size()-1);
    }

    protected UIBase _ascendingSearchForward(TestUIElement test, int startIdx){
        for (int i = startIdx; i < children.size(); i++) {
            UIBase res = children.get(i)._recursiveSearchForwardDown(test);
            if (res!=null)
                return res;
        }
        if(parent == null || this.navRoot)
            return null;
        // continue the search after this one in the parent
        return parent._ascendingSearchForward(test,parent.children.indexOf(this)+1);
    }

    protected UIBase _ascendingSearchReverse(TestUIElement test, int startIdx){
        for (int i = startIdx; i >= 0; i--) {
            UIBase res = children.get(i)._recursiveSearchReverseDown(test);
            if (res!=null)
                return res;
        }
        if(parent == null || this.navRoot)
            return null;
        // continue the search after this one in the parent
        return parent._ascendingSearchReverse(test,parent.children.indexOf(this)-1);
    }

    protected UIBase _recursiveSearchForwardDown(TestUIElement test){
        // base case
        if(test.test(this))
            return this;
        // recursive case
        for(UIBase c: children) {
            UIBase res = c._recursiveSearchForwardDown(test);
            if(res != null)
                return res;
        }
        return null;
    }

    protected UIBase _recursiveSearchReverseDown(TestUIElement test){

        // recursive case
        for (int i = children.size()-1; i >= 0 ; i--) {
            UIBase res = children.get(i)._recursiveSearchReverseDown(test);
            if (res != null)
                return res;
        }

        // base case
        if(test.test(this))
            return this;

        return null;
    }

    public int getCalculatedX() {
        return cx;
    }

    public int getCalculatedY() {
        return cy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return cw;
    }

    public int getHeight() {
        return ch;
    }

    public UIBase setPos(int x, int y) {
        this.x = x;
        this.y = y;
        _updateCalculatedLayout();
        return this;
    }

    public UIBase setSize(int w, int h) {
        this.w = w;
        this.h = h;
        _updateCalculatedLayout();
        return this;
    }

    public UIBase setFontFamily(int fontFamily) {
        this.fontFamily = fontFamily;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Checks if a given element is fully enabled (i.e. all containing elements are enabled)
     * Some increased perf cost; don't use in tight code.
     * @return
     */
    public boolean isLineageEnabled() {
        UIBase p=parent;
        while (p!=null) {
            if (!p.enabled)
                return false;
            p = p.parent;
        }
        return true;
    }


    public UIBase setEnabled(boolean e) {
        enabled = e;
        return this;
    }

    public UIBase setInteractable(boolean e) {
        interactable = e;
        return this;
    }

    public UIBase setNavRoot(boolean isNavRoot){
        navRoot = isNavRoot;
        return this;
    }

    public UIBase findNavRoot(){
        UIBase result=this;
        while (!result.navRoot&&result.parent!=null)
            result=result.parent;
        return result;
    }

    public UIBase findRoot(){
        UIBase result=this;
        while (result.parent!=null)
            result=result.parent;
        return result;
    }

    public boolean canAcceptTextInput(){
        return false;
    }

    protected void claimTextFocus() {
        // Assert.bool(canAcceptTextInput()); // we allow things to take text focus as a placeholder for navigation reasons
        if (textFocus) return;
        propagateClaimTextFocus(this);
    }

    protected void disposeTextFocus() {
        if (!textFocus) return;
        propagateClaimTextFocus(null);
    }

    protected void propagateClaimTextFocus(UIBase textFocusTarget) {
        if (parent != null) {
            // propagate
            parent.propagateClaimTextFocus(textFocusTarget);
            this.textFocusTarget = textFocusTarget;
        } else {
            // we've hit root; shift focus
            if (this.textFocusTarget != null)
                this.textFocusTarget.textFocus = false;
            if (textFocusTarget != null) {
                // new target
                textFocusTarget.textFocus = true;
            }
            this.textFocusTarget = textFocusTarget;
        }
    }

    protected UITooltip toolTip;

    public UIBase addTooltip(String s) {
        if(toolTip!=null){
            removeChild(toolTip);
        }
        toolTip = addChild(new UITooltip(s));
        return this;
    }

    public String debugName() {
        return "UIBase[" + this + "]";
    }
}
