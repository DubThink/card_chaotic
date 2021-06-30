package Globals;

import Schema.AsyncIOHandler;
import UI.UIBase;
import core.ImageLoader;
import Schema.SchemaEditDefinition;
import network.NetClientInterface;

public class GlobalEnvironment {
    public static ImageLoader imageLoader;
    public static AsyncIOHandler asyncIOHandler;

    public static boolean DEV_MODE;

    public static UIBase uiRoot;

    public static boolean modifierShift;
    public static boolean modifierCtrl;

    public static SchemaEditDefinition.OpenSchema openSchemaHandler;

    public static NetClientInterface netInterface;

    public static boolean isNetReady(){
        return netInterface !=null && netInterface.isReady();
    }

    public static void openSchema(Object o, boolean readOnly){
        openSchemaHandler.open(o,readOnly,openSchemaHandler,300,10);
    }

    public static int simTimeMS() {
        return UIBase.app.millis();
    }

}
