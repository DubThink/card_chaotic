package Globals;

public interface DBG {
    static void Warning(String txt) {
        System.err.println(txt);
        Thread.dumpStack();
    }
}
