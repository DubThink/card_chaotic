package UI;

import Globals.Style;
import bpw.Util;
import core.AdvancedApplet;
import Globals.DebugConstants;

import java.util.ArrayList;

public class UIBase {
    int x, y; // relative (to parent)
    int w, h;
    int cx, cy;
    int cw, ch;
    int fontFamily = Style.F_STANDARD;
    boolean focus;
    boolean textFocus = false;
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
                _looseFocus();
            }
            return false;
        }
        boolean wasFocus = focus;
        focus = false;
        // if a child element is in focus
        for (int i = children.size() - 1; i >= 0; i--) {
            DebugConstants.breakpointToggle();
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
            _looseFocus();
        if (!wasFocus && focus)
            _gainFocus();
        return focus;
    }

    protected void _unfocus() {
        if (focus) {
            _looseFocus();
            for (UIBase child : children) {
                child._unfocus();
            }
            focus = false;
        }
    }

    protected void _looseFocus() {
    }

    protected void _gainFocus() {
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
        if (DebugConstants.renderUIDebug) {
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

    public boolean handleKeyPress(boolean down, char key, int keyCode) {
        if (!enabled || !interactable)
            return false;

        if (textFocusTarget != null) {
            textFocusTarget._handleKeyPress(down, key, keyCode);
            return true;
        }

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).handleKeyPress(down, key, keyCode))
                return true;
        }
        return _handleKeyPress(down, key, keyCode);
    }

    protected boolean _handleKeyPress(boolean down, char key, int keyCode) {
        return false;
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

    public UIBase setEnabled(boolean e) {
        enabled = e;
        return this;
    }

    public UIBase setInteractable(boolean e) {
        interactable = e;
        return this;
    }

    protected void claimTextFocus() {
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
                this.textFocusTarget = textFocusTarget;
            }
        }
    }

    public String debugName() {
        return "UIBase[" + this + "]";
    }
}
