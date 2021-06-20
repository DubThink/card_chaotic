package UI;

import core.AdvancedApplet;

public class UICheckbox extends UIButton {

    public UIUpdateNotify<UICheckbox> notify;

    public UICheckbox(int x, int y, int w) {
        super(x, y, w, w, AdvancedApplet.hyperText("/]"));
        toggle=true;
        onAction = this::actionOn;
        offAction = this::actionOff;
    }

    private void actionOn(){
        text = AdvancedApplet.hyperText("/[");
        if(notify!=null){
            notify.notify(this);
        }
    }

    private void actionOff(){
        text = AdvancedApplet.hyperText("/]");
        if(notify!=null){
            notify.notify(this);
        }
    }

    public UICheckbox set(boolean val){
        boolean updated = val != state;
        state = val;
        if(updated) {
            if (val)
                actionOn();
            else
                actionOff();
        }
        return this;
    }

    public boolean get(){return state;}

    public UICheckbox setNotify(UIUpdateNotify<UICheckbox> notify) {
        this.notify = notify;
        return this;
    }
}
