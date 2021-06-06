package Globals;

import Schema.AsyncIOHandler;
import UI.UIBase;
import core.AdvancedApplet;
import core.ImageLoader;

public class GlobalEnvironment {
    public static ImageLoader imageLoader;
    public static AsyncIOHandler asyncIOHandler;

    public static boolean DEV_MODE;

    public static UIBase uiRoot;

    public static boolean modifierShift;
    public static boolean modifierCtrl;
}
