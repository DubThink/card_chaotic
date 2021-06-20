package core;

import UI.Action;
import processing.core.PConstants;

public class MouseInputHandler {
    static Action lAction,rAction,mAction;

    public static void registerMouseUse(int button, Action a){
        mouseReleased(button);
        if(button == PConstants.LEFT)
            lAction = a;
        else if(button == PConstants.RIGHT)
            rAction = a;
        else if(button == PConstants.CENTER)
            mAction = a;

    }

    public static void mouseReleased(int button) {
        if (button == PConstants.LEFT && lAction != null) {
            lAction.action();
            lAction = null;
        } else if (button == PConstants.CENTER && mAction != null) {
            mAction.action();
            mAction = null;
        } else if (button == PConstants.RIGHT && rAction != null) {
            rAction.action();
            rAction = null;
        }
    }
}
