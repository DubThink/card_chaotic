package UI;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public interface ClipboardUtil {

    public static String getClipboardContents(){
        Clipboard c= Toolkit.getDefaultToolkit().getSystemClipboard();
        String ret = null;
        try {
            ret = (String)c.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void setClipboardContents(String s){
        Clipboard c= Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection ss = new StringSelection(s);
        c.setContents(ss, ss);
    }
}
