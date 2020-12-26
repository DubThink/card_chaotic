package Globals;

public class DebugConstants {
    public static boolean renderUIDebug = false;
    public static boolean breakpoint = false;

    public static void breakpointToggle(){
        if(breakpoint)
        {
            System.out.println("breakpoint");
        }
    }
}
