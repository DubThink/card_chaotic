package Globals;

import Debug.PerfView;

public class Debug {
    public static boolean renderUIDebug = false;
    public static boolean renderPerfView = false;
//    public static boolean printUIDebug = false;
    public static boolean breakpoint = false;

    public static PerfView perfView;

    private final static long startNS;

    static {
        startNS=System.nanoTime();
    }

    static {
        perfView = new PerfView();
    }
    public static void breakpointToggle(){
        if(breakpoint)
        {
            System.out.println("breakpoint");
        }
    }

    public static float perfTimeMS(){
        return (System.nanoTime()-startNS)/1000000f;
    }
}
